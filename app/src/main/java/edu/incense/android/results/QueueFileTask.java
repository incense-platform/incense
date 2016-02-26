package edu.incense.android.results;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class QueueFileTask extends AsyncTask<ResultFile, Void, Integer> {
    private Context context;

    public QueueFileTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Integer doInBackground(ResultFile... params) {
        ResultsUploader resultsUploader = new ResultsUploader(context);
        for (ResultFile rf : params) {
            resultsUploader.offerFile(rf);
        }
        return params.length;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result > 0) {
            Toast.makeText(context,
                    result + " files were added to the uploader queue.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
