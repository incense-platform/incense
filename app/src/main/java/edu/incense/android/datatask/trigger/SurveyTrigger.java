package edu.incense.android.datatask.trigger;

import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.others.BooleanData;
import edu.incense.android.survey.SurveyService;

public class SurveyTrigger extends DataTrigger {
    private String surveyName;
    private long actionId;

    // private boolean trigger;

    public SurveyTrigger(Context context) {
        super(context);
        // trigger =false;
    }

    @Override
    protected void trigger() {
        // (new StartSurveyTask(context)).execute(surveyName);
        // Start service for it to run the recording session
        Intent surveyIntent = new Intent(context, SurveyService.class);
        // Point out this action was triggered by a user
        surveyIntent.setAction(SurveyService.SURVEY_ACTION);
        // Send unique id for this action
        actionId = UUID.randomUUID().getLeastSignificantBits();
        surveyIntent.putExtra(SurveyService.ACTION_ID_FIELDNAME, actionId);
        if (surveyName != null){
            surveyIntent.putExtra(SurveyService.SURVEY_NAME_FIELDNAME,
                    surveyName);
        }
        context.startService(surveyIntent);
    }

    protected void computeSingleData(Data data) {
        BooleanData bData = (BooleanData) data;
        if (bData.getValue()) {
            // trigger = true;
            trigger();
            // stop();
        }
    }

    /*
     * @Override public void stop(){ if(trigger) trigger(); super.stop(); }
     */

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyName() {
        return surveyName;
    }

}
