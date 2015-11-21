package behaviordroid.file;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import behaviordroid.automaton.symbol.MessageSyntax;
import behaviordroid.automaton.symbol.SymbolStructure;
import behaviordroid.automaton.symbol.SymbolStructureLogcat;
import behaviordroid.automaton.symbol.SymbolStructureStrace;

/**
 * Created by Alexis on 03-06-15.
 */
public class SymbolStructureReader extends FileReader {

    private static final Pattern PATTERN_ID = Pattern.compile("[\\w_]+");

    //Xml elements...
    private static final String SYMBOL_STRUCTURE = "symbol_structure";
    private static final String ID = "id";
    private static final String PARAM = "param";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String LEVEL = "level";
    private static final String TAG = "tag";

    private static final String MESSAGE_SYNTAX = "message_syntax";
    private static final String EXPRESSION = "expression";
    private static final String CATCH = "catch";
    private static final String GROUP = "group";

    private static final String NAME_CALL = "name_call";

    private static final String TYPE = "type";
    private static final String LOGCAT = "logcat";
    private static final String STRACE = "strace";


    private static final String APP_LOCATION = "app_location";
    private static final String NO_APP = "no_app";
    private static final String PID = "pid";
    private static final String MESSAGE = "message";


    private List<SymbolStructure> symbolStructureList;

    public SymbolStructureReader(String path) throws IOException, SAXException {
        super(path);
    }

    @Override
    public List<SymbolStructure> read() throws IOException, SAXException, ParserConfigurationException {
        parseFile();
//        AEMFLogger.write("Read " + symbolStructureList.size() + " symbol structures.");
        return symbolStructureList;
    }

    @Override
    protected DefaultHandler generateDocumentHandler() {

        return new DefaultHandler() {

            //Aux variables
            StringBuilder sb;
            boolean readChars;

            SymbolStructure symbolStructure;
            String idParameter;
            MessageSyntax messageSyntax;

            @Override
            public void startDocument() {
                symbolStructureList = new ArrayList<>();
                sb = new StringBuilder();
                readChars = false;
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

                if (qName.equalsIgnoreCase(SYMBOL_STRUCTURE)) {

                    if (attributes.getValue(TYPE).equalsIgnoreCase(LOGCAT)) {
                        symbolStructure = new SymbolStructureLogcat();
                    } else if (attributes.getValue(TYPE).equalsIgnoreCase(STRACE)) {
                        symbolStructure = new SymbolStructureStrace();
                    } else {
                        throw new SAXException("Symbol structure " + attributes.getValue(ID) + ": Type unknown.");
                    }

                    String id = attributes.getValue(ID);
                    Matcher matcherId = PATTERN_ID.matcher(id);
                    if(!matcherId.matches()) {
                       throw new SAXException("Symbol structure "+ id +": Invalid id.");
                    }
                    symbolStructure.setId(id);

                } else if (qName.equalsIgnoreCase(MESSAGE_SYNTAX)) {
                    messageSyntax = new MessageSyntax();

                } else if (qName.equalsIgnoreCase(CATCH)) {
                    String param = attributes.getValue(PARAM);
                    int group = Integer.parseInt(attributes.getValue(GROUP));
                    messageSyntax.getCatchGroups().put(param, group);

                } else if (qName.equalsIgnoreCase(PARAM)) {
                    idParameter = attributes.getValue(ID);
                    readChars = true;

                } else if (qName.equalsIgnoreCase(NAME) || qName.equalsIgnoreCase(DESCRIPTION) ||
                        qName.equalsIgnoreCase(LEVEL) || qName.equalsIgnoreCase(TAG) ||
                        qName.equalsIgnoreCase(EXPRESSION) || qName.equalsIgnoreCase(NAME_CALL) ||
                        qName.equalsIgnoreCase(APP_LOCATION)) {
                    readChars = true;
                }




            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {

                if(readChars){
                    sb.append(ch, start, length);
                }

            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {


                if (qName.equalsIgnoreCase(PARAM)) {
                    Pattern description = Pattern.compile(sb.toString());
                    symbolStructure.getParameterDescriptions().put(idParameter, description);

                    readChars = false;
                    sb.setLength(0);

                } else if (qName.equalsIgnoreCase(NAME)) {
                    symbolStructure.setName(sb.toString());

                    readChars = false;
                    sb.setLength(0);

                } else if (qName.equalsIgnoreCase(DESCRIPTION)) {
                    symbolStructure.setDescription(sb.toString());

                    readChars = false;
                    sb.setLength(0);

                } else if (qName.equalsIgnoreCase(LEVEL)) {
                    try {
                        ((SymbolStructureLogcat) symbolStructure).setLevel(sb.toString());
                    } catch (ClassCastException e) {
                        throw new SAXException("Symbol structure " + symbolStructure.getId() + ": Only logcat symbols has level.");
                    }

                    readChars = false;
                    sb.setLength(0);

                } else if (qName.equalsIgnoreCase(TAG)) {
                    try {
                        ((SymbolStructureLogcat) symbolStructure).setTag(sb.toString());
                    } catch (ClassCastException e) {
                        throw new SAXException("Symbol structure " + symbolStructure.getId() + ": Only logcat symbols has tag.");
                    }

                    readChars = false;
                    sb.setLength(0);

                } else if (qName.equalsIgnoreCase(EXPRESSION)) {
                    Pattern expression = Pattern.compile(sb.toString());
                    messageSyntax.setExpression(expression);

                    readChars = false;
                    sb.setLength(0);

                } else if (qName.equalsIgnoreCase(NAME_CALL)) {
                    try {
                        ((SymbolStructureStrace) symbolStructure).setNameSystemCall(sb.toString());
                    } catch (ClassCastException e) {
                        throw new SAXException("Symbol structure " + symbolStructure.getId() + ": Only strace symbols has name system call.");
                    }

                    readChars = false;
                    sb.setLength(0);

                } else if (qName.equalsIgnoreCase(APP_LOCATION)) {

                    String locationAux = sb.toString();
                    int locationApp = -1;
                    if (locationAux.equalsIgnoreCase(MESSAGE)) {
                        locationApp = SymbolStructureLogcat.MESSAGE;
                    } else if (locationAux.equalsIgnoreCase(PID)) {
                        locationApp = SymbolStructureLogcat.PID;
                    } else if (locationAux.equalsIgnoreCase(NO_APP)) {
                        locationApp = SymbolStructureLogcat.NO_APP;
                    }
                    try {
                        ((SymbolStructureLogcat) symbolStructure).setAppLocation(locationApp);
                    } catch (ClassCastException e) {
                        throw new SAXException("Symbol structure " + symbolStructure.getId() + ": Only logcat symbols has location app.");
                    }

                    readChars = false;
                    sb.setLength(0);


                } else if (qName.equalsIgnoreCase(MESSAGE_SYNTAX)) {
                    try {
                        ((SymbolStructureLogcat) symbolStructure).setMessageSyntax(messageSyntax);
                    } catch (ClassCastException e) {
                        throw new SAXException("Symbol structure " + symbolStructure.getId() + ": Only logcat symbols has message syntax.");
                    }

                } else if (qName.equalsIgnoreCase(SYMBOL_STRUCTURE)) {

                    if (!symbolStructure.isWellDefined()) {
                        throw new SAXException("Symbol structure " + symbolStructure.getId() + ": Is not well defined.");
                    }
                    symbolStructureList.add(symbolStructure);
                }
            }

        };
    }


}
