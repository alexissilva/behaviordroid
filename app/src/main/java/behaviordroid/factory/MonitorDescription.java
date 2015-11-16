package behaviordroid.factory;

import java.util.LinkedHashSet;

/**
 * Created by Alexis on 03-09-15.
 */
public class MonitorDescription {

    private String app;
    private LinkedHashSet<String> automatonFilenames;

    public MonitorDescription(String app) {
        this.app = app;
        this.automatonFilenames = new LinkedHashSet<>();
    }

    public void addAutomaton(String automaton) {
        automatonFilenames.add(automaton);
    }

    public String getApp() {
        return app;
    }

    public LinkedHashSet<String> getAutomatonFilenames() {
        return automatonFilenames;
    }
}
