package behaviordroid.automaton.symbol;


import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Alexis on 03-06-15.
 */
public abstract class SymbolStructure {

    protected String id;
    protected String name;
    protected String description;

    //<ID, AcceptedValues>
    protected HashMap<String, Pattern> parameterDescriptions;

    public SymbolStructure() {
        this.parameterDescriptions = new HashMap<>();
    }

    public String getId() {
        return id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Pattern> getParameterDescriptions() {
        return parameterDescriptions;
    }

    public void setParameterDescriptions(HashMap<String, Pattern> parameterDescriptions) {
        this.parameterDescriptions = parameterDescriptions;
    }

    public abstract boolean isWellDefined();
}
