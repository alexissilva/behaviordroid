package behaviordroid.file;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import behaviordroid.factory.MonitorDescription;
import behaviordroid.util.Constants;

/**
 * Created by Alexis on 26-06-15.
 */
public class MonitorReader extends FileReader {

    //Xml Elements
    private static final String MONITOR = "monitor";
    private static final String APP = "app";
    private static final String AUTOMATON = "automaton";

    private List<MonitorDescription> monitorDescriptionList;

    public MonitorReader(String filePath) throws IOException {
        super(filePath);
    }

    @Override
    public List<MonitorDescription> read() throws ParserConfigurationException, SAXException, IOException {

        parseFile();
//        AEMFLogger.write("Read " + monitorDescriptionList.size() + " monitors.");
        return monitorDescriptionList;
    }


    @Override
    protected DefaultHandler generateDocumentHandler() {
        return new DefaultHandler() {

            MonitorDescription monitorDescription;
            StringBuilder sb;
            boolean readChars;


            @Override
            public void startDocument() {
                monitorDescriptionList = new ArrayList<>();
                sb = new StringBuilder();
                readChars = false;
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

                if (qName.equalsIgnoreCase(MONITOR)) {
                    String app = attributes.getValue(APP);
                    if (app.equals(Constants.MINIMIZER_APP) || app.equals(Constants.MONITORED_APP)) {
                        throw new SAXException(app + "is a reserved word.");
                    }
                    monitorDescription = new MonitorDescription(app);

                } else if (qName.equalsIgnoreCase(AUTOMATON)) {
                    readChars = true;
                }


            }

            @Override
            public void characters(char ch[], int start, int length) {

                if (readChars) {
                    sb.append(ch, start, length);
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {

                if (qName.equalsIgnoreCase(AUTOMATON)) {

                    String automaton = sb.toString();
                    monitorDescription.addAutomaton(automaton);

                    sb.setLength(0); //clear
                    readChars = false;

                } else if (qName.equalsIgnoreCase(MONITOR)) {

                    if (monitorDescription.getAutomatonFilenames().size() == 0) {
                        throw new SAXException("The monitor must have at least one automaton.");
                    }
                    monitorDescriptionList.add(monitorDescription);
                }
            }

        };
    }
}
