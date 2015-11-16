package behaviordroid.factory;

import java.util.ArrayList;
import java.util.HashMap;
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


        //TODO delete this?
        //todo do with a hashset
        //Remove replied automatons
        List<String> ids = new ArrayList<>();
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
        newAutomaton.setMergedId(mergedId);
        newAutomaton.setFilename(mergedFileName);


        //Get all symbols and initial states
        List<Symbol> auxAlphabet = new ArrayList<>();
        List<State> initialStateList = new ArrayList<>();
        for (Automaton a : automatonList) {
            auxAlphabet.addAll(a.getAlphabet());
            initialStateList.add(a.getInitialState());
        }

/*

        //Set alphabet adding all super symbol w/out repetitions and creating new objects.
        //If a symbol doesn't have well defined the parameter app, put a specific value.
        for (Symbol symbol : auxAlphabet) {
            newAutomaton.addNewSymbol(createSymbolWithApp(symbol, app));
        }
*/

        for (Symbol s : auxAlphabet) {
            newAutomaton.addNewSymbol(s);
        }

        //Set initial state creating a new one from the merging of all initial states...
//        State initialState = uniteStates(initialStateList, newAutomaton);

        UnionArg arg = new UnionArg();
        arg.origin = null;
        arg.symbol = null;
        arg.states = initialStateList;


        uniteStates(arg, newAutomaton);


        //Note: In the recursive method uniteStates are added the other symbols and transitions...

        return newAutomaton;

    }


    static class UnionArg {

        State origin;
        Symbol symbol;
        List<State> states;
    }

    //todo private
    public static void uniteStates(UnionArg firstArg, Automaton underConstruction) throws InconsistentSpecificationException, NonDeterministicException {


        LinkedList<UnionArg> argStack = new LinkedList<>();
        argStack.push(firstArg);


//        HashMap<String, HashSet<String>> unitedIds = new HashMap<>();

//        String mergedId;
//        String mergedName;
//        boolean finalState;
//        boolean greenBehavior;
//        boolean redBehavior;
//        boolean exist;

        while (!argStack.isEmpty()) {

            UnionArg arg = argStack.getFirst();
            List<State> stateList = arg.states;
//            List<State> stateList = new ArrayList<>(new LinkedHashSet<>(arg.states));

            String idToMerge = "";
            for (State stateToMerge : stateList) {
                idToMerge += stateToMerge.getId() + Constants.SEPARATOR;
            }

            //verify if was created previously...
            boolean exist = false;
            for (State addedState : underConstruction.getStates()) {
//                exist = true;

                //TODO third experiment ... simple compare 2 string, because the order is the same
                if(addedState.getId().equals(idToMerge)){
                    exist = true;
                }else{
                    exist = false;
                }

//                //second expriment
//                HashSet<String> idSet = unitedIds.get(addedState.getId());
//                for (State stateToMerge : stateList) {
//                    if (!idSet.contains(stateToMerge.getId())) {
//                        exist = false;
//                        break;
//                    }
//                }


//                HashSet<String> ids = new HashSet<>(Arrays.asList(addedState.getId().split(Constants.SEPARATOR)));
//                for (State stateToMerge : stateList) {
//                    if(!ids.contains(stateToMerge.getId())){
//                        exist = false;
//                        break;
//                    }
//                }


//                for (State stateToMerge : stateList) {
//                    String aux = Constants.SEPARATOR + stateToMerge.getId() + Constants.SEPARATOR;
//                    if (!addedState.getId().contains(aux)) {
//                        exist = false;
//                        break;
//                    }
//                }
                if (exist) {

                    if (arg.origin != null) {

                        Transition newTransition = new Transition();
                        newTransition.setOriginState(arg.origin);
                        newTransition.setSymbol(arg.symbol);
                        newTransition.setDestinationState(addedState);

                        arg.origin.getTransitionsFromHere().add(newTransition);

                        //TODO check this -> I'm not sure if in this stage always be a new transition.
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


            //todo delete this
            //remove repetitions
//            HashSet<String> ids = new HashSet<>();
////            List<String> ids = new ArrayList<>();
//            Iterator<State> it = stateList.iterator();
//            while (it.hasNext()) {
//                State state = it.next();
//                if (!ids.contains(state.getId())) {
//                    ids.add(state.getId());
//                } else {
//                    it.remove();
//                }
//            }


            //if none of the above is true, we have to create a new one merging the others
            State newState = new State();

//        List<Transition> auxTransitions = new ArrayList<>();
            //TODO init outside of the loop¿?¿?
            String mergedId = "";
            String mergedName = "";
            boolean finalState = false;
            boolean greenBehavior = false;
            boolean redBehavior = false;

            HashMap<String, List<State>> transitionHash = new HashMap<>();

            for (State s : stateList) {

                //TODO sacar primer separador
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

                //add transitions (originals well-defined, specific-app and missing transitions)
//            auxTransitions.addAll(getAllTransitions(s, underConstruction.getAlphabet()));
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

            //TODO check this -> I'm not sure if in this stage always be a new state.
            underConstruction.getStates().add(newState);
            //automatonUnderConstruction.addNewState(newState);

            //TODO cehck experiment


//            unitedIds.put(newState.getId(), ids);

/*
        //Group transition by symbol...
        //Use id of symbol because es possible exist 2 objects different with same id.
        HashMap<String, List<State>> transitionHash = new HashMap<>();
        for (Transition t : auxTransitions) {

            String symbolId = t.getSymbol().getId();
            if (!transitionHash.containsKey(symbolId)) {
                transitionHash.put(symbolId, new ArrayList<State>());
            }
            transitionHash.get(symbolId).add(t.getDestinationState());
        }*/


            //Create one transition per symbol...
//            List<Transition> transitionsFromHere = new ArrayList<>();
            for (String symbolId : transitionHash.keySet()) {

                UnionArg arg2 = new UnionArg();
                arg2.origin = newState;
                arg2.symbol = findSymbolById(underConstruction.getAlphabet(), symbolId);
                arg2.states = transitionHash.get(symbolId);


                argStack.push(arg2);

//                Transition newTransition = new Transition();
//                newTransition.setOriginState(newState);
//                newTransition.setSymbol(findSymbolById(underConstruction.getAlphabet(), symbolId));
//
//
//                newTransition.setDestinationState(uniteStates(transitionHash.get(symbolId), underConstruction));
//                transitionsFromHere.add(newTransition);
//
//
//                //TODO check this -> I'm not sure if in this stage always be a new transition.
//                underConstruction.getTransitions().add(newTransition);
//                //automatonUnderConstruction.addNewTransition(newTransition);

            }

//            newState.setTransitionsFromHere(transitionsFromHere);

        }

//        return newState;

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
            //TODO change name of minimizer app -> system
            State next = state.getNextState(sym, Constants.MINIMIZER_APP);
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


    //TODO clean this
//    /**
//     * Get transitions from the state well defined, replace the transitions with app parameter
//     * bad defined the parameter app and add the missing transition.
//     * <p/>
//     * A parameter well defined means that accept a specific value and don't other.
//     * <p/>
//     * Every state of an AFD must have defined one transition per every symbol of the alphabet.
//     * If this isn't fulfilled is necessary add loop transitions per every symbol not defined
//     * (missing transitions)
//     */

    /**
     * @return all transitions of state (even the "omitted" ones)
     */
    private static List<Transition> getAllTransitions(State state, List<Symbol> alphabet) {

        List<Transition> transitionList = new ArrayList<>();
        transitionList.addAll(state.getTransitionsFromHere());


        //Save the ids of all symbol that are in some transition
        List<String> symbolsFromHere = new ArrayList<>();
        for (Transition t : transitionList) {
            symbolsFromHere.add(t.getSymbol().getId());
        }

        //Add a new transition (loop) per each symbol that wasn't in any transition previously
        for (Symbol s : alphabet) {
            if (!symbolsFromHere.contains(s.getId())) {

                Transition newTransition = new Transition();
                newTransition.setOriginState(state);
                newTransition.setDestinationState(state);
                newTransition.setSymbol(s);

                transitionList.add(newTransition);
            }
        }


        return transitionList;

    }


}
