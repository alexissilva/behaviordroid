package behaviordroid.listener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.monitor.MonitorManager;
import behaviordroid.util.Logger;
import behaviordroid.util.DroidConfiguration;
import behaviordroid.util.Globals;

/**
 * Created by Alexis on 07-07-15.
 */
public class StraceListener extends Thread {

    private static final String STRACE_NOT_PERMITTED =
            "attach: ptrace(PTRACE_ATTACH, ...): Operation not permitted";

    private boolean stopped = true;
    private String app;
    private int pid;

    public StraceListener(String app, int pid) {
        this.app = app;
        this.pid = pid;
        setName("StraceListener" + pid);
    }

    @Override
    public void run() {

        stopped = false;
        Logger.write("Strace Listener " + pid + " started.");

        Process strace = null;

        try {

            strace = Runtime.getRuntime().exec("su -c " + DroidConfiguration.getStracePath() + " -p " + pid);
            BufferedReader br = new BufferedReader(new InputStreamReader(strace.getErrorStream()));


            StraceEvent straceEvent = new StraceEvent();
            Inspector inspector = new Inspector();
            String line;

            while (!stopped && (line = br.readLine()) != null) {

                if (line.endsWith(STRACE_NOT_PERMITTED)) {
                    Logger.write("Strace " + pid + " not permitted.");
                    break;
                }

                if (straceEvent.parseStrace(app, pid, line)) {
                    Symbol symbol = inspector.createSymbolFromEvent(straceEvent);
                    if (symbol != null) {
                        MonitorManager.processSymbol(symbol);
                    }

                }
            }


        } catch (Exception e) {
            Logger.write("Error listen to strace " + pid + "! " + e.toString());
            e.printStackTrace();
            Globals.getInstance().getService().onException(e);
        } finally {
            if (strace != null) {
                strace.destroy();
            }
            stopped = true;
            Logger.write("Strace Listener " + pid + " stopped.");
        }

    }

    public boolean isListening() {
        return !stopped;
    }

    public void stopListening() {
        stopped = true;
    }
}
