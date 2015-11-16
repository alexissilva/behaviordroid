package behaviordroid.listener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.automaton.symbol.SymbolStructureStrace;
import behaviordroid.monitor.Monitor;
import behaviordroid.util.Logger;
import behaviordroid.util.Configuration;
import behaviordroid.util.Constants;
import behaviordroid.util.Globals;

/**
 * Created by Alexis on 07-07-15.
 */
public class ProcessListener extends Thread {


    private static ProcessListener instance;
    private static boolean stopped = true;

    static class ProcessInfo {
        boolean running;
        StraceListener straceListener;
    }

    private static HashMap<String, ProcessInfo> appsToMonitor; //with strace!


    private static Pattern PATTERN_PS = Pattern.compile(
            "^\\S+\\s+" + //first column
                    "([0-9]+)" + //pid
                    ".+\\s+" + //other data
                    "(.+)$" //app
    );

    private Matcher psMatcher = PATTERN_PS.matcher("");

    private ProcessListener() {
        setName("ProcessListener");
    }


    @Override
    public void run() {

        stopped = false;
        Logger.write("Process Listener started.");

        String line;
        Process ps = null;

        try {

            if (appsToMonitor == null || appsToMonitor.isEmpty()) {
                Logger.write("There are not apps to monitor with strace.");
                stopped = true;
            }

            while (!stopped) {

                //Reset running status, because I don't know if are running in this iteration...
                for (ProcessInfo pInfo : appsToMonitor.values()) {
                    pInfo.running = false;
                }


                ps = Runtime.getRuntime().exec("ps");
                BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));

                //Jump the first row
                br.readLine();

                while (!stopped && (line = br.readLine()) != null) {

                    Matcher matcher = psMatcher.reset(line);
                    if(matcher.matches()) {

                        int pid = Integer.parseInt(matcher.group(1));
                        String app = matcher.group(2);

                        //Verify if is a app to monitor.
                        if (appsToMonitor.containsKey(app)) {

                            ProcessInfo pInfo = appsToMonitor.get(app);
                            pInfo.running = true;

                            //Verify if isn't listening
                            if (pInfo.straceListener == null || !pInfo.straceListener.isListening()) {

                                //Create a new strace listener and run.
                                pInfo.straceListener = new StraceListener(app, pid);
                                pInfo.straceListener.start();
//                                Thread thread = new Thread(pInfo.straceListener, "StraceListener" + pid);
//                                thread.start();
                            }
                        }
                    }else{
                        throw new IllegalArgumentException("Error reading process, pattern ps bad defined");
                    }

                }

                //Stop strace listener that its process isn't running.
                for (ProcessInfo pInfo : appsToMonitor.values()) {
                    if (!pInfo.running && pInfo.straceListener != null) {
                        pInfo.straceListener.stopListening();
                        pInfo.straceListener = null;
                    }
                }


                //Sleep for performance...
                Thread.sleep(Configuration.getSleepTimeProcessListener());


            }
        } catch (Exception e) {
            Logger.write("Error listen to process! " + e.toString());
            e.printStackTrace();
            Globals.getInstance().getService().onException(e);
        } finally {
            if (ps != null) {
                ps.destroy();
            }
//            stopped = true;
            stopListening();
            Logger.write("Process Listener stopped.");

            //Before end, close other listeners...
            if (appsToMonitor != null) {
                for (ProcessInfo pInfo : appsToMonitor.values()) {
                    if (pInfo.straceListener != null) {
                        pInfo.straceListener.stopListening();
                    }
                }
            }
        }


    }

    public static synchronized void startListening() {
        if (instance == null) {
            instance = new ProcessListener();
            instance.start();
        }else{
            throw new IllegalStateException("Process Listener is already running.");
        }

    }

    public static void stopListening() {
        stopped = true;
        instance = null;
    }

    public static void configAppsToMonitor(List<Monitor> monitorList) {

        List<String> appsToMonitor = detectAppsToMonitorWithStrace(monitorList);
        ProcessListener.appsToMonitor = new HashMap<>();
        for (String app : appsToMonitor) {
            ProcessListener.appsToMonitor.put(app, new ProcessInfo());
        }
    }

    private static List<String> detectAppsToMonitorWithStrace(List<Monitor> monitorList) {


        List<String> appsToMonitor = new ArrayList<>();

        for (Monitor m : monitorList) {
            for (Symbol s : m.getAutomaton().getAlphabet()) {

                if (s.getStructure() instanceof SymbolStructureStrace) {

                    //This structure always has parameter app..
                    String app = s.getParameterValues().get(Constants.APP_PARAMETER_ID);

                    //If the automaton accept any app, use the value of monitor...
                    if (app.equals(Constants.MONITORED_APP)) {
                        app = m.getApp();
                    }

                    //Add a new app to monitor...
                    if (!appsToMonitor.contains(app)) {
                        appsToMonitor.add(app);
                    }
                }
            }
        }


//        AEMFLogger.write("Detected " + appsToMonitor.size() + " apps to monitor with strace.");
        return appsToMonitor;

    }


}
