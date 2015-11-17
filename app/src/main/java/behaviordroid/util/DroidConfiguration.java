package behaviordroid.util;

import android.os.Environment;

/**
 * Created by Alexis on 03-06-15.
 *
 * Some configurations with its default values.
 */
public class DroidConfiguration {


    /**
     * Path of configuration files.
     */
    private static String rootDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BehaviorDroid";
    private static String automatonFileDirectory = rootDirectory + "/automatons";
    private static String symbolStructureFilePath = rootDirectory + "/symbol_structures.xml";
    private static String monitorFilePath = rootDirectory + "/monitors.xml";
    private static String logPath = rootDirectory + "/log.txt";
    private static String stracePath = rootDirectory + "/strace";


    /**
     * What you listen it? You can reduce the consume if disabled the not used listeners.
     */
    private static boolean listenToLogcat = true;
    private static boolean listenToSystemCalls = true;


    /**
     * How much time (in milliseconds) should sleep the thread of process listener
     * between 2 followed "ps".
     */
    private static long sleepTimeProcessListener = 1000;

    /**
     * Minimize all automatons (not only the unified automaton).
     */
    private static boolean minimizeAllAutomatons = false;



    /**
     * Getters and Setters
     */

    public static String getRootDirectory() {
        return rootDirectory;
    }

    public static void setRootDirectory(String rootDirectory) {
        DroidConfiguration.rootDirectory = rootDirectory;
    }

    public static String getAutomatonFileDirectory() {
        return automatonFileDirectory;
    }

    public static void setAutomatonFileDirectory(String automatonFileDirectory) {
        DroidConfiguration.automatonFileDirectory = automatonFileDirectory;
    }

    public static String getSymbolStructureFilePath() {
        return symbolStructureFilePath;
    }

    public static void setSymbolStructureFilePath(String symbolStructureFilePath) {
        DroidConfiguration.symbolStructureFilePath = symbolStructureFilePath;
    }

    public static String getMonitorFilePath() {
        return monitorFilePath;
    }

    public static void setMonitorFilePath(String monitorFilePath) {
        DroidConfiguration.monitorFilePath = monitorFilePath;
    }

    public static String getLogPath() {
        return logPath;
    }

    public static void setLogPath(String logPath) {
        DroidConfiguration.logPath = logPath;
    }

    public static String getStracePath() {
        return stracePath;
    }

    public static void setStracePath(String stracePath) {
        DroidConfiguration.stracePath = stracePath;
    }

    public static boolean isListenToLogcat() {
        return listenToLogcat;
    }

    public static void setListenToLogcat(boolean listenToLogcat) {
        DroidConfiguration.listenToLogcat = listenToLogcat;
    }

    public static boolean isListenToSystemCalls() {
        return listenToSystemCalls;
    }

    public static void setListenToSystemCalls(boolean listenToSystemCalls) {
        DroidConfiguration.listenToSystemCalls = listenToSystemCalls;
    }

    public static long getSleepTimeProcessListener() {
        return sleepTimeProcessListener;
    }

    public static void setSleepTimeProcessListener(long sleepTimeProcessListener) {
        DroidConfiguration.sleepTimeProcessListener = sleepTimeProcessListener;
    }

    public static boolean isMinimizeAllAutomatons() {
        return minimizeAllAutomatons;
    }

    public static void setMinimizeAllAutomatons(boolean minimizeAllAutomatons) {
        DroidConfiguration.minimizeAllAutomatons = minimizeAllAutomatons;
    }
}
