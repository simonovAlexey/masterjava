package ru.javaops.masterjava.webapp;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.FileUpload;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.web.WebStateException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;

@WebServlet("/send")
@Slf4j
public class SendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String users = req.getParameter("users");
        String subject = req.getParameter("subject");
        String body = req.getParameter("body");
        String fileName = req.getParameter("fileName");
        String path = System.getProperty("java.io.tmpdir");
        File file = new File(path + fileName);

        Part filePart = req.getPart("fileToUpload");
        if (filePart != null) {
            try (InputStream is = filePart.getInputStream();
                 OutputStream out = new FileOutputStream(file)) {

                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }
        DataHandler dataHandler = new DataHandler(new FileDataSource(file));
        String groupResult;
        try {
            groupResult = MailWSClient.sendBulk(MailWSClient.split(users), subject, body,
                    new FileUpload(fileName,dataHandler)).toString();
        } catch (WebStateException e) {
            groupResult = e.toString();
        }
        resp.getWriter().write(groupResult);
    }
}
