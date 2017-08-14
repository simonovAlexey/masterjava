package ru.javaops.masterjava.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@WebServlet("/")
public class UploadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher("jsp//upload.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        List<FileItem> fileItems = null;
        try {
            fileItems = upload.parseRequest(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        if (fileItems == null) req.getRequestDispatcher("jsp//upload.jsp").forward(req, resp);
        else if (fileItems.size() == 0) req.getRequestDispatcher("jsp//upload.jsp").forward(req, resp);
        String fileName = fileItems.get(0).getName();
        String fileString = fileItems.get(0).getString();
        InputStream is = fileItems.get(0).getInputStream();
        try (StaxStreamProcessor processor = new StaxStreamProcessor(is)){
            String city;

            while ((city = processor.getElementValue("City")) != null) {
                System.out.println(city);
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }


        req.setAttribute("fileName", fileName);
        req.getRequestDispatcher("jsp//upload.jsp").forward(req, resp);
    }

}
