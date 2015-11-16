package behaviordroid.automaton.symbol;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Alexis on 12-06-15.
 */
public class MessageSyntax {

    private Pattern expression;
    private HashMap<String, Integer> catchGroups; //<id param, group>

    public MessageSyntax() {
        this.catchGroups = new HashMap<>();
    }

    public Pattern getExpression() {
        return expression;
    }

    public void setExpression(Pattern expression) {
        this.expression = expression;
    }

    public HashMap<String, Integer> getCatchGroups() {
        return catchGroups;
    }

    public void setCatchGroups(HashMap<String, Integer> catchGroups) {
        this.catchGroups = catchGroups;
    }


//TODO clean this
//    public boolean areGroupsWellDefined(SymbolStructureLogcat symbolStructure){
//
//        for(String param : symbolStructure.getParameterDescriptions().keySet()){
//            Integer group = catchGroups.get(param);
//            if(group == null || group < 0){
//                return false;
//            }
//        }
//        return true;
//    }

    /*
 * A message is composed of a series of tokens. Each token can be a literal,
 * a parameter (user-defined) or a reserved token system.
 * <p/>
 * Both "parameter tokens" like "system tokens" are variables and
 * their possible values are defined by a regular expression.
*/
//
//    //Static elements....
//    private static final HashMap<String, String> SYSTEM_TOKENS;
//
//    static {
//        SYSTEM_TOKENS = new HashMap<>();
//        SYSTEM_TOKENS.put("dummy", ".*");
//        SYSTEM_TOKENS.put("simple", "\\S+");
//        SYSTEM_TOKENS.put("integer", "[0-9]+");
//        SYSTEM_TOKENS.put("decimal", "[0-9]+(\\.[0-9]+)?");
//        SYSTEM_TOKENS.put("alphabetic", "[A-Za-z]+");
//        SYSTEM_TOKENS.put("alphanumeric","[A-Za-z0-9]+");
//    }
//
//    /**
//     * @param tokenString to analyze
//     * @return true if is a system token, false otherwise.
//     */
//    public static boolean isSystemToken(String tokenString) {
//        for (String systemParameter : SYSTEM_TOKENS.keySet()) {
//            if (systemParameter.equalsIgnoreCase(tokenString)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    private Pattern pattern;
//    private HashMap<String, List<Integer>> parameterIndexes;
//
//
//    /**
//     * @throws IllegalArgumentException if parseMessageSyntax does it.
//     */
//    public MessageSyntax(String rawSyntax, HashMap<String, String> parameterDescriptions) {
//        parseMessageSyntax(rawSyntax, parameterDescriptions);
//    }
//
//
//    /**
//     * Create a regular expression since a string with the raw syntax.
//     *
//     * @param rawSyntax
//     * @param parameterDescriptions
//     * @throws IllegalArgumentException when rawSyntax has a parameter not defined in parametersDescriptions.
//     */
//    private void parseMessageSyntax(String rawSyntax, HashMap<String, String> parameterDescriptions) {
//
//
//        this.parameterIndexes = new HashMap<>();
//
//        String[] tokenArray = rawSyntax.split("(\\{%)|(%\\})");
//        String patternString = "^";
//        int indexToken = 1;
//
//        for (int i = 0; i < tokenArray.length; i++) {
//
//            //It's a variable token....
//            if (i % 2 == 1) {
//
//                if (isSystemToken(tokenArray[i])) {
//                    patternString += "(" + SYSTEM_TOKENS.get(tokenArray[i]) + ")";
//                } else {
//
//                    if (!parameterDescriptions.containsKey(tokenArray[i])) {
//                        throw new IllegalArgumentException("The parameter " + tokenArray[i] + "was not defined in the structure.");
//                    }
//
//                    //Subgroups became non-counting groups, this necessary to recover correctly the parameters later.
//                    //Note: The first replace is problematic if the expression has the literal "(". The second replace solved it.
//                    patternString += "(" + parameterDescriptions.get(tokenArray[i]).replace("(", "(?:").replace("\\(?:", "\\(") + ")";
//
//
//                    //Save the position of the variable in the grammar...
//                    if (!parameterIndexes.containsKey(tokenArray[i])) {
//                        List<Integer> auxList = new ArrayList<>();
//                        auxList.add(indexToken);
//                        parameterIndexes.put(tokenArray[i], auxList);
//                    } else {
//                        parameterIndexes.get(tokenArray[i]).add(indexToken);
//                    }
//                }
//                indexToken++;
//            }
//            //It's a literal..
//            else if (!tokenArray[i].isEmpty()) {
//                patternString += "(" + Pattern.quote(tokenArray[i]) + ")";
//                indexToken++;
//            }
//        }
//        patternString += "$";
//
//        this.pattern = Pattern.compile(patternString);
//
//    }
//
//
//    public Pattern getPattern() {
//        return pattern;
//    }
//
//    public HashMap<String, List<Integer>> getParameterIndexes() {
//        return parameterIndexes;
//    }
}
