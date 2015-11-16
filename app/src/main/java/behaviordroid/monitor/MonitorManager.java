package behaviordroid.monitor;

import android.app.Service;

import java.util.List;

import behaviordroid.automaton.BehaviorType;
import behaviordroid.automaton.State;
import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.util.Logger;
import behaviordroid.util.Globals;
import behaviordroid.util.NonDeterministicException;

/**
 * Created by Alexis on 05-06-15.
 */
public class MonitorManager {

    private static List<Monitor> monitorList;

    private static OnNewStateListener onGreenStateListener;
    private static OnNewStateListener onRedStateListener;


    public static synchronized void processSymbol(Symbol symbol) throws NonDeterministicException {

        if (monitorList == null) {
            throw new NullPointerException("The monitor list can't be null.");
        }

        for (Monitor m : monitorList) {
            m.processSymbol(symbol);
        }
    }

    public static void setMonitorList(List<Monitor> monitorList) {
        MonitorManager.monitorList = monitorList;
    }

    public static void setOnGreenStateListener(OnNewStateListener onGreenStateListener) {
        MonitorManager.onGreenStateListener = onGreenStateListener;
    }

    public static void setOnRedStateListener(OnNewStateListener onRedStateListener) {
        MonitorManager.onRedStateListener = onRedStateListener;
    }

    //Package-private.
    static void notifyNewState(Monitor monitor, Symbol readSymbol, State oldState) {

        Service service = Globals.getInstance().getService();
        String extraMessage = "";

        State newState = monitor.getCurrentState();
        if (newState.getBehaviorType() == BehaviorType.GREEN) {
            extraMessage += " Green state reached.";
            if (onGreenStateListener != null) {
                onGreenStateListener.onNewState(service, monitor, monitor.getApp(), monitor.getAutomaton(), monitor.getCurrentState(), readSymbol, oldState);
            }

        }

        if (newState.getBehaviorType() == BehaviorType.RED) {
            extraMessage += " Red state reached.";
            if (onRedStateListener != null) {
                onRedStateListener.onNewState(service, monitor, monitor.getApp(), monitor.getAutomaton(), monitor.getCurrentState(), readSymbol, oldState);
            }
        }

//        AEMFLogger.write(monitor.getApp() + ": the automaton " + monitor.getAutomaton().getFilename()
//                + " changed to state " + newState.getName() + "." + extraMessage);
        Logger.write("The automaton " + monitor.getAutomaton().getFilename() + " of monitor for "
                + monitor.getApp() + " changed to state " + newState.getName() + " with the symbol " + readSymbol.getId() + ". " + extraMessage);

    }
}
