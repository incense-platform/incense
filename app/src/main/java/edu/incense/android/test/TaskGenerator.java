/**
 * 
 */
package edu.incense.android.test;

import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import edu.incense.android.datatask.model.Task;
import edu.incense.android.datatask.model.TaskType;
import edu.incense.android.datatask.trigger.Condition;
import edu.incense.android.datatask.trigger.JsonTrigger;
import edu.incense.android.sensor.AccelerometerSensor_old;

/**
 * @author mxpxgx
 *
 */
public class TaskGenerator {
    
    public static Task createTask(ObjectMapper mapper, String name, TaskType type){
        Task task = new Task();
        task.setName(name);
        task.setTaskType(type);
        task.setTriggered(false);
        return task;
    }
    
    public static Task createTask(ObjectMapper mapper, String name, TaskType type, int sampleFrequency){
        Task task = TaskGenerator.createTask(mapper, name, type);
        task.setSampleFrequency(sampleFrequency); 
        return task;
    }
    
    public static Task createTaskWithPeriod(ObjectMapper mapper, String name, TaskType type, long period){
        Task task = TaskGenerator.createTask(mapper, name, type);
        task.setPeriodTime(period);
        return task;
    }

    
    public static Task createGpsSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "GpsSensor", TaskType.GpsSensor, period);
        return task;
    }
    
    public static Task createBatteryLevelSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "BatteryLevelSensor", TaskType.BatteryLevelSensor, period);
        return task;
    }
    
    public static Task createStatePhoneSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "PhoneStateSensor", TaskType.PhoneStateSensor, period);
        return task;
    }
    
    public static Task createBatteryStateSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "BatteryStateSensor", TaskType.BatteryStateSensor, period);
        return task;
    }
    
    public static Task createSmsSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "SmsSensor", TaskType.SmsSensor, period);
        return task;
    }
    
    public static Task createScreenSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "ScreenSensor", TaskType.ScreenSensor, period);
        return task;
    }
    
    
    public static Task createCallSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "CallSensor", TaskType.CallSensor, period);
        return task;
    }
    
    public static Task createPowerConnectionSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "PowerConnectionSensor", TaskType.PowerConnectionSensor, period);
        return task;
    }
    
    public static Task createTimerSensor(ObjectMapper mapper, long sourcePeriod, long sensorPeriod){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "TimerSensor", TaskType.TimerSensor, sourcePeriod);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put("period", sensorPeriod); 
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createAudioSensor(ObjectMapper mapper, int sampleFrequency, long duration){
        Task task = TaskGenerator.createTask(mapper, "AudioSensor", TaskType.AudioSensor, sampleFrequency);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put("duration", duration);
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createNfcSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "NfcSensor", TaskType.NfcSensor, period);
        return task;
    }
    
    public static Task createSurveySensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "SurveySensor", TaskType.SurveySensor, period);
        return task;
    }
    
    public static Task createAccelerometerSensor(ObjectMapper mapper, int sampleFrequency, long frameTime, long duration){
        Task task = TaskGenerator.createTask(mapper, "AccelerometerSensor_old", TaskType.AccelerometerSensor_old, sampleFrequency);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put(AccelerometerSensor_old.ATT_FRAMETIME, frameTime);
        ((ObjectNode) extrasNode).put(AccelerometerSensor_old.ATT_DURATION, duration);
        task.setJsonNode(extrasNode);
        return task;
    }

    public static Task createBareAccelerometerSensor(ObjectMapper mapper, int sampleFrequency){
        Task task = TaskGenerator.createTask(mapper, "BareAccelerometerSensor", TaskType.BareAccelerometerSensor, sampleFrequency);
        return task;
    }

    public static Task createImuSensor(ObjectMapper mapper, int sampleFrequency){
        Task task = TaskGenerator.createTask(mapper, "ImuSensor", TaskType.ImuSensor, sampleFrequency);
        return task;
    }

    public static Task createGyroscopeSensor(ObjectMapper mapper, int sampleFrequency){
        Task task = TaskGenerator.createTask(mapper, "Gyroscope", TaskType.GyroscopeSensor, sampleFrequency);
        return task;
    }

    public static Task createOrientationSensor(ObjectMapper mapper, int sampleFrequency){
        return TaskGenerator.createTask(mapper, "Orientation", TaskType.OrientationSensor, sampleFrequency);
    }
    
    public static Task createWifiConnectionSensor(ObjectMapper mapper, long period, String[] ap){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "WifiConnectionSensor", TaskType.WifiConnectionSensor, period);
        JsonNode accessPoints = mapper.createObjectNode();
        ArrayNode array = ((ObjectNode) accessPoints).putArray("accessPoints");
        for(int i=0; i<ap.length; i++){
            array.add(ap[i]);
        }
        task.setJsonNode(accessPoints);
        return task;
    }

    public static Task createWifiNetworkSensor(ObjectMapper mapper, long periodTime){
        Task task = createTaskWithPeriod(mapper, "WifiNetworkSensor", TaskType.WifiNetworkSensor, periodTime);
        return task;
    }
    
    private static Task createTrigger(ObjectMapper mapper, String name, TaskType type, long period, String matches, ArrayList<Condition> conditions){
        Task task = createTaskWithPeriod(mapper, name, type, period);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put(JsonTrigger.MATCHES, matches);
        JsonNode conditionsNode = mapper.valueToTree(conditions);
        ((ObjectNode) extrasNode).put(JsonTrigger.CONDITIONS, conditionsNode);
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createTrigger(ObjectMapper mapper, String name, long period, String matches, ArrayList<Condition> conditions){
        Task task = createTrigger(mapper, name, TaskType.Trigger, period, matches, conditions);
        return task;
    }
    
    public static Task createStopTrigger(ObjectMapper mapper, String name, long period, String matches, ArrayList<Condition> conditions){
        Task task = createTrigger(mapper, name, TaskType.StopTrigger, period, matches, conditions);
        return task;
    }
    
    public static Condition createCondition(String...p){
        Condition c = new Condition();
        if(p.length > 0 && p[0]!=null){
            c.setData(p[0]);
        }
        if(p.length > 1 && p[1]!=null){
            c.setType(p[1]);
        }
        if(p.length > 2 && p[2]!=null){
            c.setOperator(p[2]);
        }
        if(p.length > 3 && p[3]!=null){
            c.setOperator(p[3]);
        }
        if(p.length > 4 && p[4]!=null){
            c.setValue1(p[4]);
        }
        if(p.length > 5 && p[5]!=null){
            c.setValue2(p[5]);
        }
        return c;
    }
    
    public static Task createMovementFilter(ObjectMapper mapper, long timePeriod, double threshold){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "MovementFilter", TaskType.MovementFilter, timePeriod);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put("threshold", threshold); 
        task.setJsonNode(extrasNode);
        return task;
    }

    public static Task createFalseTimerFilter(ObjectMapper mapper, long timePeriod, float timeLength, String attributeName){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "FalseTimerFilter", TaskType.FalseTimerFilter, timePeriod);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put("timeLength", timeLength); 
        ((ObjectNode) extrasNode).put("attributeName", attributeName); 
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createMovementTimeFilter(ObjectMapper mapper, long timePeriod, long maxNoInput, long maxNoMovement){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "MovementTimeFilter", TaskType.MovementTimeFilter, timePeriod);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put("maxNoInput", maxNoInput); 
        ((ObjectNode) extrasNode).put("maxNoMovement", maxNoMovement); 
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createStepsCounterFilter(ObjectMapper mapper, long timePeriod){
        Task task = TaskGenerator.createTaskWithPeriod(mapper,"StepsAccFilter", TaskType.StepsAccFilter, timePeriod);
        JsonNode extrasNode = mapper.createObjectNode(); 
        task.setJsonNode(extrasNode);
        return task;
    }

    public static Task CreateDataFilter(ObjectMapper mapper, String taskName, String componentName,
                                        String componentID, String campaignID){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, taskName, TaskType.DataFilter, 1);
        task.setComponentName(componentName);
        task.setComponentID(componentID);
        task.setCampaignID(campaignID);
        return task;
    }
    
    public static Task createDataSink(ObjectMapper mapper, int bufferSize){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "DataSink",
                TaskType.DataSink, 5);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put("bufferSize", bufferSize);
        task.setJsonNode(extrasNode);
        return task;
    }


}
