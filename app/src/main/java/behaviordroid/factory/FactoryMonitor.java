package behaviordroid.factory;

import java.util.ArrayList;
import java.util.List;

import behaviordroid.automaton.Automaton;
import behaviordroid.monitor.Monitor;
import behaviordroid.util.DroidConfiguration;
import behaviordroid.util.Constants;
import behaviordroid.util.InconsistentSpecificationException;
import behaviordroid.util.NonDeterministicException;

/**
 * Created by Alexis on 03-09-15.
 */
public class FactoryMonitor {

    private static List<Automaton> automatonList;

    public static void setAutomatonList(List<Automaton> automatonList) throws NonDeterministicException {

        if (automatonList != null) {
            for (Automaton a : automatonList) {
                if (DroidConfiguration.isMinimizeAllAutomatons()) {
                    Minimizer.minimizeAutomaton(a);
                    Minimizer.removeLoops(a);
                }
            }
        }

        FactoryMonitor.automatonList = automatonList;
    }

    public static List<Monitor> createMonitors(List<MonitorDescription> monitorDescriptionList) throws InconsistentSpecificationException, NonDeterministicException {

        if (automatonList == null) {
            throw new NullPointerException("The automaton list can't be null.");
        }

        List<Monitor> monitorList = new ArrayList<>();


        for (MonitorDescription md : monitorDescriptionList) {

            //get app to monitor
            String app = md.getApp();
            Automaton theAutomaton;

            theAutomaton = getAutomatonCreated(monitorList, md);
            if (theAutomaton == null) {

                //get automatons
                List<Automaton> automatonList = new ArrayList<>();
                for (String filename : md.getAutomatonFilenames()) {
                    Automaton automaton = findAutomatonByFilename(filename);
                    if (automaton == null) {
                        throw new NullPointerException("The automaton " + filename + " doesn't exist.");
                    }

                    automatonList.add(automaton);
                }

                //unify automatons... and minimize
                if (automatonList.size() > 1) {
                    theAutomaton = Unifier.uniteAutomatons(automatonList);
                    Minimizer.minimizeAutomaton(theAutomaton);
                    Minimizer.removeLoops(theAutomaton);
                } else if (automatonList.size() == 1) {
                    theAutomaton = automatonList.get(0);
                } else {
                    continue;
                }
            }


            //add to the list
            monitorList.add(new Monitor(theAutomaton, app));
        }

        return monitorList;
    }


    private static Automaton findAutomatonByFilename(String filename) {

        for (Automaton a : automatonList) {
            if (a.getFilename().equals(filename))
                return a;
        }
        return null;
    }


    /**
     * If 2 monitors has sames automatons, avoid create again. Work only to get united automaton.
     */
    private static Automaton getAutomatonCreated(List<Monitor> monitorList, MonitorDescription monitorDescription) {

        boolean exist;

        for (Monitor m : monitorList) {
            Automaton a = m.getAutomaton();
            exist = true;

            //Check if the automaton was created with the same quantity of the monitor description.
            int automatonNumber = a.getId().length() - a.getId().replace(Constants.SEPARATOR, "").length();
            if (automatonNumber != monitorDescription.getAutomatonFilenames().size()) {
                //Try with next monitor.
                continue;
            }

            for (String filename : monitorDescription.getAutomatonFilenames()) {
                String aux = filename + Constants.SEPARATOR;
                if (!a.getFilename().contains(aux)) {
                    exist = false;
                    break;
                }
            }
            if (exist) {
                return a;
            }
        }
        return null;


    }

}
