package behaviordroid;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import behaviordroid.listener.LogcatListener;
import behaviordroid.listener.ProcessListener;
import behaviordroid.util.BootLoader;
import behaviordroid.util.Logger;
import behaviordroid.util.Configuration;
import behaviordroid.util.Globals;
import behaviordroid.util.OnRunServiceListener;

/**
 * Created by Alexis on 03-09-15.
 */
public class DroidService extends Service {


    public static final int STOPPED = 0;
    public static final int BOOTING = 1;
    public static final int MONITORING = 2;

    private Handler handler;
    private static int status;
    private static OnRunServiceListener onRunServiceListener;

    private static int notificationId;
    private static Notification notification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.write("Started Service.");


        //Config service...
        if(notification == null){
            onException(new IllegalArgumentException("Notification of service must not be null."));
        }else{
            startForeground(notificationId, notification);
        }
        handler = new Handler();

        //TODO do this inside the bootloader¿?
        Globals.getInstance().setService(this);

        //Start boot
        new AsyncBoot().execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        status = STOPPED;
        Logger.write("Stopped Service.");

        if (onRunServiceListener != null) {
            onRunServiceListener.onStopped(this);
        }

        ProcessListener.stopListening();
        LogcatListener.stopListening();

    }

    public synchronized void onException(Exception exception) {

        handler.post(new OnExceptionRunnable(exception));
    }

    public static int getStatus() {
        return status;
    }

    public static void setOnRunServiceListener(OnRunServiceListener onRunServiceListener) {
        DroidService.onRunServiceListener = onRunServiceListener;
    }

    public static void setNotification(int notificationId, Notification notification) {
        if(notificationId == 0){
            throw new IllegalArgumentException("Notification ID must not be 0.");
        }
        if(notification == null){
            throw new IllegalArgumentException("Notification must not be null.");
        }
        DroidService.notificationId = notificationId;
        DroidService.notification = notification;
    }

    private class AsyncBoot extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            if (onRunServiceListener != null) {
                onRunServiceListener.onPreBoot(DroidService.this);
            }
            status = BOOTING;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                BootLoader.boot();
                return true;

            } catch (Exception e) {
                Logger.write("Error booting! " + e.toString());
                e.printStackTrace();
                onException(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {

            if (success) {
                if (onRunServiceListener != null) {
                    onRunServiceListener.onPreMonitor(DroidService.this);
                }
                status = MONITORING;

                if (Configuration.isListenToSystemCalls()) {
                    ProcessListener.startListening();
                }

                if (Configuration.isListenToLogcat()) {
                    LogcatListener.startListening();
                }
            }
        }
    }

    private class OnExceptionRunnable implements Runnable {
        Exception exception;

        public OnExceptionRunnable(Exception exception) {
            this.exception = exception;
        }

        @Override
        public void run() {
            if (onRunServiceListener != null) {
                onRunServiceListener.onException(DroidService.this, exception);
            }
            stopSelf();
        }
    }
}
