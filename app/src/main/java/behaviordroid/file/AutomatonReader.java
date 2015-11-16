package behaviordroid.file;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import behaviordroid.automaton.Automaton;
import behaviordroid.automaton.BehaviorType;
import behaviordroid.automaton.State;
import behaviordroid.automaton.Transition;
import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.automaton.symbol.SymbolStructure;
import behaviordroid.util.Constants;

/**
 * Created by Alexis on 03-06-15.
 */
public class AutomatonReader extends FileReader {

    //Xml elements...
    private static final String TYPE = "type";
    private static final String TYPE_VALUE = "fa";

    private static final String AUTOMATON = "automaton";
    private static final String STATE = "state";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final String INITIAL = "initial";
    private static final String FINAL = "final";
    private static final String TRANSITION = "transition";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String READ = "read";


    private static final Pattern PATTERN_SYMBOL = Pattern.compile(
            "^(\\S+)" //id structure
                    + "(\\(.*\\))$"); //parameters

    private static final Pattern PATTERN_PARAMS = Pattern.compile(
            "\\s*([^\\s\\(\\:]+)" + //id
                    "\\s*:\\s*" + // :
                    "(([^\\s\\\"\\,\\)]+)|(\\\"(?:[^\\\"]|(?:\\\\\"))*\\\"))" + // value
                    "\\s*[,\\)]" // , or )

    );


    private static List<SymbolStructure> symbolStructureList;

    public static void setSymbolStructureList(List<SymbolStructure> symbolStructureList) {
        AutomatonReader.symbolStructureList = symbolStructureList;
    }

    private int id;
    private Automaton automaton;


    public AutomatonReader(String filePath, int id) throws IOException, SAXException {
        super(filePath);
        this.id = id;
    }

    @Override
    public Automaton read() throws ParserConfigurationException, SAXException, IOException {
        parseFile();
//        AEMFLogger.write("Read " + automaton.getFilename() + " automaton.");
        return automaton;

    }

    @Override
    protected DefaultHandler generateDocumentHandler() {

        // Implements a default handle able to parser the automaton file.
        return new DefaultHandler() {

            //Aux variables
            StringBuilder sb;
            boolean readChars;

            State state;
            Transition transition;

            @Override
            public void startDocument() {
                sb = new StringBuilder();
                readChars = false;
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {

                if (qName.equalsIgnoreCase(AUTOMATON)) {
                    automaton = new Automaton();
                    automaton.setId(id);
                    automaton.setFilename(file.getName());

                } else if (qName.equalsIgnoreCase(STATE)) {
                    state = new State();
                    state.setId(automaton.getId(), Integer.parseInt(attributes.getValue(ID)));
                    state.setName(attributes.getValue(NAME));

                } else if (qName.equalsIgnoreCase(INITIAL)) {
                    state.setInitialState(true);

                } else if (qName.equalsIgnoreCase(FINAL)) {
                    state.setFinalState(true);

                } else if (qName.equalsIgnoreCase(TRANSITION)) {
                    transition = new Transition();


                } else if (qName.equalsIgnoreCase(TYPE) || qName.equalsIgnoreCase(LABEL) ||
                        qName.equalsIgnoreCase(FROM) || qName.equalsIgnoreCase(TO) ||
                        qName.equalsIgnoreCase(READ)) {

                    readChars = true;
                }


            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {

                if (readChars) {
                    sb.append(ch, start, length);
                }


            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {


                if (qName.equalsIgnoreCase(TYPE)) {

                    String type = sb.toString();
                    if (!type.equalsIgnoreCase(TYPE_VALUE)) {
                        throw new SAXException("File " + file.getName() + ": The structure isn't a finite automaton.");
                    }

                    sb.setLength(0); //clear builder
                    readChars = false;


                } else if (qName.equalsIgnoreCase(LABEL)) {

                    //Green, Red, White or Nothing...
                    BehaviorType behaviorType = BehaviorType.createFromString(sb.toString());
                    if (behaviorType == null) {
                        throw new SAXException("File " + file.getName() + ": The labels must be Green, Red or White.");
                    }
                    state.setBehaviorType(behaviorType);

                    sb.setLength(0);
                    readChars = false;

                } else if (qName.equalsIgnoreCase(FROM)) {

                    int index = Integer.parseInt(sb.toString());
                    State newState = automaton.getStates().get(index);
                    transition.setOriginState(newState);

                    sb.setLength(0);
                    readChars = false;


                } else if (qName.equalsIgnoreCase(TO)) {

                    int index = Integer.parseInt(sb.toString());
                    State newState = automaton.getStates().get(index);
                    transition.setDestinationState(newState);

                    sb.setLength(0);
                    readChars = false;

                } else if (qName.equalsIgnoreCase(READ)) {

                    Symbol symbol = parseSymbol(sb.toString());
                    Symbol aux = automaton.addNewSymbol(symbol);
                    transition.setSymbol(aux);

                    sb.setLength(0);
                    readChars = false;

                } else if (qName.equalsIgnoreCase(STATE)) {

                    //Default value to behaviortype
                    if (state.getBehaviorType() == null) {
                        state.setBehaviorType(BehaviorType.WHITE);
                    }

                    //Add this state to the new automaton
                    automaton.getStates().add(state);

                } else if (qName.equalsIgnoreCase(TRANSITION)) {

                    // Add it to automaton and to origin state
                    automaton.getTransitions().add(transition);
                    transition.getOriginState().getTransitionsFromHere().add(transition);


                } else if (qName.equalsIgnoreCase(AUTOMATON)) {

                    //Verify exist one (and only one) initial state;
                    State initialState = null;
                    for (State s : automaton.getStates()) {
                        if (s.isInitialState()) {
                            if (initialState == null) {
                                initialState = s;
                            } else {
                                throw new SAXException("File " + file.getName() + ": The automaton has more than one initial state.");
                            }
                        }
                    }

                    if (initialState == null) {
                        throw new SAXException("File " + file.getName() + ": The automaton doesn't have an initial state.");
                    }

                    automaton.setInitialState(initialState);

                }

            }

        };
    }


    private Symbol parseSymbol(String symbolText) throws SAXException {


        Matcher matcher = PATTERN_SYMBOL.matcher(symbolText);

        if (matcher.matches()) {

            //Get structure
            String idStructure = matcher.group(1).trim();
            SymbolStructure symbolStructure = findSymbolStructureById(idStructure);
            if (symbolStructure == null) {
                throw new SAXException("File " + file.getName() + ": The symbol structure " + idStructure + " doesn't exist.");
            }

            //Get parameters...
            String paramsText = matcher.group(2).trim();
            Matcher matcherParams = PATTERN_PARAMS.matcher(paramsText);

            String id, value;
            TreeMap<String, String> paramValues = new TreeMap<>();
            while (matcherParams.find()) {
                id = matcherParams.group(1).trim();
                value = matcherParams.group(2).trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                    value = value.replace("\\\"", "\"");
                }
                paramValues.put(id, value);
            }

            //If fail, throw a exception.
            if (!verifyParams(paramValues, symbolStructure)) {
                throw new SAXException("File " + file.getName() + ": The params of " + symbolText + " don't fulfill with the structure.");
            }


            //Set-up super symbol
            Symbol symbol = new Symbol();
            symbol.setStructure(symbolStructure);
            symbol.setParameterValues(paramValues);

            return symbol;

        } else {
            throw new SAXException("File " + file.getName() + ": The symbol " + symbolText + " doesn't fulfill with the format.");
        }


    }

    private boolean verifyParams(TreeMap<String, String> paramValues, SymbolStructure symbolStructure) {

        String id, value;


        for (Map.Entry<String, Pattern> pDescription : symbolStructure.getParameterDescriptions().entrySet()) {

            id = pDescription.getKey();
            value = paramValues.get(id);

            //all params have to be declared
            if (value != null) {

                //only the param "app" can take any value
                if (value.equals(Constants.MONITORED_APP)
                        && !id.equals(Constants.APP_PARAMETER_ID)) {
                    return false;
                }

                //the value has to fulfill with the description
                if (!value.equals(Constants.MONITORED_APP)
                        && !pDescription.getValue().matcher(value).matches()) {
                    return false;
                }


            } else {
                return false;
            }
        }


        return true;
    }

    private SymbolStructure findSymbolStructureById(String id) {

        for (SymbolStructure ss : symbolStructureList) {
            if (ss.getId().equals(id)) {
                return ss;
            }
        }
        return null;
    }


}
