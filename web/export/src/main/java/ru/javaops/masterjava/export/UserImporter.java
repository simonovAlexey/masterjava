package ru.javaops.masterjava.export;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.export.PayloadImporter.FailedEmail;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.*;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * gkislin
 * 14.10.2016
 */
@Slf4j
public class UserImporter {

    private static final int NUMBER_THREADS = 4;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
    private final UserDao userDao = DBIProvider.getDao(UserDao.class);
    private final UserGroupDao userGroupDao = DBIProvider.getDao(UserGroupDao.class);


    @Value
    public static class UserGroupTO {
        public User user;
        public List<Integer> groups;
    }

    public List<FailedEmail> process(StaxStreamProcessor processor, Map<String, City> cities, Map<String, Group> groupsM, int chunkSize) throws XMLStreamException {
        log.info("Start proseccing with chunkSize=" + chunkSize);

        return new Callable<List<FailedEmail>>() {
            class ChunkFuture {
                String emailRange;
                Future<List<String>> future;

                public ChunkFuture(List<User> chunk, Future<List<String>> future) {
                    this.future = future;
                    this.emailRange = chunk.get(0).getEmail();
                    if (chunk.size() > 1) {
                        this.emailRange += '-' + chunk.get(chunk.size() - 1).getEmail();
                    }
                }
            }

            @Override
            public List<FailedEmail> call() throws XMLStreamException {
                List<ChunkFuture> futures = new ArrayList<>();

                int id = userDao.getSeqAndSkip(chunkSize);
                List<UserGroupTO> chunk = new ArrayList<>(chunkSize);
                List<FailedEmail> failed = new ArrayList<>();

                while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    final String groupRefs = processor.getAttribute("groupRefs");
                    final String email = processor.getAttribute("email");
                    String cityRef = processor.getAttribute("city");
                    City city = cities.get(cityRef);
                    if (city == null) {
                        failed.add(new FailedEmail(email, "City '" + cityRef + "' is not present in DB"));
                    } else {

                        final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
                        final String fullName = processor.getReader().getElementText();
                        final User user = new User(id++, fullName, email, flag, city.getId());
                        List<Integer> groupsId = null;
                        if (groupRefs != null) {
                            groupsId = Arrays.asList(groupRefs.split(" ")).stream()
                                    .map(g -> groupsM.get(g).getId())
                                    .collect(Collectors.toList());
                        }
                        chunk.add(new UserGroupTO(user, groupsId));
                        if (chunk.size() == chunkSize) {
                            futures.add(submit(chunk));
                            chunk = new ArrayList<>(chunkSize);
                            id = userDao.getSeqAndSkip(chunkSize);
                        }
                    }
                }

                if (!chunk.isEmpty()) {
                    futures.add(submit(chunk));
                }

                futures.forEach(cf -> {
                    try {
                        failed.addAll(StreamEx.of(cf.future.get()).map(email -> new FailedEmail(email, "already present")).toList());
                        log.info(cf.emailRange + " successfully executed");
                    } catch (Exception e) {
                        log.error(cf.emailRange + " failed", e);
                        failed.add(new FailedEmail(cf.emailRange, e.toString()));
                    }
                });
                return failed;
            }

            private ChunkFuture submit(List<UserGroupTO> chunk) {
                val users = chunk.stream().map(ug -> ug.user).collect(Collectors.toList());
                ChunkFuture chunkFuture = new ChunkFuture(users,
                        executorService.submit(() -> insertAndGetConflictEmailsWithGroupProcess(chunk, users))
                );
                log.info("Submit " + chunkFuture.emailRange);
                return chunkFuture;
            }
        }.call();
    }

    private List<String> insertAndGetConflictEmailsWithGroupProcess(List<UserGroupTO> chunk, List<User> users) {
        List<String> failedEmails = userDao.insertAndGetConflictEmails(users);
        List<UserGroup> processGroup = new ArrayList<>();
        for (UserGroupTO ug : chunk) {
            if (ug.getGroups()==null) {
                log.error("----------------------Find null Group");
                continue;
            }
            if (!failedEmails.contains(ug.user.getEmail())) {
                List<UserGroup> collect = ug.getGroups().stream().
                        map(g -> new UserGroup(ug.user.getId(), g)).
                        collect(Collectors.toList());
                processGroup.addAll(collect);
            }
        }
        userGroupDao.insertBatch(processGroup);

        return failedEmails;
    }
}