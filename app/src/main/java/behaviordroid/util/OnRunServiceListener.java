package behaviordroid.util;

import android.app.Service;

/**
 * Created by Alexis on 07-09-15.
 */
public interface OnRunServiceListener {

    void onPreBoot(Service service);

    void onPreMonitor(Service service);

    void onException(Service service, Exception exception);

    void onStopped(Service service);
}
