/**
 * 
 */
package edu.incense.android.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.SurveyAnswersData;
import edu.incense.android.survey.AnswersContainer;
import edu.incense.android.survey.SurveyController;

/**
 * @author mxpxgx
 *
 */
public class SurveySensor extends Sensor{

    private static final String TAG = "SurveySensor";
    private IntentFilter filter;

    /**
     * @param context
     */
    public SurveySensor(Context context) {
        super(context);
        setName("Survey");
        filter = new IntentFilter();
        filter.addAction(SurveyController.SUERVEY_ANSWERS_ACTION);
    }
    
    /*** BROADCAST_RECEIVER ***/
    private BroadcastReceiver surveyAnswersReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(
                    SurveyController.SUERVEY_ANSWERS_ACTION) == 0) {
                
                    AnswersContainer container = (AnswersContainer)intent.getSerializableExtra(SurveyController.SURVEY_FIELDNAME);
                    if(container !=null) {
                        Data newData = new SurveyAnswersData(container);
                        // We interpret this as a data sensed
                        currentData = newData;
//                        Toast.makeText(getContext(), "Answers received by Survey Sensor",
//                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Serialized AnswersContainer receiver for survey named: "+container.getSurvey());
                    } else {
                        Log.e(TAG, "Serialized AnswersContainer was null.");
                    }
            }
        }

    };

    /**
     * @see edu.incense.android.sensor.Sensor#start()
     */
    @Override
    public synchronized void start() {
        super.start();
        getContext().registerReceiver(surveyAnswersReceiver, filter);
    }

    /**
     * @see edu.incense.android.sensor.Sensor#stop()
     */
    @Override
    public synchronized void stop() {
        super.stop();
        getContext().unregisterReceiver(surveyAnswersReceiver);
    }
}
