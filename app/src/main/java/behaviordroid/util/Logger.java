package behaviordroid.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Alexis on 17-06-15.
 */
public class Logger {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS";

    private static File file;

    public static synchronized void write(String stringToWrite){

        if(file == null) {
            file = new File(Configuration.getLogPath());
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String timeStamp = sdf.format(calendar.getTime());

        try {
            FileOutputStream output = new FileOutputStream(file, true);
            output.write((timeStamp + " - " + stringToWrite + "\n").getBytes());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
