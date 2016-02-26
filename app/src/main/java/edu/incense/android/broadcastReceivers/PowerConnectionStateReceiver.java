package edu.incense.android.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import static android.widget.Toast.LENGTH_LONG;

import edu.incense.android.results.SendFileTask;

/**
 * Created by xilef on 1/6/2016.
 */
public class PowerConnectionStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "Power change InCense.", Toast.LENGTH_LONG).show();
        if (SendFileTask.isPhoneReadyToSendFiles(context))
            SendFileTask.SendFiles(context);
        else
            if (SendFileTask.sendFileTaskStatus() == AsyncTask.Status.RUNNING)
                SendFileTask.CancelSendFiles();
    }
}
