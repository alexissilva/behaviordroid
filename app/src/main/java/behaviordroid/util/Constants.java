package behaviordroid.util;

/**
 * Created by Alexis on 05-09-15.
 */
public class Constants {

    /**
     * Name of parameter app.
     */
    public static final String APP_PARAMETER_ID = "app";

    /**
     * Predefined names of parameters in a strace symbol.
     */
    public static final String RETURN_VALUE_PARAMETER_ID = "return";
    public static final String STRACE_PARAMETER_PREFIX = "p";

    /**
     * Used by define parameterized automatons.
     */
    public static final String MONITORED_APP = "@monitored";

    /**
     * Used by the minimizer to get the next state without an app.
     */
    public static final String MINIMIZER_APP = "@minimizer";

    /**
     * Used to merge id, names, etc. (very useful in Unifier)
     */
    public static final String SEPARATOR = "#";
}
