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
}
