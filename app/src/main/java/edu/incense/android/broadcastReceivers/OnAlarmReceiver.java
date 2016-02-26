/**
 * 
 */
package edu.incense.android.broadcastReceivers;

import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import edu.incense.android.project.ProjectManager;
import edu.incense.android.session.SessionService;

/**
 * @author mxpxgx
 * 
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start service for it to run the recording session
        Intent projectManagerIntent = new Intent(context, ProjectManager.class);
        // Point out this action was triggered by a user
        projectManagerIntent.setAction(ProjectManager.PROJECT_START_ACTION);
        // Send unique id for this action
        long actionId = UUID.randomUUID().getLeastSignificantBits();
        projectManagerIntent.putExtra(ProjectManager.ACTION_ID_FIELDNAME,
                actionId);
        // startService(sessionServiceIntent);
        WakefulIntentService.sendWakefulWork(context, projectManagerIntent);
    }

}
