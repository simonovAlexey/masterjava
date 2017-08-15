package ru.javaops.masterjava.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.xml.schema.FlagType;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static ru.javaops.masterjava.web.ThymeleafAppUtil.getTemplateEngine;


@WebServlet("/")
public class UploadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext ctx = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        getTemplateEngine(getServletContext()).process("upload", ctx, resp.getWriter());
//        req.getRequestDispatcher("jsp//upload.jsp").forward(req, resp);
    }

    public void process(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        WebContext ctx = new WebContext(request, response, request.getServletContext(),
                request.getLocale());
        ctx.setVariable("currentDate", new Date());
        getTemplateEngine(getServletContext()).process("upload", ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<FileItem> fileItems = getFileItems(req);
        if (fileItems == null) doGet(req, resp);
        else if (fileItems.size() == 0) doGet(req, resp);

        String fileName = fileItems.get(0).getName();
        InputStream is = fileItems.get(0).getInputStream();

        Set<User> users = processByStax(is);
        is.close();

        WebContext ctx = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        ctx.setVariable("fileName", fileName);
        ctx.setVariable("list", users);
        getTemplateEngine(getServletContext()).process("output", ctx, resp.getWriter());
        /*req.setAttribute("fileName", fileName);
        req.setAttribute("list", users);
        req.getRequestDispatcher("jsp//output.jsp").forward(req, resp);*/
    }

    private List<FileItem> getFileItems(HttpServletRequest req) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        List<FileItem> fileItems = null;
        try {
            fileItems = upload.parseRequest(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        return fileItems;
    }

    private static Set<User> processByStax(InputStream is) {

        try (StaxStreamProcessor processor = new StaxStreamProcessor(is)) {
            final Set<String> groupNames = new HashSet<>();

            Set<User> users = new TreeSet<>((o1, o2) -> o1.getValue().compareTo(o2.getValue()));

            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                User user = new User();
                user.setFlag(FlagType.fromValue(processor.getAttribute("flag")));
                user.setEmail(processor.getAttribute("email"));
                user.setValue(processor.getText());
                users.add(user);
            }
            return users;
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }


}
