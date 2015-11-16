package behaviordroid.monitor;

import behaviordroid.automaton.Automaton;
import behaviordroid.automaton.State;
import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.util.NonDeterministicException;

/**
 * Created by Alexis on 19-06-15.
 */
public class Monitor {

    private Automaton automaton;
    private State currentState;
    private String app;


    public Monitor(Automaton automaton, String app) {
        this.automaton = automaton;
        this.currentState = automaton.getInitialState();
        this.app = app;
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public State getCurrentState() {
        return currentState;
    }

    public String getApp() {
        return app;
    }



    public void processSymbol(Symbol symbol) throws NonDeterministicException {

        State oldState = currentState;
        State nextState = currentState.getNextState(symbol, app);

        //automaton accept/read the symbol?
        if (nextState != null) {

            currentState = nextState;

            //automaton change of state?
            if (!oldState.getId().equals(currentState.getId())) {
                MonitorManager.notifyNewState(this, symbol, oldState);
            } else {
//                AEMFLogger.write(app+": the automaton " + automaton.getFilename()
//                        + " didn't change of state ("+currentState.getName()+").");
            }
        }
    }
}
