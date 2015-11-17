package cl.alexis.testingbehavior;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import behaviordroid.DroidService;
import behaviordroid.automaton.Automaton;
import behaviordroid.automaton.State;
import behaviordroid.automaton.symbol.Symbol;
import behaviordroid.monitor.Monitor;
import behaviordroid.monitor.MonitorManager;
import behaviordroid.monitor.OnNewStateListener;
import behaviordroid.util.AdjustReadLogsPermission;
import behaviordroid.util.DroidConfiguration;
import behaviordroid.util.OnRunServiceListener;

public class TestingActivity extends AppCompatActivity {

    //Notification ids
    public static final int NOTIFICATION_RUNNING = 1;
    public static final int NOTIFICATION_RED = 2;
    public static final int NOTIFICATION_GREEN = 3;

    //Views
    private Button serviceButton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        //Ask permission to read logcat.
        AdjustReadLogsPermission.adjustIfNeeded(this);

        //Prepare views...
        serviceButton = (Button) findViewById(R.id.serviceButton);
        statusText = (TextView) findViewById(R.id.statusText);
        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (DroidService.getStatus()) {
                    case DroidService.STOPPED:
                        startService(new Intent(TestingActivity.this, DroidService.class));
                        break;
                    case DroidService.BOOTING:
                        //do nothing
                        break;
                    case DroidService.MONITORING:
                        stopService(new Intent(TestingActivity.this, DroidService.class));
                        break;
                }
            }
        });

        prepareService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(DroidService.getStatus());
    }

    private void updateUI(int status) {

        switch (status) {
            case DroidService.STOPPED:
                serviceButton.setEnabled(true);
                serviceButton.setText("ARRANCAR     BEHAVIORDROID");
                statusText.setText("Servicio apagado.");
                break;
            case DroidService.BOOTING:
                serviceButton.setEnabled(false);
                serviceButton.setText("CARGANDO     BEHAVIORDROID");
                statusText.setText("Booteando...");
                break;
            case DroidService.MONITORING:
                serviceButton.setEnabled(true);
                serviceButton.setText("APAGAR       BEHAVIORDROID");
                statusText.setText("Servicio corriendo.");
                break;
        }
    }

    private void prepareService() {

        /**
         * Config
         */
        DroidConfiguration.setMinimizeAllAutomatons(true);

        /**
         * Set Notification
         */
        Intent notificationIntent = new Intent(this, TestingActivity.class);
        PendingIntent contIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(TestingActivity.this)
                .setSmallIcon(R.drawable.shield_icon)
                .setLargeIcon((((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.shield_icon)).getBitmap()))
                .setContentTitle("BehaviorDroid")
                .setContentText("El servicio esta en ejecución")
                .setTicker("BehaviorDroid activado!")
                .setOngoing(true)
                .setContentIntent(contIntent)
                .build();

        DroidService.setNotification(NOTIFICATION_RUNNING, notification);

        /**
         * Life cycle
         */
        DroidService.setOnRunServiceListener(new OnRunServiceListener() {

            @Override
            public void onPreBoot(Service service) {
                updateUI(DroidService.BOOTING);
                Toast.makeText(service.getBaseContext(), "Cargando BehaviorDroid...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPreMonitor(Service service) {
                updateUI(DroidService.MONITORING);
                Toast.makeText(service.getBaseContext(), "Monitoreo activado!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Service service, Exception exception) {
                Toast.makeText(service.getBaseContext(), "BehaviorDroid Error: " + exception.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStopped(Service service) {
                updateUI(DroidService.STOPPED);
                Toast.makeText(service.getBaseContext(), "BehaviorDroid detenido.", Toast.LENGTH_SHORT).show();
            }
        });


        /**
         * Red and Green Behavior
         */

        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        MonitorManager.setOnRedStateListener(new OnNewStateListener() {
            @Override
            public void onNewState(Service service, Monitor monitor, String app, Automaton automaton, State newState, Symbol readSymbol, State oldState) {

                Notification redNotification = new NotificationCompat.Builder(TestingActivity.this)
                        .setSmallIcon(R.drawable.sign_error_icon)
                        .setLargeIcon((((BitmapDrawable) getResources()
                                .getDrawable(R.drawable.sign_error_icon)).getBitmap()))
                        .setContentTitle("Comportamiento indeseado")
                        .setContentText("La aplicación " + app + " tiene un comportamiento indeseado")
                        .setTicker("Comportamiento indeseado!").build();

                manager.notify(NOTIFICATION_RED, redNotification);
            }
        });

        MonitorManager.setOnGreenStateListener(new OnNewStateListener() {
            @Override
            public void onNewState(Service service, Monitor monitor, String app, Automaton automaton, State newState, Symbol readSymbol, State oldState) {

                Notification greenNotification = new NotificationCompat.Builder(TestingActivity.this)
                        .setSmallIcon(R.drawable.sign_check_icon)
                        .setLargeIcon((((BitmapDrawable) getResources()
                                .getDrawable(R.drawable.sign_check_icon)).getBitmap()))
                        .setContentTitle("Comportamiento deseado")
                        .setContentText("La aplicación " + app + " cumple con el comportamiento deseado")
                        .setTicker("Comportamiento deseado!").build();

                manager.notify(NOTIFICATION_GREEN, greenNotification);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_testing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
