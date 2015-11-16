package behaviordroid.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import behaviordroid.automaton.symbol.MessageSyntax;
import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.automaton.symbol.SymbolStructure;
import behaviordroid.automaton.symbol.SymbolStructureLogcat;
import behaviordroid.automaton.symbol.SymbolStructureStrace;
import behaviordroid.util.Constants;

/**
 * Created by Alexis on 23-06-15.
 * <p/>
 * Try to create a symbol from a event using the structure previously defined.
 */
public class Inspector {

    private static List<SymbolStructure> symbolStructureList;
    private static Pattern PATTERN_PS_APP = Pattern.compile(
            ".+\\s+" + //other data
                    "(\\S+)$"); //name of app


    public static void setSymbolStructureList(List<SymbolStructure> symbolStructureList) {
        Inspector.symbolStructureList = symbolStructureList;
    }

    //To avoid create a new one per every comparison.
    //A match per each parameter of every symbol.
    private List<HashMap<String, Matcher>> paramMatchers;

    public Inspector() {

        paramMatchers = new ArrayList<>();
        for (int i = 0; i < symbolStructureList.size(); i++) {
            HashMap<String, Matcher> matchersPerSymbol = new HashMap<>();
            for (Map.Entry<String, Pattern> entry : symbolStructureList.get(i).getParameterDescriptions().entrySet()) {
                matchersPerSymbol.put(entry.getKey(), entry.getValue().matcher(""));
            }
            paramMatchers.add(matchersPerSymbol);
        }
    }

    /**
     * Try to create a symbol from a straceEvent. The method compare with every symbol structure strace.
     * It delegate hard work to createSymbolFromEvent(StraceEvent straceEvent, SymbolStructureStrace
     * symbolStructure, HashMap<String,Matcher> paramMatchers)
     *
     * @param straceEvent to "transform"
     * @return a symbol if is possible, null otherwise.
     */
    public Symbol createSymbolFromEvent(StraceEvent straceEvent) {

        if (symbolStructureList == null) {
            throw new NullPointerException("The symbol structure list can't be null.");
        }

        for (int i = 0; i < symbolStructureList.size(); i++) {
            SymbolStructure ss = symbolStructureList.get(i);
            if (ss instanceof SymbolStructureStrace) {
                Symbol symbol = createSymbolFromEvent(straceEvent, (SymbolStructureStrace) ss, paramMatchers.get(i));
                if (symbol != null) {
//                    AEMFLogger.write("System call \"" + straceEvent.getRawText() + "\" " +
//                            "transformed to the symbol " + symbol.getId() + ".");
                    return symbol;
                }
            }
        }

        return null;
    }


    /**
     * Try to create a symbol from a straceEvent. The method compared with the structured symbolStructure.
     * Match the event with a structure using the app, name, return value and the parameters of system call.
     * Reuse the match to avoid create a new one.
     *
     * @param straceEvent     to "transform"
     * @param symbolStructure base of the new symbol.
     * @param paramMatchers   to compare the params of the symbol.
     * @return the created symbol, null if the event doesn't match with the structure.
     */
    private synchronized Symbol createSymbolFromEvent(StraceEvent straceEvent, SymbolStructureStrace symbolStructure, HashMap<String, Matcher> paramMatchers) {

        if (straceEvent.getNameSystemCall().equals(symbolStructure.getNameSystemCall())) {

            //Find parameters
            TreeMap<String, String> parameterValues = new TreeMap<>();
            for (Map.Entry<String, Pattern> pDescription : symbolStructure.getParameterDescriptions().entrySet()) {

                String idParam = pDescription.getKey();
                String valueParam;

                if (idParam.equals(Constants.APP_PARAMETER_ID)) {
                    //The app that make the call
                    valueParam = straceEvent.getApp();

                } else if (idParam.equals(Constants.RETURN_VALUE_PARAMETER_ID)) {
                    //The return value of system call
                    valueParam = straceEvent.getReturnValue();


                } else {
                    //A parameter of system call
                    int idNumber = Integer.parseInt(idParam.replaceFirst(Constants.STRACE_PARAMETER_PREFIX, ""));
                    valueParam = straceEvent.getParameters().get(idNumber - 1);
                }

                parameterValues.put(idParam, valueParam);

                //Check whether the param fulfill with the structure values
                Matcher paramMatcher = paramMatchers.get(idParam).reset(valueParam);
                if (!paramMatcher.matches()) {
                    return null;
                }
            }

            //Create symbol
            Symbol symbol = new Symbol();
            symbol.setStructure(symbolStructure);
            symbol.setParameterValues(parameterValues);

            return symbol;

        } else {
            return null;
        }

    }


    /**
     * Try to create a symbol from a logcatEvent. The method compare with every symbol structure logcat.
     * It delegate hard work to createSymbolFromEvent(LogcatEvent logcatEvent, SymbolStructureLogcat
     * symbolStructure, HashMap<String, Matcher> paramMatchers).
     *
     * @param logcatEvent used to create a new symbol.
     * @return a symbol if is possible, null otherwise.
     */
    public Symbol createSymbolFromEvent(LogcatEvent logcatEvent) throws IOException {

        if (symbolStructureList == null) {
            throw new NullPointerException("The symbol structure list can't be null.");
        }

        for (int i = 0; i < symbolStructureList.size(); i++) {
            SymbolStructure ss = symbolStructureList.get(i);
            if (ss instanceof SymbolStructureLogcat) {
                Symbol symbol = createSymbolFromEvent(logcatEvent, (SymbolStructureLogcat) ss, paramMatchers.get(i));
                if (symbol != null) {
//                    AEMFLogger.write("Log \"" + logcatEvent.getMessage() + "\" " +
//                            "transformed to the symbol " + symbol.getId() + ".");
                    return symbol;
                }
            }
        }

        return null;

    }

    /**
     * Try to create a symbol from a logcatEvent. The method compared with the structured symbolStructure.
     * Match the event with a structure using the log, tag, syntax and pid of log message.
     * Reuse the match to avoid create a new one.
     *
     * @param logcatEvent     to "transform"
     * @param symbolStructure base of the new symbol.
     * @param paramMatchers   to compare the params of the symbol.
     * @return the created symbol, null if the event doesn't match with the structure.
     */
    private synchronized Symbol createSymbolFromEvent(LogcatEvent logcatEvent, SymbolStructureLogcat symbolStructure, HashMap<String, Matcher> paramMatchers) throws IOException {

        //Do easier check first...
        if (!logcatEvent.getLevel().equals(symbolStructure.getLevel()) || !logcatEvent.getTag().equals(symbolStructure.getTag())) {
            return null;
        }


        MessageSyntax syntax = symbolStructure.getMessageSyntax();
        Matcher syntaxMatcher = syntax.getExpression().matcher(logcatEvent.getMessage());

        if (!syntaxMatcher.matches()) {
            return null;
        }

        String id, value;
        TreeMap<String, String> paramValues = new TreeMap<>();

        for (Map.Entry<String, Integer> catchGroup : syntax.getCatchGroups().entrySet()) {
            id = catchGroup.getKey();
            value = syntaxMatcher.group(catchGroup.getValue());

            //Verify if the value match with the description of the structure
            Matcher paramMatcher = paramMatchers.get(id).reset(value);
            if (!paramMatcher.matches()) {
                return null;
            }

            paramValues.put(id, value);
        }


        //BUT, if the location of app is in PID
        if (symbolStructure.getAppLocation() == SymbolStructureLogcat.PID) {


            Process ps = Runtime.getRuntime().exec("ps " + logcatEvent.getPID());
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            //Jump the first row
            br.readLine();
            String aux = br.readLine();
            if (aux == null) {
                return null;
            }
            Matcher matcher1 = PATTERN_PS_APP.matcher(aux);
            if (matcher1.matches()) {
                String app = matcher1.group(1).trim();

                Matcher appMatcher = paramMatchers.get(Constants.APP_PARAMETER_ID).reset(app);
                if(!appMatcher.matches()){
                    return null;
                }

                paramValues.put(Constants.APP_PARAMETER_ID, app);

            } else {
                throw new IllegalArgumentException("Error creating symbol logcat with pid. Pattern ps_app bad defined.");
            }


        }


        //Add this structure..
        Symbol symbol = new Symbol();
        symbol.setStructure(symbolStructure);
        symbol.setParameterValues(paramValues);

        return symbol;


    }

}
