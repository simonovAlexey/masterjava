package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class MainXml {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) {
        String fileS = (args.length == 0) ? fileS = "payload.xml" : args[0];
        printUsersJaxB(fileS);
        printUsersStax(fileS);


    }

    private static void printUsersStax(String fileS) {
        TreeMap<String, String> treeMap = new TreeMap<>();

        try (InputStream is = getInputStream(fileS);
             StaxStreamProcessor processor = new StaxStreamProcessor(is);) {
            XMLStreamReader reader = processor.getReader();

            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    String localName = reader.getLocalName();
                    if ("User".equals(localName)) {
                        String email = reader.getAttributeValue(null, "email");
                        String name = reader.getElementText();
                        treeMap.put(name, email);
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        treeMap.forEach((k, v) -> System.out.println(k + " / " + v));
    }

    private static InputStream getInputStream(String fileS) {
        InputStream is = null;
        try {
            is = Resources.getResource(fileS).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    private static void printUsersJaxB(String fileS) {
        try (InputStream is = getInputStream(fileS)) {
            Payload payload = null;
            try {
                payload = JAXB_PARSER.unmarshal(is);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
            List<User> users = payload.getUsers().getUser();
            Collections.sort(users, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
            for (User u : users) {
                System.out.println(u);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
