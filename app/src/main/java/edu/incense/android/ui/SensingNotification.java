/**
 * 
 */
package edu.incense.android.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.incense.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * @author mxpxgx
 * 
 */
public class SensingNotification {
    private Context context;
    private NotificationManager manager;
    private List<String> activeSensors;
    private final static String CONTENT_TITLE = "InCense is active";
    private final static String CONTENT_HEADER = "Sensors: ";
    private final static int NOTIFICATION_ID = 1524;

    public SensingNotification(Context context) {
        this.context = context;
        manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        activeSensors = new ArrayList<String>();
    }

    private String createNotificationContent() {
        StringBuilder sb = new StringBuilder(CONTENT_HEADER);
        String div = ", ", end = ".";
        for (String sensor : activeSensors) {
            sb.append(sensor.toLowerCase() + div);
        }
        sb.replace(sb.length() - div.length(), sb.length(), end);
        return sb.toString();
    }
    
    private Notification createNotification() {
        int icon = R.drawable.bullseye;
        CharSequence tickerText = CONTENT_TITLE;
        long when = System.currentTimeMillis();

        return new Notification(icon, tickerText, when);
    }

    private PendingIntent createNotificationIntent() {
        // TODO CHANGE THIS ACTIVITy
        Intent intent = new Intent(context, RecordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        return pendingIntent;
    }
    
    public void add(String sensor) {
        if(!activeSensors.contains(sensor)){
            activeSensors.add(sensor);
        }
    }

    public void remove(String sensor) {
        activeSensors.remove(sensor);
    }

    public void updateNotificationWith(String sensor) {
        add(sensor);
        updateNotification();
    }

    public void updateNotificationWithout(String sensor) {
        remove(sensor);
        updateNotification();
    }

    public void updateNotification() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(notificationUpdater);
    }

    private Runnable notificationUpdater = new Runnable() {

        public void run() {
            if (activeSensors.isEmpty()) {
                manager.cancel(NOTIFICATION_ID);
            } else {
                // Title for the expanded status
                String contentTitle = CONTENT_TITLE;
                // Text to display in the extended status window
                String contentText = createNotificationContent();

                // Intent to launch an activity when the extended text is
                // clicked
                PendingIntent pendingIntent = createNotificationIntent();

                Notification notification = createNotification();
                notification.setLatestEventInfo(context, contentTitle,
                        contentText, pendingIntent);

                manager.notify(NOTIFICATION_ID, notification);
            }
        }

    };
}
