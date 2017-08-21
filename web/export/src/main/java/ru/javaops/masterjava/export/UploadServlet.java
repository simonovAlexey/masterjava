package ru.javaops.masterjava.export;

import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

import static ru.javaops.masterjava.export.ThymeleafListener.engine;

@WebServlet("/")
@MultipartConfig
public class UploadServlet extends HttpServlet {

    private final UserExport userExport = new UserExport();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        engine.process("export", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        Integer chunkSize = null;
        try {
            chunkSize = Integer.parseInt(req.getParameter("chunkSize"));
//            http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
            Part filePart = req.getPart("fileToUpload");
            try (InputStream is = filePart.getInputStream()) {
                UserExport.GroupResult process = userExport.process(is, chunkSize);

                webContext.setVariable("users", process);
                engine.process("result", webContext, resp.getWriter());
            }
        } catch (Exception e) {
            webContext.setVariable("exception", e);
            engine.process("exception", webContext, resp.getWriter());
        }
    }
}
