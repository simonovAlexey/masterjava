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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
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
        /*printUsersJaxB(fileS);
        printUsersStax(fileS);*/
//        printUserTabeleXSLT(fileS);
        printGroupTabeleXSLT(fileS);

    }

    private static void printUserTabeleXSLT(String fileS) {
        String outputHTML = "outputXSLT.html";
        String templateXSL = "userTabele.xsl";
        doXsltTransform(fileS, outputHTML, templateXSL);


    }
    private static void printGroupTabeleXSLT(String fileS) {
        String outputHTML = "groupXSLT.html";
        String templateXSL = "groupTemplate.xsl";
        doXsltTransform(fileS, outputHTML, templateXSL);


    }

    private static void doXsltTransform(String fileS, String outputHTML, String templateXSL) {
        try(InputStream fXML = getInputStream(fileS);
            InputStream fXSL = getInputStream(templateXSL)) {
            TransformerFactory factory = TransformerFactory.newInstance();
            StreamSource xslStream = new StreamSource(fXSL);
            Transformer transformer = factory.newTransformer(xslStream);

            StreamSource in = new StreamSource(fXML);
            StreamResult out = new StreamResult(outputHTML);
            transformer.transform(in, out);
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The generated HTML file is: " + outputHTML);
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
