package edu.incense.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class StartResultsActivityTask extends AsyncTask<Void, Integer, Boolean> {
    public final static int STARTED = 0;
    public final static int COMPLETED = 2;
    public final static int CANCELED = 3;
    private Context context;

    public StartResultsActivityTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        publishProgress(STARTED);
        Intent intent = new Intent(context.getApplicationContext(),
                ResultsListActivity.class);
        context.startActivity(intent);
        publishProgress(COMPLETED);
        return true;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        publishProgress(CANCELED);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

}