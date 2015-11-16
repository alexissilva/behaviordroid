package behaviordroid.listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexis on 04-06-15.
 */
public class LogcatEvent {

    private static final Pattern PATTERN_LOGCAT_THREADTIME = Pattern.compile(
            "^(\\d\\d-\\d\\d)" + //date
                    "\\s(\\d\\d:\\d\\d:\\d\\d\\.\\d+)" + //time
                    "\\s+(\\d+)" + //pid
                    "\\s+(\\d+)" + //pid
                    "\\s([VDIWEFS])" + //level
                    "\\s(.*?):" + //tag
                    "\\s+(.*)$"); //message

    private String rawText;
    private String date;
    private String time;
    private String PID;
    private String TID;
    private String level;
    private String tag;
    private String message;

    private Matcher matcherToReuse = PATTERN_LOGCAT_THREADTIME.matcher("");


    public boolean parseLog(String logText) {

        this.rawText = logText;

        Matcher matcher = matcherToReuse.reset(rawText);
        if (matcher.matches()) {
            this.date = matcher.group(1).trim();
            this.time = matcher.group(2).trim();
            this.PID = matcher.group(3).trim();
            this.TID = matcher.group(4).trim();
            this.level = matcher.group(5).trim();
            this.tag = matcher.group(6).trim();
            this.message = matcher.group(7);

            return true;

        } else {
            return false;
        }
    }

    public String getRawText() {
        return rawText;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPID() {
        return PID;
    }

    public String getTID() {
        return TID;
    }

    public String getLevel() {
        return level;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }
}
