/**
 * 
 */
package edu.incense.android.survey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import edu.incense.android.R;
import edu.incense.android.project.JsonProject;
import edu.incense.android.project.Project;

/**
 * Service that runs surveys according to the project and user settings and
 * context.
 * 
 * @author Moises Perez (incense.cicese@gmail.com)
 * @version 0.1, 2011/05/31
 * 
 */
public class SurveyService extends IntentService {

    private static final String TAG = "SurveyService";

    /**
     * This constructor is never used directly, it is used by the superclass
     * methods when it's first created.
     */
    public SurveyService() {
        super("SurveyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadProject();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO Auto-generated method stub
    }

    /* INTENT_SERVICE METHODS */

    public final static String SURVEY_ACTION = "edu.incense.android.SURVEY_ACTION";
    public final static String SURVEY_ACTION_COMPLETE = "edu.incense.android.SURVEY_ACTION_COMPLETE";
    public final static String ACTION_ID_FIELDNAME = "action_id";
    public final static String SURVEY_NAME_FIELDNAME = "survey_name";

    /**
     * This method is invoked on the worker thread with a request to process.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Do not proceed if project wasn't loaded
        if (project == null) {
            Log.e(TAG, "Project is null. It wasn't loaded correctly.");
            return;
        }

        /* SURVEY ACTION */
        if (intent.getAction().compareTo(SURVEY_ACTION) == 0) {
            String surveyName = intent.getStringExtra(SURVEY_NAME_FIELDNAME);
            if (surveyName == null)
                surveyName = "mainSurvey";
            Survey survey = project.getSurvey(surveyName);
            if (survey == null) {
                Log.e(TAG, "Survey is null. Session [" + surveyName
                        + "] doesn't exist in the project.");
                return;
            }
            Log.d(TAG, "Starting survey action: " + surveyName);
            startSurvey(survey);
            Log.d(TAG, "Survey action [" + surveyName + "] finished");

            // Send broadcast the end of this process
            // TODO The following code is repeated, please improve.
            Intent broadcastIntent = new Intent(SURVEY_ACTION_COMPLETE);
            broadcastIntent.putExtra(ACTION_ID_FIELDNAME,
                    intent.getLongExtra(ACTION_ID_FIELDNAME, -1));
            sendBroadcast(broadcastIntent);
            Log.d(TAG, "Completion message for [" + surveyName
                    + "] was broadcasted");
        } else {
            Log.e(TAG, "Non-survey action received: " + intent.getAction());
            return;
        }
    }

    /* SESSION METHODS */
    // The project this device/user is assigned to.
    private Project project;

    /**
     * Reads project from JSON
     */
    private void loadPublicProject() {
        JsonProject jsonProject = new JsonProject();
        String projectFilename = getResources().getString(
                R.string.project_filename);
        String parentDirectory = getResources().getString(
                R.string.application_root_directory);
        File parent = new File(Environment.getExternalStorageDirectory(),
                parentDirectory);
        File file = new File(parent, projectFilename);
        project = jsonProject.getProject(file);
    }

    /**
     * Reads project from JSON
     */
    private void loadProject() {
        JsonProject jsonProject = new JsonProject();
        String projectFilename = getResources().getString(
                R.string.project_filename);
        InputStream input = null;
        try {
            input = this.openFileInput(projectFilename);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File [" + projectFilename + "] not found", e);
        }
        project = jsonProject.getProject(input);
    }

    private void startSurvey(Survey survey) {
        if (survey != null) {
            Log.i(getClass().getName(), "Starting survey");
            Intent intent = new Intent(this, SurveyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SurveyController surveyController = new SurveyController(survey);
            intent.putExtra(SurveyActivity.SURVEY_CONTROLLER, surveyController);
            startActivity(intent);
        } else {
            Log.i(TAG, "Survey was null.");
        }
    }

}