/**
 * 
 */
package edu.incense.android.datatask.trigger;

import java.util.List;

import edu.incense.android.datatask.DataTask;
import edu.incense.android.datatask.data.Data;

import android.content.Context;

/**
 * @author mxpxgx
 *
 */
public class StopTrigger extends GeneralTrigger{

    /**
     * @param context
     * @param conditions
     * @param matches
     */
    public StopTrigger(Context context, List<Condition> conditions,
            String matches) {
        super(context, conditions, matches);
    }

    /**
     * @see edu.incense.android.datatask.trigger.GeneralTrigger#trigger(edu.incense.android.datatask.data.Data)
     */
    @Override
    protected void trigger(Data data) {
        for(DataTask task: tasksToStart){
            if(task.isRunning()){
                task.stop();
            }
        }
        this.pushToOutputs(data);
    }
    
    

}
