package edu.incense.android.results;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.widget.Toast;

public class SendFileTask extends AsyncTask<Void, Void, Integer> {
    private Context context;
    private static SendFileTask sft;

    public SendFileTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        ResultsUploader resultsUploader = new ResultsUploader(context);
        return resultsUploader.sendFiles();
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result > 0) {
            Toast.makeText(context, result + " files were uploaded.",
                    Toast.LENGTH_LONG).show();
        }

    }

    // Sends the files that are pending to upload to the server.
    public static void SendFiles(Context context){
        sft = new SendFileTask(context);
        sft.execute();
    }

    public static void CancelSendFiles(){
        if (sft != null)
            sft.cancel(true);
    }

    public static AsyncTask.Status sendFileTaskStatus(){
        if (sft != null)
            return sft.getStatus();

        return Status.FINISHED;
    }

    // Checks whether phone state is connected through wifi and charging.
    public static Boolean isPhoneReadyToSendFiles(Context context){
        // Get connectivity status.
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        // Get energy status
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = context.registerReceiver(null, iFilter);
        int energyStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = energyStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                energyStatus == BatteryManager.BATTERY_STATUS_FULL;

        // Check if connected to WiFi and charging.
        if (ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI
                && isCharging){
            return true;
        }

        return false;
    }
}
