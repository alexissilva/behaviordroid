package behaviordroid.automaton.symbol;

import behaviordroid.util.Constants;

/**
 * Created by Alexis on 07-07-15.
 */
public class SymbolStructureLogcat extends SymbolStructure {

    //Where is the app parameter?
    public static final int NO_APP = 0; //The structure doesn't have parameter app.
    public static final int PID = 1; //Use "ps" to get the app
    public static final int MESSAGE = 1; //Use message syntax to get app

    private int appLocation;

    private String level;
    private String tag;
    private MessageSyntax messageSyntax;

    public SymbolStructureLogcat() {
        super();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public MessageSyntax getMessageSyntax() {
        return messageSyntax;
    }

    public void setMessageSyntax(MessageSyntax messageSyntax) {
        this.messageSyntax = messageSyntax;
    }

    public int getAppLocation() {
        return appLocation;
    }

    public void setAppLocation(int appLocation) {
        this.appLocation = appLocation;
    }

    @Override
    public boolean isWellDefined() {

        boolean hasId = id != null && !id.isEmpty();
        boolean hasLevel = level != null && !level.isEmpty();
        boolean hasTag = tag != null && !tag.isEmpty();
        boolean hasMessage = messageSyntax != null && messageSyntax.getExpression() != null;
        if (!hasId || !hasLevel || !hasTag || !hasMessage) {
            return false;
        }

        boolean hasApp = parameterDescriptions.get(Constants.APP_PARAMETER_ID) != null;
        switch (appLocation) {
            case NO_APP:
                return !hasApp && areGroupsWellDefined();
            case PID | MESSAGE:
                return hasApp && areGroupsWellDefined();
            default:
                return false;
        }
    }

    private boolean areGroupsWellDefined() {

        for (String param : parameterDescriptions.keySet()) {

            if (appLocation == PID && param.equals(Constants.APP_PARAMETER_ID)) {
                continue;
            }

            Integer group = messageSyntax.getCatchGroups().get(param);
            if (group == null || group < 0) {
                return false;
            }
        }
        return true;
    }

}
