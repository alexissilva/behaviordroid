package behaviordroid.automaton;

/**
 * Created by Alexis on 27-06-15.
 */
public enum BehaviorType {

    WHITE, RED, GREEN;
    //red_and_green is a special type only used in united automatons.

    public static BehaviorType createFromString(String behaviorString){

        if(behaviorString.equalsIgnoreCase(WHITE.name()))
            return WHITE;
        else if(behaviorString.equalsIgnoreCase(RED.name()))
            return RED;
        else if(behaviorString.equalsIgnoreCase(GREEN.name()))
            return GREEN;
        else
            return null;
    }
}
