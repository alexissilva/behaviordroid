package behaviordroid.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import behaviordroid.automaton.Automaton;
import behaviordroid.automaton.BehaviorType;
import behaviordroid.automaton.State;
import behaviordroid.automaton.Transition;
import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.util.Constants;
import behaviordroid.util.InconsistentSpecificationException;
import behaviordroid.util.NonDeterministicException;

/**
 * Created by Alexis on 18-07-15.
 */
public class Unifier {


    public static Automaton uniteAutomatons(List<Automaton> automatonList) throws InconsistentSpecificationException, NonDeterministicException {

        //Remove replied automatons
        HashSet<String> ids = new HashSet<>();
        Iterator<Automaton> it = automatonList.iterator();
        while (it.hasNext()) {
            Automaton automaton = it.next();
            if (!ids.contains(automaton.getId())) {
                ids.add(automaton.getId());
            } else {
                it.remove();
            }
        }


        Automaton newAutomaton = new Automaton();

        //Set id and filename merging the info of the automatons to unite
        String mergedId = "";
        String mergedFileName = "";
        for (Automaton a : automatonList) {
            mergedId += a.getId() + Constants.SEPARATOR;
            mergedFileName += a.getFilename() + Constants.SEPARATOR;
        }
        newAutomaton.setId(mergedId);
        newAutomaton.setFilename(mergedFileName);


        //Get all symbols and initial states
        List<Symbol> auxAlphabet = new ArrayList<>();
        List<State> initialStateList = new ArrayList<>();
        for (Automaton a : automatonList) {
            auxAlphabet.addAll(a.getAlphabet());
            initialStateList.add(a.getInitialState());
        }


        for (Symbol s : auxAlphabet) {
            newAutomaton.addNewSymbol(s);
        }

        //Prepare first argument to unite states.
        UnionArg arg = new UnionArg();
        arg.origin = null;
        arg.symbol = null;
        arg.states = initialStateList;

        uniteStates(arg, newAutomaton);


        return newAutomaton;

    }


    static class UnionArg {

        State origin;
        Symbol symbol;
        List<State> states;
    }

    private static void uniteStates(UnionArg firstArg, Automaton underConstruction) throws InconsistentSpecificationException, NonDeterministicException {


        LinkedList<UnionArg> argStack = new LinkedList<>();
        argStack.push(firstArg);


        while (!argStack.isEmpty()) {

            UnionArg arg = argStack.getFirst();
            List<State> stateList = arg.states;

            String idToMerge = "";
            for (State stateToMerge : stateList) {
                idToMerge += stateToMerge.getId() + Constants.SEPARATOR;
            }

            //verify if was created previously...
            boolean exist = false;
            for (State addedState : underConstruction.getStates()) {

                //simply compare 2 string, because the order is the same
                if (addedState.getId().equals(idToMerge)) {
                    exist = true;
                } else {
                    exist = false;
                }

                if (exist) {

                    if (arg.origin != null) {

                        Transition newTransition = new Transition();
                        newTransition.setOriginState(arg.origin);
                        newTransition.setSymbol(arg.symbol);
                        newTransition.setDestinationState(addedState);

                        arg.origin.getTransitionsFromHere().add(newTransition);

                        //add transition to automaton
                        underConstruction.getTransitions().add(newTransition);

                    } else {
                        addedState.setInitialState(true);
                        underConstruction.setInitialState(addedState);
                    }

                    argStack.pop();
                    break;
                }
            }

            if (exist) {
                continue;
            }


            //if none of the above is true, we have to create a new one merging the others
            State newState = new State();

            String mergedId = "";
            String mergedName = "";
            boolean finalState = false;
            boolean greenBehavior = false;
            boolean redBehavior = false;

            HashMap<String, List<State>> transitionHash = new HashMap<>();

            for (State s : stateList) {

                //merge id and name
                mergedId += s.getId() + Constants.SEPARATOR;
                mergedName += s.getName() + Constants.SEPARATOR;

                //if at least one state is final, the new one will be final
                finalState = finalState || s.isFinalState();

                //get behavior type
                if (s.getBehaviorType() == BehaviorType.GREEN) {
                    greenBehavior = true;
                }
                if (s.getBehaviorType() == BehaviorType.RED) {
                    redBehavior = true;
                }

                //add next states to transitionHash
                addNextStates(transitionHash, s, underConstruction.getAlphabet());

            }

            newState.setId(mergedId);
            newState.setName(mergedName);
            newState.setFinalState(finalState);


            //config behavior
            if (greenBehavior && redBehavior) {
                throw new InconsistentSpecificationException("The unified automaton " + underConstruction.getFilename() + " is inconsistent.");
            } else if (greenBehavior) {
                newState.setBehaviorType(BehaviorType.GREEN);
            } else if (redBehavior) {
                newState.setBehaviorType(BehaviorType.RED);
            } else {
                newState.setBehaviorType(BehaviorType.WHITE);
            }

            //add state to automaton
            underConstruction.getStates().add(newState);


            //Add new states for unite to stack...
            for (String symbolId : transitionHash.keySet()) {

                UnionArg arg2 = new UnionArg();
                arg2.origin = newState;
                arg2.symbol = findSymbolById(underConstruction.getAlphabet(), symbolId);
                arg2.states = transitionHash.get(symbolId);


                argStack.push(arg2);

            }


        }

    }

    private static Symbol findSymbolById(List<Symbol> alphabet, String id) {

        for (Symbol s : alphabet) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    private static void addNextStates(HashMap<String, List<State>> nextStateHash, State state, List<Symbol> alphabet) throws NonDeterministicException {


        for (Symbol sym : alphabet) {
            State next = state.getNextState(sym, Constants.SYSTEM_APP);
            if (next == null) {
                next = state;
            }

            if (!nextStateHash.containsKey(sym.getId())) {
                List<State> aux = new ArrayList<>();
                aux.add(next);
                nextStateHash.put(sym.getId(), aux);
            } else {
                nextStateHash.get(sym.getId()).add(next);
            }
        }
    }


}
