package edu.incense.android.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import edu.incense.android.results.SendFileTask;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by xilef on 1/6/2016.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "Network changed InCense", LENGTH_LONG).show();
        if (SendFileTask.isPhoneReadyToSendFiles(context))
            SendFileTask.SendFiles(context);
        else
            if (SendFileTask.sendFileTaskStatus() == AsyncTask.Status.RUNNING)
                SendFileTask.CancelSendFiles();
    }
}
