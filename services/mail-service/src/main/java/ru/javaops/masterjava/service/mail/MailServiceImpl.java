package ru.javaops.masterjava.service.mail;

import ru.javaops.web.WebStateException;

import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
import java.io.*;
import java.util.Set;

@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService", targetNamespace = "http://mail.javaops.ru/"
//          , wsdlLocation = "WEB-INF/wsdl/mailService.wsdl"
)
@MTOM
public class MailServiceImpl implements MailService {

    @Override
    public String sendToGroup(Set<Addressee> to, Set<Addressee> cc, String subject, String body, FileUpload upFile) throws WebStateException {

        File file = getFile(upFile);

        return MailSender.sendToGroup(to, cc, subject, body, file);
    }

    private File getFile(FileUpload upFile) {
        String path = System.getProperty("java.io.tmpdir");
        File file = new File(path + upFile.getName());
            try {
            InputStream inputStream = upFile.getHolderFile().getInputStream();
            copyInputStreamToFile(inputStream, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public GroupResult sendBulk(Set<Addressee> to, String subject, String body, FileUpload upFile) throws WebStateException {
        File file = getFile(upFile);
        return MailServiceExecutor.sendBulk(to, subject, body, file);
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}