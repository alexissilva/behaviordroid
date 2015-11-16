package behaviordroid.monitor;

import android.app.Service;

import behaviordroid.automaton.Automaton;
import behaviordroid.automaton.State;
import behaviordroid.automaton.symbol.Symbol;

/**
 * Created by Alexis on 27-06-15.
 */
public interface OnNewStateListener {

    void onNewState(Service service, Monitor monitor, String app, Automaton automaton,
                    State newState, Symbol readSymbol, State oldState);
}
