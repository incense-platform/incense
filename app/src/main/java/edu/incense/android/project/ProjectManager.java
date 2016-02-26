/**
 * 
 */
package edu.incense.android.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import edu.incense.android.R;
import edu.incense.android.session.Session;
import edu.incense.android.session.SessionService;

/**
 * ProjectManager loads/read the project configuration and programs each of its
 * sessions accordingly. If a session is programmed to be started sometime in
 * the future, an alarm is used (AlarmManager). If the stating time of a session
 * has passed, it's started immediately.
 * 
 * Checks for updates from the server. If an update is available, any running
 * session is stopped to start new ones.
 * 
 * @author mxpxgx
 * 
 */
public class ProjectManager extends WakefulIntentService implements
		ProjectUpdateListener {
	private static final String TAG = "ProjectManager";
	public final static String PROJECT_START_ACTION = "edu.incense.android.PROJECT_START_ACTION";
	public final static String PROJECT_UPDATE_ACTION = "edu.incense.android.PROJECT_UPDATE_ACTION";
	public final static String ACTION_ID_FIELDNAME = "action_id";
	private volatile Project project;
	private Map<String, Session> sessions;
	private PendingIntent updateIntent;

	/**
	 * This constructor is never used directly, it is used by the superclass
	 * methods when it's first created.
	 */
	public ProjectManager() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		loadProject();
		// setUpdateAlarm();
		updateIntent = null;
		// Thread.setDefaultUncaughtExceptionHandler(onRuntimeError);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "ProjectManager destroyed");
	}

	/**
	 * 
	 * @see com.commonsware.cwac.wakeful.WakefulIntentService#doWakefulWork(android
	 *      .content.Intent)
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		// Do not proceed if project wasn't loaded
		if (getProject() == null) {
			Log.e(TAG, "Project is null. It wasn't loaded correctly.");
			return;
		}

		if (intent.getAction().compareTo(PROJECT_START_ACTION) == 0) {
			startProject();

		} else if (intent.getAction().compareTo(PROJECT_UPDATE_ACTION) == 0) {
			UpdateProjectTask updateTask = new UpdateProjectTask(this, this);
			updateTask.execute();
			Log.d(TAG, "Updating task started.");

		} else {
			Log.e(TAG, "Unknown action received: " + intent.getAction());
			return;
		}
	}

	private void startProject() {
		if (sessions == null || sessions.isEmpty()) {
			Log.e(TAG, "Project doesn't contain any session.");
			return;
		}

		// if(SessionService.isSessionRunning()){
		// Toast.makeText(getApplicationContext(),
		// "Session currently running, please wait...",
		// Toast.LENGTH_SHORT).show();
		// Log.d(TAG, "Session currently running, please wait...");
		// return;
		// }

		long currentTime = System.currentTimeMillis();
		for (Session session : sessions.values()) {
			if (currentTime >= session.getStartDate()) {
				startSession(session);
			} else {
				// this.setAlarmsFor(session);
			}
		}
	}

	private Intent createActionIntent(Class<?> cls, String action) {
		Intent projectIntent = new Intent(this, cls);
		// Point out this action was triggered by a user
		projectIntent.setAction(action);
		// Send unique id for this action
		long actionId = UUID.randomUUID().getLeastSignificantBits();
		projectIntent.putExtra(SessionService.ACTION_ID_FIELDNAME, actionId);
		return projectIntent;
	}

	private Intent createProjectActionIntent(String action) {
		return createActionIntent(ProjectManager.class, action);
	}

	private Intent generateSessionActionIntent(String action) {
		return createActionIntent(SessionService.class, action);
	}

	/* PROJECT UPDATE */

	/**
	 * Sends update action each day at midnight (12:00 AM).
	 */
	private void setUpdateAlarm() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if (updateIntent != null) {
			am.cancel(updateIntent);
		}
		Intent intent = createProjectActionIntent(PROJECT_UPDATE_ACTION);
		updateIntent = PendingIntent.getService(this, 0, intent, 0);
		// Set the trigger time to the next 12am occurrence (today or tomorrow)
		long triggerAtTime = obtainNextOcurranceOf(0, 0);
		// Interval of 24hrs
		long interval = 24L * 60L * 60L * 1000L;
		am.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, interval,
				updateIntent);
	}

	/**
	 * Obtains the time length in milliseconds before the next occurrence/event
	 * of the hour and minute specified.
	 * 
	 * @param hour
	 * @param minute
	 * @return
	 */
	private long obtainNextOcurranceOf(int hour, int minute) {
		Time now = new Time();
		now.setToNow();
		Time next = new Time();
		next.set(now);
		next.set(0, minute, hour, now.monthDay, now.month, now.year);
		if (!next.after(now)) {
			next.set(next.second, next.minute, next.hour, next.monthDay + 1,
					next.month, next.year);
		}
		return next.normalize(false);
	}

	/* RECORDING SESION */

	/**
	 * Set an alarm for recording sessions and surveys.
	 */
	public void setAlarmsFor(Session session) {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = generateSessionActionIntent(SessionService.SESSION_START_ACTION);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				0);
		// Set the trigger time to the next 12am occurrence (today or tomorrow)
		long triggerAtTime = session.getStartDate();

		if (session.isRepeat()) {
			long interval = getRepeatInterval(session);
			am.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
					interval, pendingIntent);
		} else {
			am.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pendingIntent);
		}
	}

	private long getRepeatInterval(Session session) {
		long interval = session.getDurationUnits();
		Session.RepeatType measure = Session.RepeatType.valueOf(session
				.getRepeatMeasure());

		switch (measure) {
		case MINUTES:
			interval = interval * 60L * 1000L;
			break;
		case HOURS:
			interval = interval * 60L * 60L * 1000L;
			break;
		case DAYS:
			interval = interval * 24L * 60L * 60L * 1000L;
			break;
		case WEEKS:
			interval = interval * 7L * 24L * 60L * 60L * 1000L;
			break;
		case MONTHS:
			interval = interval * 30L * 24L * 60L * 60L * 1000L;
			break;
		}

		return interval;
	}

	public void startSession(Session session) {
		// Start service for it to run the recording session
		Intent sessionServiceIntent = new Intent(this, SessionService.class);
		// Point out this action was triggered by a user
		sessionServiceIntent.setAction(SessionService.SESSION_START_ACTION);
		// Send unique id for this action
		long actionId = UUID.randomUUID().getLeastSignificantBits();
		sessionServiceIntent.putExtra(SessionService.ACTION_ID_FIELDNAME,
				actionId);
		sessionServiceIntent.putExtra(SessionService.SESSION_NAME_FIELDNAME,
				session.getName());
		WakefulIntentService.sendWakefulWork(this, sessionServiceIntent);
	}

	// public void stopSession() {
	// // Start service for it to run the recording session
	// Intent sessionServiceIntent = new Intent(this, SessionService.class);
	// // Point out this action was triggered by a user
	// sessionServiceIntent.setAction(SessionService.SESSION_STOP_ACTION);
	// // Send unique id for this action
	// long actionId = UUID.randomUUID().getLeastSignificantBits();
	// sessionServiceIntent.putExtra(SessionService.ACTION_ID_FIELDNAME,
	// actionId);
	// WakefulIntentService.sendWakefulWork(this, sessionServiceIntent);
	// }

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
		 input = this.openFileInput(projectFilename);
		 } catch (FileNotFoundException e) {
		 Log.e(TAG, "File [" + projectFilename + "] not found", e);
		 }

		 setProject(jsonProject.getProject(input));

		// UPDATE: to read project file from a ExternalStorageDirectory

		// Create/get reference to parent directory (/InCense/.project)
		//String parentDirectory = getResources().getString(
		//		R.string.application_root_directory)
			//	+ "/.project/";
		//Log.d(TAG, "Project config file directory: " + parentDirectory);
		//File parent = new File(Environment.getExternalStorageDirectory(),
			//	parentDirectory);
		//parent.mkdirs();

		// Create/get reference to actual file (/InCense/.project/project.json)
		//String projectFilename = "project.json";
		//File file = new File(parent, projectFilename);
		//FileInputStream fis = null;
		//try {
		//	fis = new FileInputStream(file);
		//} catch (FileNotFoundException e) {
		//	Log.e(TAG, "File [" + projectFilename + "] not found", e);
		//}

		//setProject(jsonProject.getProject(fis));

		// UPDATE ENDS HERE
		loadSessions();
	}

	private void loadSessions() {
		sessions = getProject().getSessions();
	}

	/**
	 * @see edu.incense.android.project.ProjectUpdateListener#update(edu.incense.android.project.Project)
	 */
	public void update(Project newProject) {
		// Stop SessionService
		Intent stopIntent = generateSessionActionIntent(SessionService.SESSION_STOP_ACTION);
		startService(stopIntent);

		// Reset project configuration
		setProject(newProject);
		loadSessions();

		// Register receiver to wait until is stopped
		IntentFilter intentFilter = new IntentFilter(
				SessionService.SESSION_STOP_ACTION_COMPLETE);
		this.registerReceiver(sessionStopReceiver, intentFilter);

	}

	private void unregisterReceiver() {
		ProjectManager.this.unregisterReceiver(sessionStopReceiver);
	}

	BroadcastReceiver sessionStopReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().compareTo(
					SessionService.SESSION_STOP_ACTION_COMPLETE) == 0) {
				// Start project
				startProject();
				unregisterReceiver();
			}
		}

	};

	/* In case of crashes */
	//
	// private Thread.UncaughtExceptionHandler onRuntimeError = new
	// Thread.UncaughtExceptionHandler() {
	// private long actionId;
	//
	// public void uncaughtException(Thread thread, Throwable ex) {
	// // Start service for it to run the recording session
	// Intent projectManagerIntent = new Intent(
	// ProjectManager.this.getApplicationContext(),
	// ProjectManager.class);
	// // Point out this action was triggered by a user
	// projectManagerIntent.setAction(ProjectManager.PROJECT_START_ACTION);
	// // Send unique id for this action
	// actionId = UUID.randomUUID().getLeastSignificantBits();
	// projectManagerIntent.putExtra(ProjectManager.ACTION_ID_FIELDNAME,
	// actionId);
	// // startService(sessionServiceIntent);
	// WakefulIntentService.sendWakefulWork(
	// ProjectManager.this.getApplicationContext(),
	// projectManagerIntent);
	// }
	// };

}
