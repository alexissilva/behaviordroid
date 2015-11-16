package behaviordroid.automaton.symbol;


import behaviordroid.util.Constants;

/**
 * Created by Alexis on 07-07-15.
 */
public class SymbolStructureStrace extends SymbolStructure {

    public SymbolStructureStrace() {
        super();
    }

    private String nameSystemCall;

    public String getNameSystemCall() {
        return nameSystemCall;
    }

    public void setNameSystemCall(String nameSystemCall) {
        this.nameSystemCall = nameSystemCall;
    }

    /**
     * Check if has id and name call. Also A symbol structure strace must defined the parameter app.
     * The other parameters can be the return value or the parameters of system call. The id of
     * every parameter is pre-defined by the framework.
     *
     * @return true if structure is valid, otherwise false.
     */
    @Override
    public boolean isWellDefined() {

        boolean hasId = id != null && !id.isEmpty();
        boolean hasCall = nameSystemCall != null && !nameSystemCall.isEmpty();
        if (!hasId || !hasCall) {
            return false;
        }

        boolean hasApp = false;
        for (String idParam : parameterDescriptions.keySet()) {

            if (idParam.equals(Constants.APP_PARAMETER_ID)) {
                hasApp = true;

            } else if (idParam.equals(Constants.RETURN_VALUE_PARAMETER_ID)) {
                //Do nothing...

            } else {
                try {
                    int idNumber = Integer.parseInt(idParam.replace(Constants.STRACE_PARAMETER_PREFIX, ""));
                    if (idNumber <= 0) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        return hasApp;

    }

}
