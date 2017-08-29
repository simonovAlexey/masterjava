package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.dao.EmailResultDao;
import ru.javaops.masterjava.service.model.EmailResult;

import javax.mail.internet.InternetAddress;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.javaops.masterjava.config.Configs.getConfig;

/**
 * gkislin
 * 15.11.2016
 */
@Slf4j
public class MailSender {
    /*static {

        Config db = Configs.getConfig("persist.conf", "db");

        DBIProvider.init(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("PostgreSQL driver not found", e);
            }
            return DriverManager.getConnection(db.getString("url"), db.getString("user"), db.getString("password"));
        });
    }*/

    private static final EmailResultDao resultDao = DBIProvider.getDao(EmailResultDao.class);

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));

        Config conf = getConfig("mail.conf");
        String emailAdress = conf.getString("mail.username");
        String password = conf.getString("mail.password");

        Email email = new SimpleEmail();
        email.setHostName(conf.getString("mail.host"));
        email.setSmtpPort(conf.getInt("mail.port"));
        email.setAuthenticator(new DefaultAuthenticator(emailAdress, password));
        email.setSSLOnConnect(conf.getBoolean("mail.useSSL"));
        email.setStartTLSEnabled(conf.getBoolean("mail.useTLS"));
        email.setDebug(conf.getBoolean("mail.debug"));
        email.setSubject(subject);
        try {
            email.setFrom(emailAdress, conf.getString("mail.fromName"));
            email.setMsg(body);

            for (final Addressee addressee : to) {
                email.addTo(addressee.getEmail(), addressee.getName());
            }
            for (final Addressee addressee : cc) {
                email.addCc(addressee.getEmail(), addressee.getName());
            }
            String send = email.send();

            Date sentDate = email.getSentDate();
            List<InternetAddress> sendAddresses = email.getToAddresses();
            sendAddresses.addAll(email.getCcAddresses());

            List<EmailResult> collect = sendAddresses.stream().
                    map(a -> new EmailResult(a.getAddress(), send, sentDate)).collect(Collectors.toList());
            resultDao.insertBatch(collect);

        } catch (EmailException e) {
            log.error("error sending email, reason: {}", e.getMessage());
        }

    }
}
