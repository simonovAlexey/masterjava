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
import java.util.Collections;
import java.util.List;

/**
 * gkislin
 * 14.10.2016
 */
public class UserExport {


    public List<User> process(final InputStream is, final Integer chunkSize) throws XMLStreamException, SQLException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        UserDao dao = DBIProvider.getDao(UserDao.class);
        List<User> users = new ArrayList<>();
        List<User> temp = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final User user = new User(fullName, email, flag);
            temp.add(user);
            if (temp.size() % chunkSize == 0) {
                dao.insertAll(Collections.unmodifiableList(temp));
                users.addAll(temp);
                temp = new ArrayList<>();
            }
        }
        return users;
    }
}
