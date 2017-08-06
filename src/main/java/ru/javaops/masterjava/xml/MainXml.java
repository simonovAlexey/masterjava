package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MainXml {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) {
        String fileS = (args.length==0) ? fileS="payload.xml" : args[0];
        Payload payload = null;
        try {
            payload = JAXB_PARSER.unmarshal(Resources.getResource(fileS).openStream());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<User> users = payload.getUsers().getUser();
        Collections.sort(users, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        for (User u:users) {
            System.out.println(u);
        }
    }
}
