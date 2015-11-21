package behaviordroid.automaton;

import java.util.ArrayList;
import java.util.List;

import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.util.Utils;


/**
 * Created by Alexis on 03-06-15.
 */
public class Automaton {

    private String id;
    private String filename;

    private State initialState;

    private List<State> states;
    private List<Symbol> alphabet;
    private List<Transition> transitions;

    public Automaton() {
        states = new ArrayList<>();
        alphabet = new ArrayList<>();
        transitions = new ArrayList<>();
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public State getInitialState() {
        return initialState;
    }

    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<Symbol> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(List<Symbol> alphabet) {
        this.alphabet = alphabet;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }


    public String getId() {
        return id;
    }

    public void setId(int id) {
        this.id = Utils.numberToLetters(id);
    }

    public void setId(String id) {
        this.id = id;
    }


    /**
     * Add a new symbol to the alphabet. Add the symbol only if this isn't in the
     * alphabet previously.
     *
     * @param symbol
     * @return the same object if the symbol was not in the alphabet (ie, was added)
     * in other case return the object that was previously.
     */
    public Symbol addNewSymbol(Symbol symbol) {

        for (Symbol s : alphabet) {
            if (s.getId().equals(symbol.getId()))
                return s;
        }

        alphabet.add(symbol);
        return symbol;
    }


    /**
     * Add a new state to the automaton. Add the state only if this isn't in the
     * automaton previously.
     *
     * @param state
     * @return the same object if the state was not in the automaton (ie, was added)
     * in other case return the object that was previously.
     */
    public State addNewState(State state) {

        for (State s : states) {
            if (s.getId().equals(state.getId()))
                return s;
        }

        states.add(state);
        return state;
    }


    /**
     * Add a new transition to the automaton. Add the transition only if this isn't in the
     * automaton previously.
     *
     * @param transition
     * @return the same object if the transition was not in the automaton (ie, was added)
     * in other case return the object that was previously.
     */
    public Transition addNewTransition(Transition transition) {

        for (Transition t : transitions) {
            if (t.getId().equals(transition.getId()))
                return t;
        }

        transitions.add(transition);
        return transition;
    }


    public boolean areObjectReused(){

        //All transition of the states are in transition list.
        for(State s : states){
            if(!transitions.containsAll(s.getTransitionsFromHere())){
                return false;
            }
        }

        //All states and symbol of the transitions are in state and symbol list.
        for(Transition t :transitions){
            if(!states.contains(t.getOriginState()) || !states.contains(t.getDestinationState()) || !alphabet.contains(t.getSymbol())){
                return false;
            }
        }

        return true;
    }


}
