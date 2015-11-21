package behaviordroid.automaton;

import java.util.ArrayList;
import java.util.List;

import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.util.NonDeterministicException;


/**
 * Created by Alexis on 03-06-15.
 */
public class State {


    private String id;
    private String name;

    private boolean finalState;
    private boolean initialState;
    private BehaviorType behaviorType;

    private List<Transition> transitionsFromHere;


    public State() {
        this.transitionsFromHere = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String idAutomaton, int id) {

        this.id = idAutomaton + id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
    }

    public boolean isInitialState() {
        return initialState;
    }

    public void setInitialState(boolean initialState) {
        this.initialState = initialState;
    }

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(BehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }

    public List<Transition> getTransitionsFromHere() {
        return transitionsFromHere;
    }

    public void setTransitionsFromHere(List<Transition> transitionsFromHere) {
        this.transitionsFromHere = transitionsFromHere;
    }


    /**
     * Check whether the state has some transition with the symbol to consume.
     *
     * @param symbol to consume.
     * @param app    to monitor.
     * @return the next state to the automaton, null if the state doesn't have a transition with the symbol.
     * @throws NonDeterministicException if has more than one possible transition, i. e., the automaton is non-deterministic.
     */
    public State getNextState(Symbol symbol, String app) throws NonDeterministicException {

        State nextState = null;

        for (Transition transition : transitionsFromHere) {
            if (transition.acceptSymbol(symbol, app)) {
                if (nextState == null) {
                    nextState = transition.getDestinationState();
                } else {
                    throw new NonDeterministicException("Non deterministic transitions in the state " + id);
                }
            }
        }
        return nextState;
    }


    /**
     * Redirect the transition of this state that have a symbol with symbolId to state newDestination.
     * Doesn't work well if is a non-deterministic automaton.
     *
     * @param symbolId       id of the symbol that is in the transition to redirect
     * @param newDestination new destination for the transition
     * @return the redirected transition to newDestination.
     * @throws IllegalArgumentException in case the state doesn't have a transition with symbolId.
     */
    public void redirectTransition(String symbolId, State newDestination) {

        Transition transitionToRedirect = null;

        for (Transition transition : transitionsFromHere) {
            if (transition.getSymbol().getId().equals(symbolId)) {
                transitionToRedirect = transition;
            }
        }

        if (transitionToRedirect == null) {
            throw new IllegalArgumentException("Doesn't exist a transition with symbol " + symbolId + ".");
        }

        transitionToRedirect.setDestinationState(newDestination);
        transitionToRedirect.resetId();

    }

    public void addTransitionFromHere(Transition transition) {

        if (!transition.getOriginState().getId().equals(getId())) {
            throw new IllegalArgumentException();
        }

        transitionsFromHere.add(transition);
    }

    public Transition addNewTransitionFromHere(Transition transition) {

        for (Transition t : transitionsFromHere) {
            if (t.getId().equals(transition.getId()))
                return t;
        }

        addTransitionFromHere(transition);
        return transition;
    }
}
