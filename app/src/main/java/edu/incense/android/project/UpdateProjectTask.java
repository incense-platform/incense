package edu.incense.android.project;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class UpdateProjectTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private ProjectUpdateListener listener;

    public UpdateProjectTask(Context context) {
        super();
        this.context = context;
        this.listener = null;
    }

    public UpdateProjectTask(Context context, ProjectUpdateListener listener) {
        this(context);
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ProjectUpdater updater = new ProjectUpdater(context);
        if(listener != null){
            updater.setListener(listener);
        }
        return updater.updateProject();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result == true) {
            Toast.makeText(context, "The project has been updated.",
                    Toast.LENGTH_LONG).show();
        }
    }

}
