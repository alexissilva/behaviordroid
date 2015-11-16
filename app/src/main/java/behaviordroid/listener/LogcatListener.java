package behaviordroid.listener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.automaton.symbol.SymbolStructureLogcat;
import behaviordroid.monitor.Monitor;
import behaviordroid.monitor.MonitorManager;
import behaviordroid.util.Logger;
import behaviordroid.util.Globals;

/**
 * Created by Alexis on 06-06-15.
 */
public class LogcatListener extends Thread {

    private static LogcatListener instance;
    private static boolean stopped = true;
    private static boolean hasAppsToMonitor;

    private LogcatListener() {
        setName("LogcatListener");
    }

    @Override
    public void run() {

        stopped = false;
        Logger.write("Logcat Listener started.");

        Process logcat = null;

        try {

            if (!hasAppsToMonitor) {
                Logger.write("There are not apps to monitor with logcat.");
            } else {
                // First, we need to clear the buffer logs to begin reads
                Runtime.getRuntime().exec("logcat -c");

                // Second, we start to read at real time logs
                logcat = Runtime.getRuntime().exec("logcat -v threadtime");
                BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()));

                LogcatEvent logcatEvent = new LogcatEvent();
                Inspector inspector = new Inspector();
                String line;

                while (!stopped && (line = br.readLine()) != null) {

                    //Parser a text from logcat to an object LogcatEvent...
                    if (logcatEvent.parseLog(line)) {

                        //Create a symbol from the logcatEvent...
                        Symbol symbol = inspector.createSymbolFromEvent(logcatEvent);

                        //Send to the monitors...
                        if (symbol != null) {
                            MonitorManager.processSymbol(symbol);
                        }

                    }
                }
            }
        } catch (Exception e) {
            Logger.write("Error listen to logcat! " + e.toString());
            e.printStackTrace();
            Globals.getInstance().getService().onException(e);
        } finally {
            if (logcat != null) {
                logcat.destroy();
            }
//            stopped = true;
            stopListening();
            Logger.write("Logcat Listener stopped.");
        }
    }

    public static synchronized void startListening() {
        if (instance == null) {
            instance = new LogcatListener();
            instance.start();
        }else{
            throw new IllegalStateException("Logcat Listener is already running.");
        }
    }

    public static void stopListening() {
        stopped = true;
        instance = null;
    }

    public static void configAppsToMonitorWithLogcat(List<Monitor> monitorList) {
        for (Monitor m : monitorList) {
            for (Symbol s : m.getAutomaton().getAlphabet()) {
                if (s.getStructure() instanceof SymbolStructureLogcat) {
                    hasAppsToMonitor = true;
//                    AEMFLogger.write("Detected apps to monitor with logcat.");
                    return;
                }
            }
        }
        hasAppsToMonitor = false;
//        AEMFLogger.write("Didn't detect apps to monitor with logcat.");
    }

}
