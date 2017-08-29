package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javax.xml.ws.Endpoint;
import java.util.List;

import static ru.javaops.masterjava.config.Configs.getConfig;

/**
 * gkislin
 * 15.11.2016
 */
@Slf4j
public class MailSender {
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
            email.send();

        } catch (EmailException e) {
            log.error("error sending email, reason: {}", e.getMessage());
        }

    }
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/mail/mailService", new MailServiceImpl());
    }
}
