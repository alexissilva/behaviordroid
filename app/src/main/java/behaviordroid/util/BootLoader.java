package behaviordroid.util;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import behaviordroid.automaton.Automaton;
import behaviordroid.automaton.symbol.SymbolStructure;
import behaviordroid.factory.FactoryMonitor;
import behaviordroid.factory.MonitorDescription;
import behaviordroid.file.AutomatonReader;
import behaviordroid.file.MonitorReader;
import behaviordroid.file.SymbolStructureReader;
import behaviordroid.listener.Inspector;
import behaviordroid.listener.LogcatListener;
import behaviordroid.listener.ProcessListener;
import behaviordroid.monitor.Monitor;
import behaviordroid.monitor.MonitorManager;

/**
 * Created by Alexis on 04-06-15.
 */
public class BootLoader {

    public static void boot() throws ParserConfigurationException, SAXException, IOException, InconsistentSpecificationException, NonDeterministicException {

        Logger.write("Booting system.");

        configStructures();
        configAutomatons();
        configMonitors();
        freeMemory();

        Logger.write("Success boot.");
    }

    private static void configStructures() throws IOException, SAXException, ParserConfigurationException {

        //Read symbol structure file...
        SymbolStructureReader structureReader = new SymbolStructureReader(Configuration.getSymbolStructureFilePath());
        List<SymbolStructure> symbolStructureList = structureReader.read();

        //Set values
        Inspector.setSymbolStructureList(symbolStructureList);
        AutomatonReader.setSymbolStructureList(symbolStructureList);

        Logger.write("Read " + symbolStructureList.size() + " symbol structures.");


    }

    private static void configAutomatons() throws IOException, SAXException, ParserConfigurationException, NonDeterministicException {

        //Create an automaton reader per file existing in the automaton directory...
        File[] automatonFiles = new File(Configuration.getAutomatonFileDirectory()).listFiles();
        if (automatonFiles == null) {
            throw new IOException("[" + automatonFiles + "] doesn't exist.");
        }
        List<AutomatonReader> automatonReaderList = new ArrayList<>();
        int idAutomaton = 1;
        for (File f : automatonFiles) {
            if (f.getName().toLowerCase().contains(".jff")) {

                automatonReaderList.add(new AutomatonReader(f.getAbsolutePath(), idAutomaton));
                idAutomaton++;
            }
        }

        //Read all automaton file and add the automatons to the list...
        List<Automaton> automatonList = new ArrayList<>();
        for (AutomatonReader automatonReader : automatonReaderList) {
            Automaton automaton = automatonReader.read();
            automatonList.add(automaton);
        }

        //Set values
        FactoryMonitor.setAutomatonList(automatonList);

        Logger.write("Read " + automatonList.size() + " automatons.");

    }

    private static void configMonitors() throws IOException, SAXException, ParserConfigurationException, NonDeterministicException, InconsistentSpecificationException {

        //Read monitor file...
        MonitorReader appMonitorReader = new MonitorReader(Configuration.getMonitorFilePath());
        List<MonitorDescription> monitorDescriptionList = appMonitorReader.read();

        //Call to factory monitor...
        List<Monitor> monitorList = FactoryMonitor.createMonitors(monitorDescriptionList);

        //Set values
        MonitorManager.setMonitorList(monitorList);
        ProcessListener.configAppsToMonitor(monitorList);
        LogcatListener.configAppsToMonitorWithLogcat(monitorList);

        Logger.write("Read " + monitorDescriptionList.size() + " monitors.");

    }

    /**
     * "Free" data that won't be used again.
     */
    private static void freeMemory() throws NonDeterministicException {

        AutomatonReader.setSymbolStructureList(null);
        FactoryMonitor.setAutomatonList(null);

    }

}
