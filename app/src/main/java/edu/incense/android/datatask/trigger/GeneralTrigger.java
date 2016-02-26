/**
 * 
 */
package edu.incense.android.datatask.trigger;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.incense.android.datatask.DataTask;
import edu.incense.android.datatask.Input;
import edu.incense.android.datatask.InputEnabledTask;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.survey.SurveyService;

/**
 * @author mxpxgx
 * 
 */
public class GeneralTrigger extends DataTask implements InputEnabledTask  {
    private final static String TAG = "GeneralTrigger";
    protected List<DataTask> tasksToStart;
    protected List<String> surveysToStart;
    protected List<String> sessionsToStart;
    private List<Condition> conditions;
    private boolean matchesAll;
    protected Context context;

    public enum DataType {
        BOOLEAN, NUMERIC, TEXT, DATE, TYPE, DATA
    }

    public static final String[] matches = { "all", "any" };
    public static final String[] booleanOperators = { "is true", "is false" };
    public static final String[] textOperators = { "contains",
            "does not contain", "is", "is not", "starts with", "ends with" };
    public static final String[] numericOperators = { "is", "is not",
            "is greater than", "is less than", "is in the range" };
    public static final String[] dateOperators = { "is", "is not", "is after",
            "is before", "is in the last", "is not in the last",
            "is in the range" };

    public GeneralTrigger(Context context, List<Condition> conditions, String matches) {
        this.context = context;
        clear();
        this.conditions = conditions;
        // Case "all"
        if (matches.compareTo(GeneralTrigger.matches[0]) == 0) {
            matchesAll = true;
        }
//        // Case "any"
//        else if (matches.compareTo(GeneralTrigger.matches[1]) == 0) {
//            matchesAll = false;
//        }
        // default
        else {
            matchesAll = false;
        }
        
        tasksToStart = new ArrayList<DataTask>();
        surveysToStart = new ArrayList<String>();
        sessionsToStart = new ArrayList<String>();
    }
    
    public void addTask(DataTask task){
        tasksToStart.add(task);
    }
    
    public void addSurvey(String surveyName){
        surveysToStart.add(surveyName);
    }
    
    public void addSession(String sessionName){
        sessionsToStart.add(sessionName);
    }

    protected void trigger(Data data){
        if(tasksToStart.isEmpty()){
            Log.d(TAG, "There aren't tasks to trigger.");
        }
        for(DataTask task: tasksToStart){
            if(!task.isRunning()){
                Log.d(TAG, "Triggering task... "+task.getClass());
                task.start();
            } else {
                Log.d(TAG, "Already running..."+task.getClass());
            }
        }
        for(String surveyName: surveysToStart){
            startSurvey(surveyName);
        }
        this.pushToOutputs(data);
    }

    protected void startSurvey(String surveyName){
        Log.d(TAG, "Triggering survey...");
        Intent surveyIntent = new Intent(context, SurveyService.class);
        // Point out this action was triggered by a user
        surveyIntent.setAction(SurveyService.SURVEY_ACTION);
        // Send unique id for this action
        long actionId = UUID.randomUUID().getLeastSignificantBits();
        surveyIntent.putExtra(SurveyService.ACTION_ID_FIELDNAME, actionId);
        if (surveyName != null){
            surveyIntent.putExtra(SurveyService.SURVEY_NAME_FIELDNAME,
                    surveyName);
            context.startService(surveyIntent);
        }
    }
    private void startSession(){
        //TODO session trigger
    }

    @Override
    protected void compute() {
        Data tempData;
        for (Input i : inputs) {
            //Log.i(getClass().getName(), "Asking for new data");
            tempData = i.pullData();
            if (tempData != null) {
                computeSingleData(tempData);
                //Log.i(getClass().getName(), "GOOD");
            } else {
                //Log.i(getClass().getName(), "BAD");
            }
        }
    }

    protected void computeSingleData(Data data){
        boolean valid = validate(data);
        if(valid){
            Log.d(TAG, getName()+" is valid!");
            trigger(data);
        } else {
            Log.d(TAG, getName()+" is invalid!");
        }
    }

    @Override
    public void addInput(Input i) {
        super.addInput(i);
    }
    
    /* *** VALIDATION METHODS *** */

    public boolean validate(Data data) {
        for (Condition c : conditions) {
            boolean isValid = validate(data, c);
            if (matchesAll && !isValid) {
                return false;
            } else if (!matchesAll && isValid) {
                return true;
            }
        }
        if (matchesAll)
            return true;
        else
            return false;
    }

    private boolean validate(Data data, Condition condition) {
        DataType type = DataType.valueOf(condition.getType());
        switch (type) {
        case BOOLEAN:
            return validateBoolean(data, condition);
        case NUMERIC:
            return validateNumeric(data, condition);
        case TEXT:
            return validateText(data, condition);
        case DATE:
            return validateDate(data, condition);
        case TYPE:
            return validateType(data, condition);
        case DATA:
            return validateData(data, condition);
        }
        return false;
    }

    /**
     * @param data
     * @param condition
     * @return
     */
    private boolean validateData(Data data, Condition condition) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @param data
     * @param condition
     * @return
     */
    private boolean validateType(Data data, Condition condition) {
        return validateText(data, condition);
    }

    /**
     * @param data
     * @param condition
     * @return
     */
    private boolean validateDate(Data data, Condition condition) {
        Long value = getLong(data, condition.getData());
        if (value == null) {
            return false;
        }

        // "is", "is not", "is after",
        // "is before", "is in the last", "is not in the last",
        // "is in the range"

        // Case "is"
        if (condition.getOperator().compareTo(dateOperators[0]) == 0) {
            return value == Long.valueOf(condition.getValue1()).longValue();
        }
        // Case "is not"
        else if (condition.getOperator().compareTo(dateOperators[1]) == 0) {
            return value != Long.valueOf(condition.getValue1()).longValue();
        }
        // Case "is after"
        else if (condition.getOperator().compareTo(dateOperators[2]) == 0) {
            return value > Long.valueOf(condition.getValue1()).longValue();
        }
        // Case "is before"
        else if (condition.getOperator().compareTo(dateOperators[3]) == 0) {
            return value < Long.valueOf(condition.getValue1()).longValue();
        }
//        // Case "is in the last"
//        else if (condition.getOperator().compareTo(dateOperators[4]) == 0) {
//            return value < Long.valueOf(condition.getValue1());
//        }
//        // Case "is not in the last"
//        else if (condition.getOperator().compareTo(dateOperators[5]) == 0) {
//            boolean b1 = Long.valueOf(condition.getValue1()) < value;
//            boolean b2 = value < Long.valueOf(condition.getValue2());
//            return b1 && b2;
//        }
        // Case "is in the range"
        else if (condition.getOperator().compareTo(dateOperators[6]) == 0) {
            boolean b1 = Long.valueOf(condition.getValue1()).longValue() < value;
            boolean b2 = value < Long.valueOf(condition.getValue2()).longValue();
            return b1 && b2;
        } else {
            return false;
        }
    }

    private Long getLong(Data data, String fieldName) {
        boolean extraExists = data.getExtras().containsKey(fieldName);
        if (extraExists) {
            return data.getExtras().getLong(fieldName);
        } else {
            boolean fieldExists = fieldExists(data, fieldName);
            if (fieldExists) {
                Field field = getField(data, fieldName);
                if (field == null) {
                    return null;
                }
                try {
                    field.setAccessible(true);
                    Long value = field.getLong(data);
                    return value;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "IllegalArgumentException", e);
                    return null;
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "IllegalAccessException", e);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * @param data
     * @param condition
     * @return
     */
    private boolean validateText(Data data, Condition condition) {
        String value = getString(data, condition.getData());
        if (value == null) {
            return false;
        }

        // Case "contains"
        if (condition.getOperator().compareTo(textOperators[0]) == 0) {
            return value.contains(String.valueOf(condition.getValue1()));
        }
        // Case "does not contain"
        else if (condition.getOperator().compareTo(textOperators[1]) == 0) {
            return !value.contains(String.valueOf(condition.getValue1()));
        }
        // Case "is"
        else if (condition.getOperator().compareTo(textOperators[2]) == 0) {
            return value.compareTo(String.valueOf(condition.getValue1())) == 0;
        }
        // Case "is not"
        else if (condition.getOperator().compareTo(textOperators[3]) == 0) {
            return !(value.compareTo(String.valueOf(condition.getValue1())) == 0);
        }
        // Case "starts with"
        else if (condition.getOperator().compareTo(textOperators[4]) == 0) {
            return value.startsWith(String.valueOf(condition.getValue1()));
        }
        // Case "ends with"
        else if (condition.getOperator().compareTo(textOperators[5]) == 0) {
            return value.endsWith(String.valueOf(condition.getValue1()));
        } else {
            return false;
        }
    }

    private String getString(Data data, String fieldName) {
        boolean extraExists = data.getExtras().containsKey(fieldName);
        if (extraExists) {
            return data.getExtras().getString(fieldName);
        } else {
            boolean fieldExists = fieldExists(data, fieldName);
            if (fieldExists) {
                Field field = getField(data, fieldName);
                if (field == null) {
                    return null;
                }
                try {
                    field.setAccessible(true);
                    String value = (String) field.get(data);
                    return value;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "IllegalArgumentException", e);
                    return null;
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "IllegalAccessException", e);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * @param data
     * @param condition
     * @return
     */
    private boolean validateNumeric(Data data, Condition condition) {
        Float value = getFloat(data, condition.getData());
        if (value == null) {
            return false;
        }

        // Case "is"
        if (condition.getOperator().compareTo(numericOperators[0]) == 0) {
            return value == Float.valueOf(condition.getValue1()).floatValue();
        }
        // Case "is not"
        else if (condition.getOperator().compareTo(numericOperators[1]) == 0) {
            return value != Float.valueOf(condition.getValue1()).floatValue();
        }
        // Case "is greater than"
        else if (condition.getOperator().compareTo(numericOperators[2]) == 0) {
            Log.d(TAG, "Compare: "+value+" > "+Float.valueOf(condition.getValue1()).floatValue());
            return value > Float.valueOf(condition.getValue1()).floatValue();
        }
        // Case "is less than"
        else if (condition.getOperator().compareTo(numericOperators[3]) == 0) {
            return value < Float.valueOf(condition.getValue1()).floatValue();
        }
        // Case "is in the range"
        else if (condition.getOperator().compareTo(numericOperators[4]) == 0) {
            boolean b1 = Float.valueOf(condition.getValue1()).floatValue() < value;
            boolean b2 = value < Float.valueOf(condition.getValue2()).floatValue();
            return b1 && b2;
        } else {
            return false;
        }
    }

    private Float getFloat(Data data, String fieldName) {
        boolean extraExists = data.getExtras().containsKey(fieldName);
        if (extraExists) {
            return Float.valueOf(data.getExtras().getString(fieldName));
        } else {
            boolean fieldExists = fieldExists(data, fieldName);
            if (fieldExists) {
                Field field = getField(data, fieldName);
                if (field == null) {
                    return null;
                }
                Type type=null;
                try {
                    type = field.getGenericType();
                    Log.d(TAG, "Casting to Float from: "+type);
                    field.setAccessible(true);
                    float value = field.getFloat(data);
                    return value;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "IllegalArgumentException", e);
                    return null;
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "IllegalAccessException", e);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * @param data
     * @param condition
     * @return
     */
    private boolean validateBoolean(Data data, Condition condition) {
        Boolean value = getBoolean(data, condition.getData());
        if (value == null) {
            return false;
        }

        // Case "is true"
        if (condition.getOperator().compareTo(booleanOperators[0]) == 0) {
            return value;
        }
        // Case "is false"
        else if (condition.getOperator().compareTo(booleanOperators[1]) == 0) {
            return !value;
        } else {
            return false;
        }
    }

    private Boolean getBoolean(Data data, String fieldName) {
        boolean extraExists = data.getExtras().containsKey(fieldName);
        if (extraExists) {
            return data.getExtras().getBoolean(fieldName);
        } else {
            boolean fieldExists = fieldExists(data, fieldName);
            if (fieldExists) {
                Field field = getField(data, fieldName);
                if (field == null) {
                    return null;
                }
                try {
                    field.setAccessible(true);
                    boolean value = field.getBoolean(data);
                    return value;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "IllegalArgumentException", e);
                    return null;
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "IllegalAccessException", e);
                    return null;
                }
            }
        }
        return null;
    }

    private Field getField(Data data, String fieldName) {
        Class dataClass = data.getClass();
        try {
            Field field = dataClass.getDeclaredField(fieldName);
            return field;
        } catch (SecurityException e) {
            Log.e(TAG, "SecurtyException", e);
            return null;
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "NoSuchFieldException", e);
            return null;
        }
    }

    private boolean fieldExists(Data data, String fieldName) {
        Class dataClass = data.getClass();
        Field[] fields = dataClass.getDeclaredFields();
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < fields.length; i++) {
            names.add(fields[i].getName());
        }
        if (names.contains(fieldName)) {
            return true;
        } else {
            return false;
        }
    }

}
