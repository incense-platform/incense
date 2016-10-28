package edu.incense.android.datatask;

import java.util.List;

import android.content.Context;
import android.hardware.SensorManager;
import edu.incense.android.datatask.data.BatteryStateData;
import edu.incense.android.datatask.data.WifiNetworkData;
import edu.incense.android.datatask.filter.AccelerometerMeanFilter;
import edu.incense.android.datatask.filter.FalseTimerFilter;
import edu.incense.android.datatask.filter.Loader.ComponentLoader;
import edu.incense.android.datatask.filter.MovementFilter;
import edu.incense.android.datatask.filter.MovementTimeFilter;
import edu.incense.android.datatask.filter.ShakeFilter;
import edu.incense.android.datatask.filter.WifiTimeConnectedFilter;
import edu.incense.android.datatask.model.Task;
import edu.incense.android.datatask.sink.AudioSink;
import edu.incense.android.datatask.sink.DataSink;
import edu.incense.android.datatask.sink.JsonSinkWritter;
import edu.incense.android.datatask.sink.RawAudioSinkWritter;
import edu.incense.android.datatask.trigger.Condition;
import edu.incense.android.datatask.trigger.GeneralTrigger;
import edu.incense.android.datatask.trigger.JsonTrigger;
import edu.incense.android.datatask.trigger.StopTrigger;
import edu.incense.android.datatask.trigger.SurveyTrigger;
import edu.incense.android.sensor.AccelerometerSensor;
import edu.incense.android.sensor.AccelerometerSensor_old;
import edu.incense.android.sensor.BareAccelerometerSensor;
import edu.incense.android.sensor.AudioSensor;
import edu.incense.android.sensor.BatteryLevelSensor;
import edu.incense.android.sensor.BatteryStateSensor;
import edu.incense.android.sensor.BluetoothConnectionSensor;
import edu.incense.android.sensor.BluetoothSensor;
import edu.incense.android.sensor.CallSensor;
import edu.incense.android.sensor.GpsSensor;
import edu.incense.android.sensor.GyroscopeSensor;
import edu.incense.android.sensor.ImuSensor;
import edu.incense.android.sensor.NfcSensor;
import edu.incense.android.sensor.PhoneCallSensor;
import edu.incense.android.sensor.PhoneStateSensor;
import edu.incense.android.sensor.PowerConnectionSensor;
import edu.incense.android.sensor.ScreenSensor;
import edu.incense.android.sensor.Sensor;
import edu.incense.android.sensor.SmsSensor;
import edu.incense.android.sensor.SurveySensor;
import edu.incense.android.sensor.TimerSensor;
import edu.incense.android.sensor.WifiConnectionSensor;
import edu.incense.android.sensor.WifiNetworkSensor;
import edu.incense.android.sensor.WifiScanSensor;
import edu.incense.android.sensor.OrientationSensor;

public class DataTaskFactory {
    public static DataTask createDataTask(Task task, Context context) {
        DataTask dataTask = null;

        switch (task.getTaskType()) {
        case AccelerometerSensor_old:
            long frameTime = task.getLong(
                    AccelerometerSensor_old.ATT_FRAMETIME, 1000);
            long duration = task.getLong(AccelerometerSensor_old.ATT_DURATION,
                    500);
            Sensor sensor = AccelerometerSensor_old.createAccelerometer(
                    context, frameTime, duration);
            if (task.getSampleFrequency() > 0) {
                sensor.setSampleFrequency(task.getSampleFrequency());
            } else if (task.getPeriodTime() > 0) {
                sensor.setPeriodTime(task.getPeriodTime());
            }
            dataTask = new DataSource(sensor);
            task.setPeriodTime(1000);
            task.setSampleFrequency(-1.0f);
            break;
        case AccelerometerSensor:
            long frameTime_as = task.getLong(
                    AccelerometerSensor.ATT_FRAMETIME, 1000);
            int sensorDelay_as = task.getInt(AccelerometerSensor.ATT_SENSOR_DELAY,
                    SensorManager.SENSOR_DELAY_GAME);
            Sensor sensor_as = AccelerometerSensor.createAccelerometer(
                    context, frameTime_as, sensorDelay_as);
            if (task.getSampleFrequency() > 0) {
                sensor_as.setSampleFrequency(task.getSampleFrequency());
            } else if (task.getPeriodTime() > 0) {
                sensor_as.setPeriodTime(task.getPeriodTime());
            }
            dataTask = new DataSource(sensor_as);
            task.setPeriodTime(1000);
            task.setSampleFrequency(-1.0f);
            break;
        case BareAccelerometerSensor:
            Sensor bSensor = new BareAccelerometerSensor(context);
            if (task.getSampleFrequency() > 0)
                bSensor.setSampleFrequency(task.getSampleFrequency());
            dataTask = new DataSource(bSensor);
            break;
        case TimerSensor:
            long period = task.getLong("period", 1000);
            TimerSensor ts = new TimerSensor(context, period);
            dataTask = new DataSource(ts);
            ts.addSourceTask((DataSource) dataTask);
            break;
        case AudioSensor:
            long audioDuration = task.getLong("duration", -1);
            AudioSensor as = new AudioSensor(context, task.getSampleFrequency());
            dataTask = new AudioDataSource(as, audioDuration);
            as.addSourceTask((AudioDataSource) dataTask); // AudioSensor is
                                                          // faster than
                                                          // DataTask
            break;
        case BluetoothSensor:
            dataTask = new DataSource(new BluetoothSensor(context));
            break;
        case BluetoothConnectionSensor:
            dataTask = new DataSource(new BluetoothConnectionSensor(context,
                    task.getString("address", "")));
            break;
        case GpsSensor:
            dataTask = new DataSource(new GpsSensor(context));
            break;
        case GyroscopeSensor:
//            long frameTime_gs = task.getLong(
//                    AccelerometerSensor.ATT_FRAMETIME, 1000);
//            int sensorDelay_gs = task.getInt(AccelerometerSensor.ATT_SENSOR_DELAY,
//                    SensorManager.SENSOR_DELAY_GAME);
//            Sensor sensor_gs = AccelerometerSensor.createGyroscope(
//                    context, frameTime_gs, sensorDelay_gs);
//            if (task.getSampleFrequency() > 0) {
//                sensor_gs.setSampleFrequency(task.getSampleFrequency());
//            } else if (task.getPeriodTime() > 0) {
//                sensor_gs.setPeriodTime(task.getPeriodTime());
//            }
//            dataTask = new DataSource(sensor_gs);
//            task.setPeriodTime(frameTime_gs);
//            task.setSampleFrequency(-1.0f);
            GyroscopeSensor gSensor = GyroscopeSensor.createGyroscope(context);
            if (task.getSampleFrequency() > 0){
                gSensor.setSampleFrequency(task.getSampleFrequency());
            }
            dataTask = new DataSource(gSensor);

            break;
        case OrientationSensor:
            OrientationSensor oSensor = new OrientationSensor(context);
            if (task.getSampleFrequency() > 0){
                oSensor.setSampleFrequency(task.getSampleFrequency());
            }
            dataTask = new DataSource(oSensor);
            break;
        case ImuSensor:
            ImuSensor iSensor = new ImuSensor(context);
            if (task.getSampleFrequency() > 0){
                iSensor.setSampleFrequency(task.getSampleFrequency());
            }
            dataTask = new DataSource(iSensor);
            break;
        case CallSensor:
            dataTask = new DataSource(new CallSensor(context));
            break;
        case SmsSensor:
            dataTask = new DataSource(new SmsSensor(context));
            break;
        case ScreenSensor:
            dataTask = new DataSource(new ScreenSensor(context));
            break;
        case PhoneStateSensor:
            dataTask = new DataSource(new PhoneStateSensor(context));
            break;
        case PowerConnectionSensor:
            dataTask = new DataSource(new PowerConnectionSensor(context));
            break;
        case BatteryStateSensor:
            dataTask = new DataSource(new BatteryStateSensor(context));
            break;
        case NfcSensor:
            dataTask = new DataSource(new NfcSensor(context));
            break;
        case BatteryLevelSensor:
        	dataTask = new DataSource (new BatteryLevelSensor(context));
        	break;
        case SurveySensor:
            dataTask = new DataSource(new SurveySensor(context));
            break;
        case WifiScanSensor:
            dataTask = new DataSource(new WifiScanSensor(context));
            break;
        case WifiConnectionSensor:
            // String[] ap = task.getStringArray("accessPoints");
            // List<String> apList = Arrays.asList(ap);
            dataTask = new DataSource(new WifiConnectionSensor(context));
            break;
        case AccelerometerMeanFilter:
            dataTask = new AccelerometerMeanFilter();
            break;
        case WifiNetworkSensor:
            WifiNetworkSensor wns = new WifiNetworkSensor(context);
            wns.setPeriodTime(task.getPeriodTime());
            dataTask = new DataSource(wns);
            break;
        case StepsAccFilter:
            dataTask = new AccelerometerMeanFilter();
            break;
        case DataSink:
            // Set SinkWritter type (Json)
            // It will write results to a JSON file
            int bufferSize = task.getInt(
                    DataSink.ATT_BUFFER_SIZE, 60);
            dataTask = new DataSink(new JsonSinkWritter(context), bufferSize);
            ((DataSink) dataTask).setName(task.getName());
            break;
        case AudioSink:
            // Set SinkWritter type (Json)
            // It will write results to a RAW file
            dataTask = new AudioSink(new RawAudioSinkWritter(context));
            ((DataSink) dataTask).setName(task.getName());
            break;
        case ShakeFilter:
            dataTask = new ShakeFilter();
            break;
        case MovementFilter:
            double threshold = task.getDouble("threshold", 1000);
            dataTask = new MovementFilter();
            ((MovementFilter) dataTask).setMovementThreshold((float) threshold);
            break;
        case FalseTimerFilter:
            long timeLength = task.getLong("timeLength", 1000);
            String attributeName = task.getString("attributeName", "");
            dataTask = new FalseTimerFilter();
            ((FalseTimerFilter) dataTask).setTimeLength(timeLength);
            ((FalseTimerFilter) dataTask).setAttributeName(attributeName);
            break;
        case MovementTimeFilter:
            long maxNoInput = task.getLong("maxNoInput", 30000L);
            long maxNoMovement = task.getLong("maxNoMovement", 5000L);
            dataTask = new MovementTimeFilter();
            ((MovementTimeFilter) dataTask).setMaxNoInput(maxNoInput);
            ((MovementTimeFilter) dataTask).setMaxNoMovement(maxNoMovement);
            break;
        case WifiTimeConnectedFilter:
            dataTask = new WifiTimeConnectedFilter();
            break;
        case SurveyTrigger:
            dataTask = new SurveyTrigger(context);
            ((SurveyTrigger) dataTask).setSurveyName("mainSurvey");// task.getString("surveyName",
            break;
        case Trigger:
            String matches = task.getString(JsonTrigger.MATCHES, null);
            JsonTrigger jsonTrigger = new JsonTrigger();
            List<Condition> conditionsList = jsonTrigger.toConditions(task
                    .getJsonNode());
            dataTask = new GeneralTrigger(context, conditionsList, matches);
            break;
        case StopTrigger:
            String matches2 = task.getString(JsonTrigger.MATCHES, null);
            JsonTrigger jsonTrigger2 = new JsonTrigger();
            List<Condition> conditionsList2 = jsonTrigger2.toConditions(task
                    .getJsonNode());
            dataTask = new StopTrigger(context, conditionsList2, matches2);
            break;
        case DataFilter:
            ComponentLoader cl = new ComponentLoader(context, task.getComponentName(),
                    task.getComponentID(), task.getCampaignID());
            dataTask = cl.createInstanceOfComponent();
            break;
        default:
            return null;
        }
        if (task.getSampleFrequency() > 0) {
            dataTask.setSampleFrequency(task.getSampleFrequency());
        } else if (task.getPeriodTime() > 0) {
            dataTask.setPeriodTime(task.getPeriodTime());
        }
        dataTask.setTaskType(task.getTaskType());
        dataTask.setName(task.getName());
        dataTask.setTriggered(task.isTriggered());
        return dataTask;
    }
}
