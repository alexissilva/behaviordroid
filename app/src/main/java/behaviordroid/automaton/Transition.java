package behaviordroid.automaton;

import java.util.Map;

import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.util.Constants;


/**
 * Created by Alexis on 03-06-15.
 */
public class Transition {

    private String id;
    private State originState;
    private State destinationState;
    private Symbol symbol;

    public String getId() {
        if (id == null) {
            resetId();
        }
        return id;
    }

    public State getOriginState() {
        return originState;
    }

    public void setOriginState(State originState) {
        this.originState = originState;
    }

    public State getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(State destinationState) {
        this.destinationState = destinationState;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    /**
     * Create a unique code with the next format: T(State1, Symbol) = State2
     */
    private String generateCode() {

        return "T(" + originState.getId() + ", " + symbol.getId() + ") = " + destinationState.getId();

    }

    /**
     * Generate again, useful if the transition change.
     */
    public void resetId(){
        id = generateCode();
    }



    /**
     * A transition will accept a symbol if it has the same structure as the symbol that belongs
     * to the transition and the parameters are "equals".
     *
     * It's possible that the symbol (that belongs to the transition) doesn't have well defined the
     * parameter app, in that case the comparison will do with the string app.
     * (Thus we permitted reused automaton for many apps).
     *
     * @param symbol to be process.
     * @param app origin of symbol.
     * @return true if accept symbol, false otherwise.
     * @throws IllegalArgumentException if the symbol needs to know the app and this is null.
     */
    public boolean acceptSymbol(Symbol symbol, String app) {

        //Compare the structure...
        if (!this.symbol.getStructure().getId().equals(symbol.getStructure().getId())) {
            return false;
        }

        //Compare the parameter values...
        for (Map.Entry<String, String> entry : this.symbol.getParameterValues().entrySet()) {

            String idParam = entry.getKey();
            String ownValue = entry.getValue();
            String valueToProcess = symbol.getParameterValues().get(idParam);

            //If the parameter to compare is "app" and the symbol accept "anything",
            //  use the string app to compare with value to process.
            //Otherwise to compare the value to process directly with the own value.
            if (idParam.equals(Constants.APP_PARAMETER_ID)
                    && ownValue.equals(Constants.MONITORED_APP)) {

                if (app == null) {
                    throw new IllegalArgumentException("The transition " + getId() + " require an app to process symbols.");
                }

                //Minimizer app is a special value used for the minimizer to get the next state
                if (!app.equals(Constants.SYSTEM_APP) && !valueToProcess.equals(app)) {
                    return false;
                }

            } else if (!valueToProcess.equals(ownValue)) {
                return false;
            }
        }

        return true;
    }


}
