package ru.javaops.masterjava.export;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * gkislin
 * 14.10.2016
 */
public class UserExport {

    private static final String OK = "OK";
    private static final String ALREADY_ADDED = "User already in DB";

    private static final String INTERRUPTED_BY_FAULTS_NUMBER = "+++ Interrupted by faults number";
    private static final String INTERRUPTED_BY_TIMEOUT = "+++ Interrupted by timeout";
    private static final String INTERRUPTED_EXCEPTION = "+++ InterruptedException";

    private final ExecutorService mailExecutor = Executors.newFixedThreadPool(4);

    public GroupResult process(final InputStream is, final Integer chunkSize) throws XMLStreamException, SQLException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        UserDao dao = DBIProvider.getDao(UserDao.class);
        List<User> users = new ArrayList<>();
        List<User> temp = new ArrayList<>();
        final CompletionService<List<UserResult>> completionService = new ExecutorCompletionService<>(mailExecutor);
        List<Future<List<UserResult>>> futures = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final User user = new User(fullName, email, flag);
            temp.add(user);
            if (temp.size() % chunkSize == 0) {
                List<User> finalList = new ArrayList<>(temp);
                futures.add(completionService.submit(() -> addList(dao, finalList)));
                temp = new ArrayList<>();
            }
        }
        if (!temp.isEmpty()) {
            List<User> finalList = new ArrayList<>(temp);
            futures.add(completionService.submit(() -> addList(dao, finalList)));
        }

        return  new Callable<GroupResult>() {
            private int success = 0;
            private List<UserResult> failed = new ArrayList<>();

            @Override
            public GroupResult call() {
                while (!futures.isEmpty()) {
                    try {
                        Future<List<UserResult>> future = completionService.poll(10, TimeUnit.SECONDS);
                        if (future == null) {
                            return cancelWithFail(INTERRUPTED_BY_TIMEOUT);
                        }
                        futures.remove(future);
                        List<UserResult> userResultList = future.get();
                        for (UserResult userResult : userResultList) {
                            if (userResult.isOk()) {
                                success++;
                            } else {
                                failed.add(userResult);
                                if (failed.size() >= 6) {
                                    return cancelWithFail(INTERRUPTED_BY_FAULTS_NUMBER);
                                }
                            }
                        }

                    } catch (ExecutionException e) {
                        return cancelWithFail(e.getCause().toString());
                    } catch (InterruptedException e) {
                        return cancelWithFail(INTERRUPTED_EXCEPTION);
                    }
                }
                return new GroupResult(success, failed, null);
            }

            private GroupResult cancelWithFail(String cause) {
                futures.forEach(f -> f.cancel(true));
                return new GroupResult(success, failed, cause);
            }
        }.call();
    }

    public List<UserResult> addList(UserDao dao, List<User> users) {
        List<User> failedUsers = dao.insertAll(users);
        List<UserResult> failedUsersResult = failedUsers.stream().map(u -> new UserResult(u, ALREADY_ADDED)).collect(Collectors.toList());
        users.removeAll(failedUsers);
        List<UserResult> collect = users.stream().map(u -> new UserResult(u, OK)).collect(Collectors.toList());
        collect.addAll(failedUsersResult);
        return collect;
    }

    public static class UserResult {
        private final User user;
        private final String result;

        public UserResult(User user, String result) {
            this.user = user;
            this.result = result;
        }

        @Override
        public String toString() {
            return '(' + user.getEmail() + ',' + result + ')';
        }

        public boolean isOk() {
            return OK.equals(result);
        }

        public User getUser() {
            return user;
        }

        public String getResult() {
            return result;
        }
    }

    public static class GroupResult {
        private final int success; // number of successfully added email
        private final List<UserResult> failed; // failed to add user with causes
        private final String failedCause;  // global fail cause

        public GroupResult(int success, List<UserResult> failed, String failedCause) {
            this.success = success;
            this.failed = failed;
            this.failedCause = failedCause;
        }

        @Override
        public String toString() {
            return "Success: " + success + '\n' +
                    "Failed: " + failed.toString() + '\n' +
                    (failedCause == null ? "" : "Failed cause" + failedCause);
        }

        public int getSuccess() {
            return success;
        }

        public List<UserResult> getFailed() {
            return failed;
        }

        public String getFailedCause() {
            return failedCause;
        }
    }
}
