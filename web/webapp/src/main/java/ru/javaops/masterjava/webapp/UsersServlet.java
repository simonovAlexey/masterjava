package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.MailWSClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;
@Slf4j
@WebServlet("")
public class UsersServlet extends HttpServlet {
    private UserDao userDao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("users", userDao.getWithLimit(20)));
        engine.process("users", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] chekedUsers = req.getParameterValues("chekedUser");
        String subject = req.getParameter("subject");
        String body = req.getParameter("body");
        Set<Addressee> collect = StreamEx.of(chekedUsers).map(Addressee::new).collect(Collectors.toSet());
        try {
            MailWSClient.sendMail(collect, Collections.emptySet(), subject, body);
            log.info("Send mail to '" + collect + "' subject '" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));

        } catch (Exception e) {
            log.error("Error sending emails: "+e.getMessage());
        }
        resp.sendRedirect("/webapp");
    }


}
