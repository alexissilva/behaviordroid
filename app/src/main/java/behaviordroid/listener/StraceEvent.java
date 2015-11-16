package behaviordroid.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexis on 07-07-15.
 */
public class StraceEvent {

    private static final Pattern PATTERN_STRACE = Pattern.compile(
            "^(\\S+)" //name call
                    + "\\((.*)\\)" //parameters
                    + "\\s+\\=\\s(\\S+)");//return value

    private static final String OPEN_CURLY_BRACKET = "{";
    private static final String CLOSE_CURLY_BRACKET = "}";
    private static final char COMMA = ',';

    private int pid;
    private String app;
    private String rawText;
    private String nameSystemCall;
    private List<String> parameters;
    private String returnValue;

    private Matcher matcherToReuse = PATTERN_STRACE.matcher("");


    public boolean parseStrace(String app, int pid, String straceText) {

        this.app = app;
        this.pid = pid;
        this.rawText = straceText;


        Matcher matcher = matcherToReuse.reset(rawText);
        if (matcher.find()) {

            this.nameSystemCall = matcher.group(1).trim();
            this.returnValue = matcher.group(3).trim();


            this.parameters = new ArrayList<>();
            String parameters = matcher.group(2);

            //The parameters are separated by ',' but exist compound parameters.
            //They have sub-parameters separated by ',' too and closed by { }.
            int startIndex = 0;
            while (true) {

                int indexComma = parameters.indexOf(COMMA, startIndex);

                //In this case, this is the last parameter.
                if (indexComma == -1) {
                    this.parameters.add(parameters.trim());
                    break;
                }

                //Get the possible parameter...
                String auxParameter = parameters.substring(0, indexComma).trim();

                //Check if the quantity of opening and closing bracket is the same inside the parameter...
                int countOpen = auxParameter.length() - auxParameter.replace(OPEN_CURLY_BRACKET, "").length();
                int countClose = auxParameter.length() - auxParameter.replace(CLOSE_CURLY_BRACKET, "").length();

                //If are equals the ',' is not between brackets, i.e. I find a parameter.
                if (countOpen == countClose) {
                    //Save the parameter and remove from the string parameters.
                    this.parameters.add(auxParameter);
                    parameters = parameters.substring(indexComma + 1);
                    startIndex = 0;
                } else {
                    //Search the next ',' ...
                    startIndex = indexComma + 1;
                }
            }

            return true;

        } else {
            return false;
        }

    }


    public String getApp() {
        return app;
    }

    public int getPid() {
        return pid;
    }

    public String getRawText() {
        return rawText;
    }

    public String getNameSystemCall() {
        return nameSystemCall;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getReturnValue() {
        return returnValue;
    }
}
