package edu.incense.android;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.IntentFilter;
import android.util.Log;

import edu.incense.android.broadcastReceivers.NetworkStateReceiver;
import edu.incense.android.datatask.DataTask;
import edu.incense.android.test.ProjectGenerator;

/**
 * InCenseApplication is a subclass of android.app.Application, a base class for
 * maintaining global application state. In this case, it maintains a
 * SurveyController instance to... TODO This is no the best approach, I need
 * something like the The Static Starter Pattern
 * http://fupeg.blogspot.com/2011/02/static-starter-pattern.html
 * 
 * @author Moises Perez (mxpxgx@gmail.com)
 * @since 2011/04/10 //TODO probably before
 * @version 1.1, 2011/05/20
 * 
 */

public class InCenseApplication extends Application {
    // Static singleton of this application
//    private static InCenseApplication singleton; 
    // Temporary SurveyController reference to be started
//    private SurveyController surveyController;
    private Map<String, DataTask> taskCollection; // TODO
    private NetworkStateReceiver nsr;
    /**
     * Returns this application instance (an static singleton).
     * 
     * @return the instance of this application (an static singleton).
     */
//    public static InCenseApplication getInstance() {
//        return singleton;
//    }

    /**
     * @see android.app.Application#onCreate()
     */
    @Override
    public final void onCreate() {
        super.onCreate();
        //ProjectGenerator.buildProjectJsonP(this);
        ProjectGenerator.buildProjectJsonNutrition(this);
        Log.i(getClass().getName(), "Project.json saved");
//        singleton = this;
        taskCollection = new HashMap<String, DataTask>();
//        surveyController = null;
//        IntentFilter i = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        nsr = new NetworkStateReceiver();
//        this.registerReceiver(nsr,i);
    }
    
    public Map<String, DataTask> getTaskCollection() {
        return taskCollection;
    }
}
