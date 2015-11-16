package behaviordroid.util;

import java.util.List;

import behaviordroid.automaton.Automaton;
import behaviordroid.automaton.symbol.SymbolStructure;
import behaviordroid.DroidService;

/**
 * Created by Alexis on 11-09-15.
 */
public class Globals {

    private static Globals instance;

    private List<SymbolStructure> symbolStructureList;
    private List<Automaton> automatonList;

    private DroidService service;

    private Globals() {
    }

    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }
        return instance;
    }

    public List<SymbolStructure> getSymbolStructureList() {
        return symbolStructureList;
    }

    public void setSymbolStructureList(List<SymbolStructure> symbolStructureList) {
        this.symbolStructureList = symbolStructureList;
    }

    public List<Automaton> getAutomatonList() {
        return automatonList;
    }

    public void setAutomatonList(List<Automaton> automatonList) {
        this.automatonList = automatonList;
    }

    public DroidService getService() {
        return service;
    }

    public void setService(DroidService service) {
        this.service = service;
    }
}
