package behaviordroid.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import behaviordroid.automaton.Automaton;
import behaviordroid.automaton.State;
import behaviordroid.automaton.Transition;
import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.util.Constants;
import behaviordroid.util.NonDeterministicException;

/**
 * Created by Alexis on 21-07-15.
 */
public class Minimizer {

    /**
     * Remove all 'loop' transitions (the from and to state is the same) from states and automaton.
     * Doesn't modify the alphabet.
     *
     * @param automaton
     */
    public static void removeLoops(Automaton automaton) {

        //remove from states
        for (State s : automaton.getStates()) {
            Iterator<Transition> it = s.getTransitionsFromHere().iterator();
            while (it.hasNext()) {
                Transition t = it.next();
                if (t.getDestinationState().getId().equals(s.getId())) {
                    it.remove();
                }
            }
        }

        //remove form automaton
        Iterator<Transition> it = automaton.getTransitions().iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            t.getId(); //todo clean? do this for a comparation more fair
            if (t.getDestinationState().getId().equals(t.getOriginState().getId())) {
                it.remove();
            }
        }
    }


    /**
     * Minimize a deterministic automaton.
     *
     * @param automaton deterministic.
     * @throws NonDeterministicException if the automaton is non-deterministic.
     */
    public static void minimizeAutomaton(Automaton automaton) throws NonDeterministicException {

        //step1
        removeDisconnectedState(automaton);

        //step2
        boolean[][] distinguishablePairs = getDistinguishablePairs(automaton);

        //step 3....
//        List<Transition> redirectedTransitions = new ArrayList<>();

        for (int i = 0; i < automaton.getStates().size(); i++) {
            State stateToKeep = automaton.getStates().get(i);
            if (stateToKeep == null) {
                //If it was "deleted" previously
                continue;
            }

            for (int j = 0; j < i; j++) {
                State stateToDelete = automaton.getStates().get(j);
                if (stateToDelete == null) {
                    continue;
                }


                //Join states...If aren't distinguishable and neither was deleted
                if (!distinguishablePairs[i][j]) {


                    //"delete" the state.. does it this way for doesn't change the order
                    automaton.getStates().set(j, null);

                    if(stateToDelete.isInitialState()){
                        stateToKeep.setInitialState(true);
                        automaton.setInitialState(stateToKeep);
                    }



                    //restart
//                    redirectedTransitions.clear();

                    //delete transitions from state-to-delete
                    // and redirect transitions to state-to-delete to state-to-keep.
                    Iterator<Transition> it = automaton.getTransitions().iterator();
                    while (it.hasNext()) {
                        Transition t = it.next();
                        State originState = t.getOriginState();
                        State destinationState = t.getDestinationState();

                        if (originState.getId().equals(stateToDelete.getId())) {
                            it.remove();

                        } else if (destinationState.getId().equals(stateToDelete.getId())) {
                            //remove the old transition from the list...

                            t.setDestinationState(stateToKeep);
//                            it.remove();

                            //In the case is not the same object, redirect the transition from the state too...
                            //But that never should be pass...So, check your code.
                            //The idea is reuse to reduce the memory consume
                            originState.redirectTransition(t.getSymbol().getId(), stateToKeep);
                            //redirect transition of the state...
//                            Transition redirectedTransition = originState.redirectTransition
//                                    (t.getSymbol().getId(), stateToKeep);

                            //save redirected transition to add to automaton later
//                            redirectedTransitions.add(redirectedTransition);
                        }
                    }
                    //add redirected transitions
//                    for (Transition t : redirectedTransitions) {
//                        automaton.getTransitions().addAll(redirectedTransitions);
//                    }


                }
            }
        }

        //delete definitely states...
        Iterator<State> it = automaton.getStates().iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                it.remove();
            }
        }

    }


    private static void removeDisconnectedState(Automaton automaton) {


        HashSet<String> connectedStates = new HashSet<>(); //save id
        LinkedList<State> stateStack = new LinkedList<>(); //to analyze transition from here

        stateStack.push(automaton.getInitialState());
        connectedStates.add(automaton.getInitialState().getId());

        while (stateStack.size() > 0) {

            State state = stateStack.pop();
            for (Transition t : state.getTransitionsFromHere()) {
                State state2 = t.getDestinationState();
                //add new connected states
                if (!connectedStates.contains(state2.getId())) {
                    stateStack.push(state2);
                    connectedStates.add(state2.getId());
                }
            }
        }

        //avoid do unnecessary work, continue only if exist some disconnected state...
        if (connectedStates.size() != automaton.getStates().size()) {

            //delete not connected states
            Iterator<State> it = automaton.getStates().iterator();
            while (it.hasNext()) {
                State s = it.next();
                if (!connectedStates.contains(s.getId())) {
                    it.remove();
                }
            }

            //delete (of the automaton) transitions from disconnected states
            Iterator<Transition> it2 = automaton.getTransitions().iterator();
            while (it2.hasNext()) {
                Transition t = it2.next();
                if (!connectedStates.contains(t.getOriginState().getId())) {
                    it2.remove();
                }
            }

            //all transition to disconnected state, came from other disconnected states
            //so, isn't necessary repeat that work.
        }


        //It's to BAD algorithm.
        /*boolean anyRemoved;
        boolean connected;
        do {
            anyRemoved = false;
            Iterator<State> it = automaton.getStates().iterator();
            while (it.hasNext()) {
                State s = it.next();

                //Initial is always connected
                if (!s.isInitialState()) {

                    connected = false;
                    for (Transition t : automaton.getTransitions()) {
                        if (t.getDestinationState().getId().equals(s.getId())) {
                            connected = true;
                            break;
                        }
                    }

                    if (!connected) {
                        it.remove();
                        anyRemoved = true;
                    }
                }
            }
        } while (anyRemoved);*/
    }

    private static boolean[][] getDistinguishablePairs(Automaton automaton) throws NonDeterministicException {


        int stateNumber = automaton.getStates().size();
        boolean distinguishablePairs[][] = new boolean[stateNumber][stateNumber];


        //First step: 2 states are distinguishable if have different behavior types...
        //We only use the half table..
        for (int i = 0; i < stateNumber; i++) {
            State state1 = automaton.getStates().get(i);
            for (int j = 0; j < i; j++) {
                State state2 = automaton.getStates().get(j);

                if (state1.getBehaviorType() != state2.getBehaviorType()) {
                    distinguishablePairs[i][j] = true;
                }

            }
        }

        //To know the position in the list of "next states"
//        HashMap<String, Integer> stateIndexHash = createStateIndexHash(automaton);
        HashMap<String, ArrayList<Integer>> nextStateIndexHash = createNextStateIndexHash(automaton);

        //Second step: 2 states are distinguishable if with a symbol go to different way (ie, go to distinguishable pair)
        boolean marked;
        do {
            marked = false;

            //Per every pair...
            for (int i = 0; i < stateNumber; i++) {

                //Get state 1 and get the indexes of all possible next states
                State state1 = automaton.getStates().get(i);
                List<Integer> indexNext1List = nextStateIndexHash.get(state1.getId());
//                List<Integer> indexNext1List = new ArrayList<>();
//                for (Symbol s : automaton.getAlphabet()) {
//                    State nextState1 = state1.getNextState(s, Constants.MINIMIZER_APP);
//                    if (nextState1 == null) {
//                        //If don't exist a transition with this symbol, then doesn't change of state
//                        nextState1 = state1;
//                    }
////                    int indexNext1 = getIndexOfStateWithId(automaton, nextState1.getId());
//                    int indexNext1 = stateIndexHash.get(nextState1.getId());
//                    indexNext1List.add(indexNext1);
//                }

                for (int j = 0; j < i; j++) {

                    //If are distinguishable, continue...
                    if (distinguishablePairs[i][j]) {
                        continue;
                    }


                    //Get state2 and the indexes of next states
                    State state2 = automaton.getStates().get(j);
                    List<Integer> indexNext2List = nextStateIndexHash.get(state2.getId());

                    //Check for every symbol if the pair go to a distinguishable pair.
                    for (int k = 0; k < automaton.getAlphabet().size(); k++) {

                        //Get the pre-calculated index of nextState1 and nextState2
                        int indexNext1 = indexNext1List.get(k);
                        int indexNext2 = indexNext2List.get(k);

//                        //Calculate the index of nextState2
//                        Symbol symbol = automaton.getAlphabet().get(k);
//                        State nextState2 = state2.getNextState(symbol, Constants.MINIMIZER_APP);
//                        if (nextState2 == null) {
//                            nextState2 = state2;
//                        }
////                        int indexNext2 = getIndexOfStateWithId(automaton, nextState2.getId());
//                        int indexNext2 = stateIndexHash.get(nextState2.getId());

                        //Check if "next pair" is distinguishable (in both side, because I don't
                        // know if indexNext2 is less than indexNext1 and only that side is filled)
                        if (distinguishablePairs[indexNext1][indexNext2]
                                || distinguishablePairs[indexNext2][indexNext1]) {

                            distinguishablePairs[i][j] = true;
                            marked = true;
                            break; //go to next pair
                        }
                    }


                }
            }

        } while (marked);
        //Third step: If in the second step at least one pair was marked, repeat...


        return distinguishablePairs;

    }


    /**
     * Gets the index (of the state list of automaton) of the state that has the id stateId.
     * <p/>
     * In this case the use of this function is recommended over indexOf (from interface List),
     * because it's possible have 2 different objects with the same id.
     */
    //TODO use another structure (hash)
    private static int getIndexOfStateWithId(Automaton automaton, String stateId) {

        for (int i = 0; i < automaton.getStates().size(); i++) {
            if (automaton.getStates().get(i).getId().equals(stateId)) {
                return i;
            }
        }

        return -1;
    }


    /**
     * It's better create a hash than check the list every time.
     */
    private static HashMap<String, Integer> createStateIndexHash(Automaton automaton) {

        HashMap<String, Integer> stateIndexHash = new HashMap<>();
        int length = automaton.getStates().size();
        for (int i = 0; i < length; i++) {
            stateIndexHash.put(automaton.getStates().get(i).getId(), i);
        }
        return stateIndexHash;
    }


    private static HashMap<String, ArrayList<Integer>> createNextStateIndexHash(Automaton automaton) throws NonDeterministicException {


        HashMap<String, ArrayList<Integer>> nexStateIndexHash = new HashMap<>();

        int alphabetSize = automaton.getAlphabet().size();
        HashMap<String, Integer> stateIndexHash = createStateIndexHash(automaton);

        for (State state : automaton.getStates()) {
            ArrayList nextStateIndexList = new ArrayList(alphabetSize);
            for (Symbol sym : automaton.getAlphabet()) {

                State nextState = state.getNextState(sym, Constants.MINIMIZER_APP);
                if (nextState == null) {
                    nextState = state;
                }
                int indexNext = stateIndexHash.get(nextState.getId());
                nextStateIndexList.add(indexNext);
            }
            nexStateIndexHash.put(state.getId(), nextStateIndexList);
        }

        return nexStateIndexHash;

    }
}
