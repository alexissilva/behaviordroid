package behaviordroid.automaton.symbol;

import java.util.TreeMap;

/**
 * Created by Alexis on 03-06-15.
 */
public class Symbol {

    protected String id;
    protected SymbolStructure structure;

    //<IdParameter, Value>
    //The order is important to generate the same code
    protected TreeMap<String, String> parameterValues;


    public Symbol() {
        parameterValues = new TreeMap<>();
    }

    public String getId() {
        if(id == null){
            id = generateCode();
        }
        return id;
    }

    public SymbolStructure getStructure() {
        return structure;
    }

    public void setStructure(SymbolStructure structure) {
        this.structure = structure;
    }

    public TreeMap<String, String> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(TreeMap<String, String> parameterValues) {
        this.parameterValues = parameterValues;
    }

    /**
     * Create a unique code with the next format
     * Structure(Param1: Value1, Param2: Value2, ...)
     */
    private String generateCode() {
        String symbolString = structure.getId() + "(";
        int count = 1;
        for (String idParameter : parameterValues.keySet()) {
            if (count != 1) {
                symbolString += ",";
            }

            symbolString += idParameter + ":";
            symbolString += parameterValues.get(idParameter);

            if (count == parameterValues.size()) {
                symbolString += ")";
            }
            count++;
        }

        return symbolString;
    }


}
