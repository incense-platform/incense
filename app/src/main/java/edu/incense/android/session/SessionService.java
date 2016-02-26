package edu.incense.android.session;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import edu.incense.android.R;
import edu.incense.android.project.JsonProject;
import edu.incense.android.project.Project;

/**
 * Service that runs recording sessionsaccording to the project and user
 * settings and context.
 * 
 * 
 * @author Moises Perez (mxpxgx@gmail.com)
 * @version 0.6, 2011/05/31
 * 
 */
public class SessionService extends WakefulIntentService implements
        SessionCompletionListener {// extends
    // IntentService {
    private static final String TAG = "SessionService";

    // private static volatile boolean sessionRunning = false;

//    /**
//     * @return the sessionRunning
//     */
//    public boolean isSessionRunning() {
//        return sessionRunning;
//    }

    /**
     * This constructor is never used directly, it is used by the superclass
     * methods when it's first created.
     */
    public SessionService() {
        super("SessionService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // sessionRunning = false;
        loadProject();
        Thread.setDefaultUncaughtExceptionHandler(onRuntimeError);
        Log.d(TAG, "SessionService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SessionService destroyed");
    }

    /* INTENT_SERVICE METHODS */

    public final static String SESSION_START_ACTION = "edu.incense.android.SESSION_START_ACTION";
    public final static String SESSION_STOP_ACTION = "edu.incense.android.SESSION_STOP_ACTION";
    public final static String SESSION_START_ACTION_COMPLETE = "edu.incense.android.SESSION_START_ACTION_COMPLETE";
    public final static String SESSION_STOP_ACTION_COMPLETE = "edu.incense.android.SESSION_STOP_ACTION_COMPLETE";
    public final static String ACTION_ID_FIELDNAME = "action_id";
    public final static String SESSION_NAME_FIELDNAME = "session_name";
    private long actionId;

    /**
     * This method is invoked on the worker thread with a request to process.
     */
    protected void doWakefulWork(Intent intent) {
        // protected synchronized void onHandleIntent(Intent intent) {
        // Do not proceed if project wasn't loaded
        if (getProject() == null) {
            Log.e(TAG, "Project is null. It wasn't loaded correctly.");
            return;
        }

        /* SESSION ACTION */
        if (intent.getAction().compareTo(SESSION_START_ACTION) == 0) {
            // if (sessionRunning) {
//            if (isWorking()) {
//                Toast.makeText(getApplicationContext(),
//                        "Session currently running, please wait...",
//                        Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "Session currently running, please wait...");
//                return;
//            }

            // Get session
            String sessionName = intent.getStringExtra(SESSION_NAME_FIELDNAME);
            if (sessionName == null)
                sessionName = "mainSession";
            Session session = getProject().getSession(sessionName);
            if (session == null) {
                Log.e(TAG, "Session is null. Session [" + sessionName
                        + "] doesn't exist in the project.");
                return;
            }

            // Start session
            Log.d(TAG, "Starting session action: " + sessionName);
            startSession(session);

            actionId = intent.getLongExtra(ACTION_ID_FIELDNAME, -1);
            // Send broadcast the end of this process
            Intent broadcastIntent = new Intent(SESSION_START_ACTION_COMPLETE);
            broadcastIntent.putExtra(ACTION_ID_FIELDNAME, actionId);
            sendBroadcast(broadcastIntent);
            Log.d(TAG, "Session start action [" + sessionName + "] finished");

        } else if (intent.getAction().compareTo(SESSION_STOP_ACTION) == 0) {
            if (controller == null) {
                Log.e(TAG,
                        "SessionController is null. There's nothing to stop.");
                return;
            }
//            if (!isWorking()){//sessionRunning) {
//                Log.e(TAG,
//                        "SessionService is not running any session. There's nothing to stop.");
//                return;
//            }
            actionId = intent.getLongExtra(ACTION_ID_FIELDNAME, -1);
            String sessionName = controller.getSessionName();
            Log.d(TAG, "Stopping session: " + sessionName);
            stopSession();
        } else {
            Log.e(TAG, "Unknown action received: " + intent.getAction());
            return;
        }
    }

    /* SessionCompletionListener */

    private void sessionCompletion(String sessionName) {
        Log.d(TAG, "Session [" + sessionName + "] stopped");
        // Send broadcast the end of this process
        Intent broadcastIntent = new Intent(SESSION_STOP_ACTION_COMPLETE);
        broadcastIntent.putExtra(ACTION_ID_FIELDNAME, actionId);
        sendBroadcast(broadcastIntent);
        Log.d(TAG, "SessionService stop broadcasted");
//        sessionRunning = false;
//        setWorking(false);
    }

    /**
     * @see edu.incense.android.session.SessionCompletionListener#completedSession(java.lang.String,
     *      long)
     */
    public void completedSession(String sessionName, long activeTime) {
        sessionCompletion(sessionName);
    }

    /* SESSION METHODS */

    // The project this device/user is assigned to.
    private volatile Project project;
    private SessionController controller;

    /**
     * @return the project
     */
    private synchronized Project getProject() {
        return project;
    }

    /**
     * @param project
     *            the project to set
     */
    private synchronized void setProject(Project project) {
        this.project = project;
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
            input = SessionService.this.openFileInput(projectFilename);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File [" + projectFilename + "] not found", e);
        }
        setProject(jsonProject.getProject(input));
    }

    private void startSession(Session session) {
//        sessionRunning = true;
//        setWorking(true);
        controller = new SessionController(this, session);
        Log.d(TAG, "Session controller initiated");
        controller.start();
        Log.d(TAG, "Session started");
    }

    private void stopSession() {
        if (controller != null)
            controller.stop();
//        sessionRunning = false;
//        setWorking(false);
    }

    /* In case of crashes */

    private Thread.UncaughtExceptionHandler onRuntimeError = new Thread.UncaughtExceptionHandler() {
        private long actionId;

        public void uncaughtException(Thread thread, Throwable ex) {
            // Start service for it to run the recording session
            Intent sessionServiceIntent = new Intent(
                    SessionService.this.getApplicationContext(),
                    SessionService.class);
            // Point out this action was triggered by a user
            sessionServiceIntent.setAction(SessionService.SESSION_START_ACTION);
            // Send unique id for this action
            actionId = UUID.randomUUID().getLeastSignificantBits();
            sessionServiceIntent.putExtra(SessionService.ACTION_ID_FIELDNAME,
                    actionId);
            // startService(sessionServiceIntent);
            WakefulIntentService.sendWakefulWork(
                    SessionService.this.getApplicationContext(),
                    sessionServiceIntent);
        }
    };

}
