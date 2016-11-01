/**
 * 
 */
package edu.incense.android.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Environment;
import android.text.format.Time;
import edu.incense.android.R;
import edu.incense.android.datatask.filter.WifiTimeConnectedFilter;
import edu.incense.android.datatask.model.Task;
import edu.incense.android.datatask.model.TaskRelation;
import edu.incense.android.datatask.model.TaskType;
import edu.incense.android.datatask.trigger.Condition;
import edu.incense.android.datatask.trigger.GeneralTrigger;
import edu.incense.android.project.Project;
import edu.incense.android.sensor.WifiConnectionSensor;
import edu.incense.android.session.Session;
import edu.incense.android.survey.Survey;

/**
 * Project examples for testing
 * 
 * @author Moises Perez (mxpxgx@gmail.com)
 * 
 */
public class ProjectGenerator {
	/**
	 * Project Alan tesis
	 */
	
	public static void biuldProjectTesisAlan(Context context){
		 ObjectMapper mapper = new ObjectMapper();

					// Session
		Session session = new Session();
		session.setDurationUnits(24L * 10L); // 10days
		session.setDurationMeasure("hours"); 
					// TaskList
		List<Task> tasks = new ArrayList<Task>();
					// Sensors
		Task callSensor = TaskGenerator.createCallSensor(mapper, 1000);
		callSensor.setTriggered(false);
		tasks.add(callSensor);
		//
		Task screenSensor = TaskGenerator.createScreenSensor(mapper, 1000);
		screenSensor.setTriggered(false);
		tasks.add(screenSensor);
		//
		Task smsSensor = TaskGenerator.createSmsSensor(mapper, 1000);
		smsSensor.setTriggered(false);
		tasks.add(smsSensor);
		//
		Task batteryLevelSensor = TaskGenerator.createBatteryLevelSensor(mapper, 10);
		batteryLevelSensor.setTriggered(false);
		tasks.add(batteryLevelSensor);
		//
		Task batteryStateSensor = TaskGenerator.createBatteryStateSensor(mapper, 10);
		batteryLevelSensor.setTriggered(false);
		//
		Task phoneStateSensor = TaskGenerator.createStatePhoneSensor(mapper, 1000);
		phoneStateSensor.setTriggered(false);
		tasks.add(phoneStateSensor);
		//                
		Task gpsSensor = TaskGenerator.createGpsSensor(mapper, 1000L * 30L);
		gpsSensor.setTriggered(true);
		tasks.add(gpsSensor);
		//
		Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,1000, new String[] { "WifiSsid" });
		tasks.add(wifiSensor);
		
						//Conditions & Triggers
		Condition ifNotConnected = TaskGenerator.createCondition(WifiConnectionSensor.ATT_ISCONNECTED,GeneralTrigger.DataType.BOOLEAN.name(),GeneralTrigger.booleanOperators[1]); // "is false"
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		conditions.add(ifNotConnected);
		//
		Task gpsTrigger = TaskGenerator.createTrigger(mapper, "GpsTrigger",1000, GeneralTrigger.matches[0], conditions);
		tasks.add(gpsTrigger);
		//
		Condition ifConnected = TaskGenerator.createCondition(WifiConnectionSensor.ATT_ISCONNECTED,GeneralTrigger.DataType.BOOLEAN.name(),GeneralTrigger.booleanOperators[0]); // "is true"
		conditions = new ArrayList<Condition>();
		conditions.add(ifConnected);
		//
		Task gpsStopTrigger = TaskGenerator.createStopTrigger(mapper,"GpsStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
		tasks.add(gpsStopTrigger);               
						// Sink        
		Task dataSink = TaskGenerator.createDataSink(mapper, 60);
		tasks.add(dataSink);
						//Relations
		List<TaskRelation> relations = Arrays.asList(new TaskRelation[] {
		new TaskRelation(wifiSensor.getName(), gpsTrigger.getName()),
		new TaskRelation(gpsTrigger.getName(), gpsSensor.getName()),
		new TaskRelation(wifiSensor.getName(), gpsStopTrigger.getName()),
		new TaskRelation(gpsStopTrigger.getName(), gpsSensor.getName()),
		new TaskRelation(wifiSensor.getName(), dataSink.getName()),
		new TaskRelation(gpsSensor.getName(), dataSink.getName()),  
		new TaskRelation(callSensor.getName(), dataSink.getName()),
		new TaskRelation(smsSensor.getName(), dataSink.getName()),
		new TaskRelation(batteryStateSensor.getName(), dataSink.getName()),
		new TaskRelation(screenSensor.getName(), dataSink.getName())});
						//Sessions
		session.setTasks(tasks);
		session.setRelations(relations);
						//Project
		Project project = new Project();
		project.setSessionsSize(1);
		project.put("mainSession", session);
		project.setSurveysSize(0);
		
		writeProject(context, mapper, project);
	}
    /**
     * Audio project
     */
    public static void buildProjectJsonA(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(60);
        session.setDurationMeasure("minutes");
        session.setAutoTriggered(true);
        Time time = new Time();
        time.setToNow();
        time.set(time.monthDay - 1, time.month, time.year);
        session.setStartDate(time.normalize(false));

        time.setToNow();
        time.set(time.monthDay + 7, time.month, time.year);
        session.setEndDate(time.normalize(false));

        session.setName("GPS Session");

        List<Task> tasks = new ArrayList<Task>();

        Task audioSensor = TaskGenerator.createAudioSensor(mapper, 44100,
                1000 * 60 * 15); // rate: 44100Hz, duration: 25 seconds
        tasks.add(audioSensor);

        Task audioSink = TaskGenerator.createTaskWithPeriod(mapper,
                "AudioSink", TaskType.AudioSink, 1000);
        tasks.add(audioSink);

        Condition ifTimerSaysSo = TaskGenerator.createCondition("value",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifTimerSaysSo);
        Task audioTrigger = TaskGenerator.createTrigger(mapper, "AudioTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(audioTrigger);

        Task timerSensor = TaskGenerator.createTimerSensor(mapper, 1000,
                1000 * 60 * 30); // each 2min
        tasks.add(timerSensor);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(timerSensor.getName(), audioTrigger
                                .getName()),
                        new TaskRelation(audioTrigger.getName(), audioSensor
                                .getName()),
                        new TaskRelation(audioSensor.getName(), audioSink
                                .getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);
    }

    /**
     * Survey + Shake
     * 
     * @param resources
     */
    public static void buildProjectJsonB(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey
        Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(1);
        session.setDurationMeasure("minutes");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 5,
                1000, 500);
        tasks.add(accSensor);

        Task shakeFilter = new Task();
        shakeFilter.setName("ShakeFilter");
        shakeFilter.setTaskType(TaskType.ShakeFilter);
        shakeFilter.setPeriodTime(1000);
        tasks.add(shakeFilter);

        Condition ifShake = TaskGenerator.createCondition("isShake",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifShake);
        Task surveyTrigger = TaskGenerator.createTrigger(mapper,
                "SurveyTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(surveyTrigger);

        Task nfcSensor = TaskGenerator.createNfcSensor(mapper, 1000);
        tasks.add(nfcSensor);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(accSensor.getName(), shakeFilter
                                .getName()),
                        new TaskRelation(shakeFilter.getName(), surveyTrigger
                                .getName()),
                        new TaskRelation(surveyTrigger.getName(), "mainSurvey") });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(1);
        project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * GPS + Wifi + Acc, no audio
     * 
     * @param resources
     */
    public static void buildProjectJsonE(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,
                1000, new String[] { "AppleBS4" });
        tasks.add(wifiSensor);

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 20,
                10000, 5000);
        tasks.add(accSensor);

        Condition ifNotConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[1]); // "is false"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifNotConnected);
        Task gpsTrigger = TaskGenerator.createTrigger(mapper, "GpsTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsTrigger);

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        // Task gpsSensor = TaskGenerator.createGpsSensor(mapper, period);
        // tasks.add(gpsSensor);

        Condition ifConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is false"
        conditions = new ArrayList<Condition>();
        conditions.add(ifConnected);
        Task gpsStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "GpsStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsStopTrigger);

        Task audioSensor = TaskGenerator.createAudioSensor(mapper, 44100,
                1000 * 25); // rate: 44100Hz, duration: 25 seconds
        tasks.add(audioSensor);

        Task audioSink = TaskGenerator.createTaskWithPeriod(mapper,
                "AudioSink", TaskType.AudioSink, 1000);
        tasks.add(audioSink);

        Condition ifTimerSaysSo = TaskGenerator.createCondition("value",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifTimerSaysSo);
        Task audioTrigger = TaskGenerator.createTrigger(mapper, "AudioTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(audioTrigger);

        Task timerSensor = TaskGenerator.createTimerSensor(mapper, 1000,
                1000 * 60 * 60 * 3); // each 3 hour
        tasks.add(timerSensor);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(timerSensor.getName(), audioTrigger
                                .getName()),
                        new TaskRelation(audioTrigger.getName(), audioSensor
                                .getName()),
                        new TaskRelation(audioSensor.getName(), audioSink
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), gpsTrigger
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), gpsStopTrigger
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()) });
        // new TaskRelation(gpsSensor.getName(), dataSink.getName()),
        // new TaskRelation(gpsStopTrigger.getName(), gpsSensor.getName()),
        // new TaskRelation(gpsTrigger.getName(), gpsSensor.getName())});

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(1);
        project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    private static void writeProject(Context context, ObjectMapper mapper,
            Project project) {
        String projectFilename = context.getResources().getString(
                R.string.project_filename);
        String parentDirectory = context.getResources().getString(
                R.string.application_root_directory);
        File parent = new File(Environment.getExternalStorageDirectory(),
                parentDirectory);
        parent.mkdirs();
        try {
            //File file = new File(parent, projectFilename);
            //OutputStream output = new FileOutputStream(file);
            OutputStream output = context.openFileOutput(projectFilename, 0);
            //mapper.writeValue(file, project);
            mapper.writeValue(output, project);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }
    
    private static void writeProject_sdcard(Context context, ObjectMapper mapper,
            Project project) {
        String projectFilename = context.getResources().getString(
                R.string.project_filename);
//        String parentDirectory = context.getResources().getString(
//                R.string.application_root_directory);
        String parentDirectory = context.getResources().getString(
				R.string.application_root_directory)
				+ "/.project/";
        File parent = new File(Environment.getExternalStorageDirectory(),
                parentDirectory);
        parent.mkdirs();
        try {
            File file = new File(parent, projectFilename);
            OutputStream output = new FileOutputStream(file);
            //mapper.writeValue(file, project);
            mapper.writeValue(output, project);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * Audio
     * 
     * @param resources
     */
    public static void buildProjectJsonF(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(1); // 1 minute
        session.setDurationMeasure("minutes");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,
                1000, new String[] { "AppleBS4" });
        tasks.add(wifiSensor);

        Task wifiFilter = new Task();
        wifiFilter.setName("WifiTimeConnectedFilter");
        wifiFilter.setTaskType(TaskType.WifiTimeConnectedFilter);
        wifiFilter.setPeriodTime(1000);
        tasks.add(wifiFilter);

        Condition ifTimeDisconnectedGreater = TaskGenerator.createCondition(
                WifiTimeConnectedFilter.ATT_TIMEDISCONNECTED,
                GeneralTrigger.DataType.NUMERIC.name(),
                GeneralTrigger.numericOperators[2],// "is greater than"
                String.valueOf(5000));
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifTimeDisconnectedGreater);
        Task surveyTrigger = TaskGenerator.createTrigger(mapper,
                "SurveyTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(surveyTrigger);

        Condition ifTimeConnectedGreater = TaskGenerator.createCondition(
                WifiTimeConnectedFilter.ATT_TIMECONNECTED,
                GeneralTrigger.DataType.NUMERIC.name(),
                GeneralTrigger.numericOperators[2],// "is greater than"
                String.valueOf(5000));
        conditions = new ArrayList<Condition>();
        conditions.add(ifTimeConnectedGreater);
        Task surveyTrigger2 = TaskGenerator.createTrigger(mapper,
                "SurveyTrigger2", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(surveyTrigger2);

        // Task nfcSensor = TaskGenerator.createNfcSensor(mapper, 44100);
        // tasks.add(nfcSensor);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(wifiSensor.getName(), wifiFilter
                                .getName()),
                        new TaskRelation(wifiFilter.getName(), surveyTrigger
                                .getName()),
                        new TaskRelation(wifiFilter.getName(), surveyTrigger2
                                .getName()),
                        new TaskRelation(surveyTrigger2.getName(), "mainSurvey"),
                        new TaskRelation(surveyTrigger.getName(), "mainSurvey") });

        session.setTasks(tasks);
        session.setRelations(relations);

        Survey survey = SurveyGenerator.createWanderingMindSurvey();

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(1);
        project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * Accelerometer
     * 
     * @param context
     */
    public static void buildProjectJsonG(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 44,
                10000, 10000);
        tasks.add(accSensor);

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] { new TaskRelation(accSensor
                        .getName(), dataSink.getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);
    }

    /**
     * GPS
     * 
     * @param context
     */
    public static void buildProjectJsonH(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setName("mainSession");
        session.setDurationUnits(24L * 4L); // 4 days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())
        // session.setAutoTriggered(true);
        // Time time = new Time();
        // time.setToNow();
        // time.set(time.monthDay - 1, time.month, time.year);
        // session.setStartDate(time.normalize(false));
        //
        // time.setToNow();
        // time.set(time.monthDay + 7, time.month, time.year);
        // session.setEndDate(time.normalize(false));

        // session.setNotices(true);
        // session.setRepeat(false);
        // session.setSessionType("Automatic");

        List<Task> tasks = new ArrayList<Task>();

        Task gpsSensor = TaskGenerator.createGpsSensor(mapper, 10000);
        tasks.add(gpsSensor);

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] { new TaskRelation(gpsSensor
                        .getName(), dataSink.getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);
    }

    /**
     * WiFi
     * 
     * @param context
     */
    public static void buildProjectJsonI(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,
                1000, new String[] { "INFINITUM8DE174" });
        tasks.add(wifiSensor);

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] { new TaskRelation(wifiSensor
                        .getName(), dataSink.getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);
    }

    /**
     * GPS + Wifi + Acc, no audio
     * 
     * @param resources
     */
    public static void buildProjectJsonJ(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,
                1000, new String[] { "INFINITUM8DE174" });
        tasks.add(wifiSensor);
        

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 44,
                10000, 5000);
        tasks.add(accSensor);

        
        Condition ifNotConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[1]); // "is false"
        
        
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifNotConnected);
        
        
        Task gpsTrigger = TaskGenerator.createTrigger(mapper, "GpsTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsTrigger);
        

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        Task gpsSensor = TaskGenerator.createGpsSensor(mapper, 1000L * 30L);
        tasks.add(gpsSensor);

        Condition ifConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        
        
        conditions = new ArrayList<Condition>();
        conditions.add(ifConnected);
        
        
        Task gpsStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "GpsStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsStopTrigger);

      

        

        Task timerSensor = TaskGenerator.createTimerSensor(mapper, 1000,
                1000 * 60 * 60 * 3); // each 3 hour
        tasks.add(timerSensor);
        
        Task surveyTrigger = TaskGenerator.createTrigger(mapper,
                "SurveyTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(surveyTrigger);
        
        

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(wifiSensor.getName(), gpsTrigger.getName()),
                        new TaskRelation(wifiSensor.getName(), gpsStopTrigger.getName()),                       
                        new TaskRelation(wifiSensor.getName(), dataSink.getName()),
                        new TaskRelation(accSensor.getName(), dataSink.getName()),
                        new TaskRelation(gpsSensor.getName(), dataSink.getName()),
                        new TaskRelation(gpsStopTrigger.getName(), gpsSensor.getName()),                       
                        new TaskRelation(gpsTrigger.getName(), gpsSensor.getName()),
                        new TaskRelation(gpsStopTrigger.getName(),surveyTrigger.getName()), 
                        new TaskRelation(surveyTrigger.getName(), "mainSurvey"),});

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(1);
        project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * GPS + Wifi + Acc + audio
     * 
     * @param resources
     */
    public static void buildProjectJsonK(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,
                1000, new String[] { "AppleBS4" });
        tasks.add(wifiSensor);

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 20,
                10000, 5000);
        tasks.add(accSensor);

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        Task gpsSensor = TaskGenerator.createGpsSensor(mapper, 1000L * 30L);
        tasks.add(gpsSensor);

        Task audioSensor = TaskGenerator.createAudioSensor(mapper, 44100,
                1000 * 25); // rate: 44100Hz, duration: 25 seconds
        tasks.add(audioSensor);

        Task audioSink = TaskGenerator.createTaskWithPeriod(mapper,
                "AudioSink", TaskType.AudioSink, 1000);
        tasks.add(audioSink);

        Condition ifTimerSaysSo = TaskGenerator.createCondition("value",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifTimerSaysSo);
        Task audioTrigger = TaskGenerator.createTrigger(mapper, "AudioTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(audioTrigger);

        Task timerSensor = TaskGenerator.createTimerSensor(mapper, 1000,
                1000 * 60 * 60 * 1); // each 3 hour
        tasks.add(timerSensor);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(timerSensor.getName(), audioTrigger
                                .getName()),
                        new TaskRelation(audioTrigger.getName(), audioSensor
                                .getName()),
                        new TaskRelation(audioSensor.getName(), audioSink
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(gpsSensor.getName(), dataSink
                                .getName()), });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(1);
        project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * POWER
     * 
     * @param context
     */
    public static void buildProjectJsonL(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setName("mainSession");
        session.setDurationUnits(24L * 4L); // 4 days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())
        session.setAutoTriggered(true);
        Time time = new Time();
        time.setToNow();
        time.set(time.monthDay - 1, time.month, time.year);
        session.setStartDate(time.normalize(false));

        time.setToNow();
        time.set(time.monthDay + 7, time.month, time.year);
        session.setEndDate(time.normalize(false));

        session.setNotices(true);
        session.setRepeat(false);
        session.setSessionType("Automatic");

        List<Task> tasks = new ArrayList<Task>();

        Task powerSensor = TaskGenerator.createPowerConnectionSensor(mapper,
                1000);
        tasks.add(powerSensor);

        Condition ifPower = TaskGenerator.createCondition("value",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[1]); // "is true"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifPower);
        Task surveyTrigger = TaskGenerator.createTrigger(mapper,
                "SurveyTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(surveyTrigger);

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        // Survey
        Survey survey = SurveyGenerator.createWanderingMindSurvey();

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(powerSensor.getName(), surveyTrigger
                                .getName()),
                        new TaskRelation(surveyTrigger.getName(), "mainSurvey") });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(1);
        project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * Acc + Movement
     * 
     * @param context
     */
    public static void buildProjectJsonM(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setName("mainSession");
        session.setDurationUnits(24L * 4L); // 4 days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())
        session.setAutoTriggered(true);
        Time time = new Time();
        time.setToNow();
        time.set(time.monthDay - 1, time.month, time.year);
        session.setStartDate(time.normalize(false));

        time.setToNow();
        time.set(time.monthDay + 7, time.month, time.year);
        session.setEndDate(time.normalize(false));

        session.setNotices(true);
        session.setRepeat(false);
        session.setSessionType("Automatic");

        List<Task> tasks = new ArrayList<Task>();

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 20,
                10000, 10000);
        tasks.add(accSensor);

        Task movementFilter = TaskGenerator.createMovementFilter(mapper, 1000,
                2.0f);
        tasks.add(movementFilter);

        Condition ifMovement = TaskGenerator.createCondition("isMovement",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifMovement);
        Task surveyTrigger = TaskGenerator.createTrigger(mapper,
                "SurveyTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(surveyTrigger);

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        // Survey
        Survey survey = SurveyGenerator.createWanderingMindSurvey();

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(accSensor.getName(), movementFilter.getName()),
                        new TaskRelation(accSensor.getName(), dataSink.getName()),
                        new TaskRelation(movementFilter.getName(),surveyTrigger.getName()),
                        new TaskRelation(surveyTrigger.getName(), "mainSurvey") });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(1);
        project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * GPS + Wifi + Acc + audio + NFC
     * 
     * @param resources
     */
    public static void buildProjectJsonN(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        // Sensors

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 20,
                10000, 10000);
        tasks.add(accSensor);

        Task audioSensor = TaskGenerator.createAudioSensor(mapper, 44100,
                1000 * 60 * 2); // rate: 44100Hz, duration: 2 minutes
        audioSensor.setTriggered(true);
        tasks.add(audioSensor);

        Task gpsSensor = TaskGenerator.createGpsSensor(mapper, 1000L * 30L);
        gpsSensor.setTriggered(true);
        tasks.add(gpsSensor);

        Task nfcSensor = TaskGenerator.createNfcSensor(mapper, 500);
        tasks.add(nfcSensor);

        Task accTimerSensor = TaskGenerator.createTimerSensor(mapper, 1000,
                1000 * 20); // each 20 seconds
        accTimerSensor.setTriggered(true);
        tasks.add(accTimerSensor);

        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,
                1000, new String[] { "AppleBS4" });
        tasks.add(wifiSensor);

        // Filters

        Task movementFilter = TaskGenerator.createMovementFilter(mapper, 1000,
                0.3f);
        tasks.add(movementFilter);

        Task falseTimerFilter = TaskGenerator.createFalseTimerFilter(mapper,
                1000, 5000L, "isMovement");
        tasks.add(falseTimerFilter);

        // Triggers

        Condition ifNotConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[1]); // "is false"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifNotConnected);
        Task gpsTrigger = TaskGenerator.createTrigger(mapper, "GpsTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsTrigger);

        Condition ifConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifConnected);
        Task gpsStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "GpsStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsStopTrigger);

        Condition ifNotNull = TaskGenerator.createCondition("message",
                GeneralTrigger.DataType.TEXT.name(),
                GeneralTrigger.textOperators[3], null, "null"); // "is not"
        conditions = new ArrayList<Condition>();
        conditions.add(ifNotNull);
        Task audioTrigger = TaskGenerator.createTrigger(mapper, "AudioTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(audioTrigger);

        Condition ifNotMovement = TaskGenerator.createCondition("falseEvent",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifNotMovement);
        Task accStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "AccStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(accStopTrigger);
        Task timerTrigger = TaskGenerator.createTrigger(mapper, "TimerTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(timerTrigger);

        Condition ifTimerSaysSo = TaskGenerator.createCondition("value",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifTimerSaysSo);
        Task accTrigger = TaskGenerator.createTrigger(mapper, "AccTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(accTrigger);

        // Sinks

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        Task audioSink = TaskGenerator.createTaskWithPeriod(mapper,
                "AudioSink", TaskType.AudioSink, 1000);
        tasks.add(audioSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(wifiSensor.getName(), gpsTrigger
                                .getName()),
                        new TaskRelation(gpsTrigger.getName(), gpsSensor
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), gpsStopTrigger
                                .getName()),
                        new TaskRelation(gpsStopTrigger.getName(), gpsSensor
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(gpsSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(nfcSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(nfcSensor.getName(), audioTrigger
                                .getName()),
                        new TaskRelation(audioTrigger.getName(), audioSensor
                                .getName()),
                        new TaskRelation(audioSensor.getName(), audioSink
                                .getName()),
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(accSensor.getName(), movementFilter
                                .getName()),
                        new TaskRelation(movementFilter.getName(),
                                falseTimerFilter.getName()),
                        new TaskRelation(falseTimerFilter.getName(),
                                accStopTrigger.getName()),
                        new TaskRelation(accStopTrigger.getName(), accSensor
                                .getName()),
                        new TaskRelation(falseTimerFilter.getName(),
                                timerTrigger.getName()),
                        new TaskRelation(timerTrigger.getName(), accTimerSensor
                                .getName()),
                        new TaskRelation(accTimerSensor.getName(), accTrigger
                                .getName()),
                        new TaskRelation(accTrigger.getName(), accSensor
                                .getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);
        // project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * ACC
     * 
     * @param resources
     */
    public static void buildProjectJsonO(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        // Sensors

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 44,
                10000, 10000);
        tasks.add(accSensor);

        Task accTimerSensor = TaskGenerator.createTimerSensor(mapper, 1000,
                1000 * 20); // each 20 seconds
        accTimerSensor.setTriggered(true);
        tasks.add(accTimerSensor);

        // Task powerSensor = TaskGenerator.createPowerConnectionSensor(mapper,
        // 1000);
        // tasks.add(powerSensor);

        // Filters

        Task movementFilter = TaskGenerator.createMovementFilter(mapper, 1000,
                0.3f);
        tasks.add(movementFilter);

        Task falseTimerFilter = TaskGenerator.createFalseTimerFilter(mapper,
                1000, 5000L, "isMovement");
        tasks.add(falseTimerFilter);

        // Triggers

        Condition ifNotMovement = TaskGenerator.createCondition("falseEvent",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifNotMovement);
        Task accStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "AccStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(accStopTrigger);
        Task timerTrigger = TaskGenerator.createTrigger(mapper, "TimerTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(timerTrigger);

        // Condition ifMovement = TaskGenerator.createCondition("falseEvent",
        // GeneralTrigger.DataType.BOOLEAN.name(),
        // GeneralTrigger.booleanOperators[1]); // "is false"
        // conditions = new ArrayList<Condition>();
        // conditions.add(ifMovement);
        // Task timerStopTrigger = TaskGenerator
        // .createStopTrigger(mapper, "TimerStopTrigger", 1000,
        // GeneralTrigger.matches[0], conditions);
        // tasks.add(timerStopTrigger);

        Condition ifTimerSaysSo = TaskGenerator.createCondition("value",
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifTimerSaysSo);
        Task accTrigger = TaskGenerator.createTrigger(mapper, "AccTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(accTrigger);

        // Condition ifPower = TaskGenerator.createCondition("value",
        // GeneralTrigger.DataType.BOOLEAN.name(),
        // GeneralTrigger.booleanOperators[0]); // "is true"
        // conditions = new ArrayList<Condition>();
        // conditions.add(ifPower);
        // Task accStopTrigger2 = TaskGenerator.createStopTrigger(mapper,
        // "AccStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        // tasks.add(accStopTrigger2);
        //
        // Condition ifNoPower = TaskGenerator.createCondition("value",
        // GeneralTrigger.DataType.BOOLEAN.name(),
        // GeneralTrigger.booleanOperators[1]); // "is false"
        // conditions = new ArrayList<Condition>();
        // conditions.add(ifNoPower);
        // Task accTrigger2 = TaskGenerator.createTrigger(mapper, "AccTrigger",
        // 1000, GeneralTrigger.matches[0], conditions);
        // tasks.add(accTrigger2);

        // Sinks

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(accSensor.getName(), movementFilter
                                .getName()),
                        new TaskRelation(movementFilter.getName(),
                                falseTimerFilter.getName()),
                        new TaskRelation(falseTimerFilter.getName(),
                                accStopTrigger.getName()),
                        new TaskRelation(accStopTrigger.getName(), accSensor
                                .getName()),
                        // new TaskRelation(falseTimerFilter.getName(),
                        // timerStopTrigger.getName()),
                        // new TaskRelation(timerStopTrigger.getName(),
                        // accTimerSensor.getName()),
                        new TaskRelation(falseTimerFilter.getName(),
                                timerTrigger.getName()),
                        new TaskRelation(timerTrigger.getName(), accTimerSensor
                                .getName()),
                        new TaskRelation(accTimerSensor.getName(), accTrigger
                                .getName()),
                        new TaskRelation(accTrigger.getName(), accSensor
                                .getName())
                // new TaskRelation(powerSensor.getName(), accTrigger2
                // .getName()),
                // new TaskRelation(accTrigger2.getName(), accSensor
                // .getName()),
                // new TaskRelation(powerSensor.getName(), accStopTrigger2
                // .getName()),
                // new TaskRelation(accStopTrigger2.getName(), accSensor
                // .getName()),

                });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);
        // project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * GPS + Wifi
     * 
     * @param resources
     */
    public static void buildProjectJsonP(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        								// Session
        Session session = new Session();
        session.setDurationUnits(24L * 10L); // 10days
        session.setDurationMeasure("hours"); 
										// TaskList
        List<Task> tasks = new ArrayList<Task>();
        								// Sensors
        Task callSensor = TaskGenerator.createCallSensor(mapper, 1000);
        callSensor.setTriggered(false);
        tasks.add(callSensor);
        //
        Task screenSensor = TaskGenerator.createScreenSensor(mapper, 1000);
        screenSensor.setTriggered(false);
        tasks.add(screenSensor);
        //
        Task smsSensor = TaskGenerator.createSmsSensor(mapper, 1000);
        smsSensor.setTriggered(false);
        tasks.add(smsSensor);
        //
        Task batteryLevelSensor = TaskGenerator.createBatteryLevelSensor(mapper, 10);
        batteryLevelSensor.setTriggered(false);
        tasks.add(batteryLevelSensor);
        //
        Task batteryStateSensor = TaskGenerator.createBatteryStateSensor(mapper, 10);
        batteryLevelSensor.setTriggered(false);
        tasks.add(batteryStateSensor);
        //
        Task phoneStateSensor = TaskGenerator.createStatePhoneSensor(mapper, 1000);
        phoneStateSensor.setTriggered(false);
        tasks.add(phoneStateSensor);
        //                
        Task gpsSensor = TaskGenerator.createGpsSensor(mapper, 1000L * 30L);
        gpsSensor.setTriggered(true);
        tasks.add(gpsSensor);
        //
        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper, 1000, new String[]{"WifiSsid"});
        tasks.add(wifiSensor);

        Task wifiNetworkSensor = TaskGenerator.createWifiNetworkSensor(mapper, 10000);
        tasks.add(wifiNetworkSensor);

        									//Conditions & Triggers
        Condition ifNotConnected = TaskGenerator.createCondition(WifiConnectionSensor.ATT_ISCONNECTED, GeneralTrigger.DataType.BOOLEAN.name(), GeneralTrigger.booleanOperators[1]); // "is false"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifNotConnected);
        //
        Task gpsTrigger = TaskGenerator.createTrigger(mapper, "GpsTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsTrigger);
        //
        Condition ifConnected = TaskGenerator.createCondition(WifiConnectionSensor.ATT_ISCONNECTED, GeneralTrigger.DataType.BOOLEAN.name(), GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifConnected);
        //
        Task gpsStopTrigger = TaskGenerator.createStopTrigger(mapper, "GpsStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsStopTrigger);

        // Components or filters
        Task wifiCountTask = TaskGenerator.CreateDataFilter(mapper, "WifiNetworkCount1",
                "WifiNetworksQuantityFoundFilter_1", "1", "1");
        tasks.add(wifiCountTask);

        Task wifiCountTask1 = TaskGenerator.CreateDataFilter(mapper, "WifiNetworkCount2",
                "WifiNetworksQuantityFoundFilter_1", "1", "1");
        tasks.add(wifiCountTask1);

        									// Sink        
        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);
        									//Relations
        List<TaskRelation> relations = Arrays.asList(new TaskRelation[]{
                new TaskRelation(wifiSensor.getName(), gpsTrigger.getName()),
                new TaskRelation(gpsTrigger.getName(), gpsSensor.getName()),
                new TaskRelation(wifiSensor.getName(), gpsStopTrigger.getName()),
                new TaskRelation(gpsStopTrigger.getName(), gpsSensor.getName()),
                new TaskRelation(wifiSensor.getName(), dataSink.getName()),
                new TaskRelation(gpsSensor.getName(), dataSink.getName()),
                new TaskRelation(callSensor.getName(), dataSink.getName()),
                new TaskRelation(smsSensor.getName(), dataSink.getName()),
                new TaskRelation(batteryStateSensor.getName(), dataSink.getName()),
                new TaskRelation(screenSensor.getName(), dataSink.getName()),
                new TaskRelation(wifiNetworkSensor.getName(), wifiCountTask.getName()),
                new TaskRelation(wifiNetworkSensor.getName(), wifiCountTask1.getName()),
                new TaskRelation(wifiCountTask.getName(), dataSink.getName()),
                new TaskRelation(wifiCountTask1.getName(), dataSink.getName())});
        									//Sessions
        session.setTasks(tasks);
        session.setRelations(relations);
        									//Project
        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);
    }

    /**
     * GPS + Wifi + Acc + audio + NFC without movement
     * 
     * @param resources
     */
    public static void buildProjectJsonQ(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 25L); // 21days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        // Sensors

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 30,
                10000, 5000);
        tasks.add(accSensor);

        Task audioSensor = TaskGenerator.createAudioSensor(mapper, 22050,
                1000 * 60 * 2); // rate: 44100Hz, duration: 2 minutes
        audioSensor.setTriggered(true);
        tasks.add(audioSensor);

        Task gpsSensor = TaskGenerator.createGpsSensor(mapper, 1000L * 60L);
        gpsSensor.setTriggered(true);
        tasks.add(gpsSensor);

        Task nfcSensor = TaskGenerator.createNfcSensor(mapper, 1000);
        tasks.add(nfcSensor);

        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,
                1000, new String[] { "AppleBS4" });
        tasks.add(wifiSensor);

        // Triggers

        Condition ifNotConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[1]); // "is false"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifNotConnected);
        Task gpsTrigger = TaskGenerator.createTrigger(mapper, "GpsTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsTrigger);

        Condition ifConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifConnected);
        Task gpsStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "GpsStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsStopTrigger);

        Condition ifNotNull = TaskGenerator.createCondition("message",
                GeneralTrigger.DataType.TEXT.name(),
                GeneralTrigger.textOperators[3], null, "null"); // "is not"
        conditions = new ArrayList<Condition>();
        conditions.add(ifNotNull);
        Task audioTrigger = TaskGenerator.createTrigger(mapper, "AudioTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(audioTrigger);

        // Sinks

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);

        Task audioSink = TaskGenerator.createTaskWithPeriod(mapper,
                "AudioSink", TaskType.AudioSink, 1000);
        tasks.add(audioSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(wifiSensor.getName(), gpsTrigger
                                .getName()),
                        new TaskRelation(gpsTrigger.getName(), gpsSensor
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), gpsStopTrigger
                                .getName()),
                        new TaskRelation(gpsStopTrigger.getName(), gpsSensor
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(gpsSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(nfcSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(nfcSensor.getName(), audioTrigger
                                .getName()),
                        new TaskRelation(audioTrigger.getName(), audioSensor
                                .getName()),
                        new TaskRelation(audioSensor.getName(), audioSink
                                .getName()),
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);
        // project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * Audio + NFC
     * 
     * @param resources
     */
    public static void buildProjectJsonR(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        // Sensors

        Task audioSensor = TaskGenerator.createAudioSensor(mapper, 44100,
                1000 * 60 * 2); // rate: 44100Hz, duration: 2 minutes
        audioSensor.setTriggered(true);
        tasks.add(audioSensor);

        Task nfcSensor = TaskGenerator.createNfcSensor(mapper, 500);
        tasks.add(nfcSensor);

        // Triggers

        Condition ifNotNull = TaskGenerator.createCondition("message",
                GeneralTrigger.DataType.TEXT.name(),
                GeneralTrigger.textOperators[3], null, "null"); // "is not"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifNotNull);
        Task audioTrigger = TaskGenerator.createTrigger(mapper, "AudioTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(audioTrigger);

        // Sinks

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        Task audioSink = TaskGenerator.createTaskWithPeriod(mapper,
                "AudioSink", TaskType.AudioSink, 1000);
        tasks.add(audioSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(nfcSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(nfcSensor.getName(), audioTrigger
                                .getName()),
                        new TaskRelation(audioTrigger.getName(), audioSensor
                                .getName()),
                        new TaskRelation(audioSensor.getName(), audioSink
                                .getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);
        // project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * GPS + Wifi + Acc + audio + NFC with Movement filters
     * 
     * @param resources
     */
    public static void buildProjectJsonS(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 25L); // 21days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        // Sensors

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 30,
                10000, 10000);
        tasks.add(accSensor);

        Task audioSensor = TaskGenerator.createAudioSensor(mapper, 22050,
                1000 * 60 * 2); // rate: 44100Hz, duration: 2 minutes
        audioSensor.setTriggered(true);
        tasks.add(audioSensor);

        Task gpsSensor = TaskGenerator
                .createGpsSensor(mapper, 1000L * 60L * 2L); // each 2 minutes
        gpsSensor.setTriggered(true);
        tasks.add(gpsSensor);

        Task nfcSensor = TaskGenerator.createNfcSensor(mapper, 1000);
        tasks.add(nfcSensor);

        Task wifiSensor = TaskGenerator.createWifiConnectionSensor(mapper,
                1000, new String[] { "AppleBS4" });
        tasks.add(wifiSensor);

        // Filters

        Task movementFilter = TaskGenerator.createMovementFilter(mapper, 1000,
                0.3f);
        tasks.add(movementFilter);

        Task movementTimeFilter = TaskGenerator.createMovementTimeFilter(
                mapper, 1000, 30000L, 5000L);
        tasks.add(movementTimeFilter);

        // Triggers

        Condition ifNotConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[1]); // "is false"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifNotConnected);
        Task gpsTrigger = TaskGenerator.createTrigger(mapper, "GpsTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsTrigger);

        Condition ifConnected = TaskGenerator.createCondition(
                WifiConnectionSensor.ATT_ISCONNECTED,
                GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifConnected);
        Task gpsStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "GpsStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(gpsStopTrigger);

        Condition ifNotNull = TaskGenerator.createCondition("message",
                GeneralTrigger.DataType.TEXT.name(),
                GeneralTrigger.textOperators[3], null, "null"); // "is not"
        conditions = new ArrayList<Condition>();
        conditions.add(ifNotNull);
        Task audioTrigger = TaskGenerator.createTrigger(mapper, "AudioTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(audioTrigger);

        Condition ifMoveTimeEvent = TaskGenerator.createCondition(
                "moveTimeEvent", GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        conditions = new ArrayList<Condition>();
        conditions.add(ifMoveTimeEvent);
        Task accTrigger = TaskGenerator.createTrigger(mapper, "AccTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(accTrigger);

        Condition ifNotMoveTimeEvent = TaskGenerator.createCondition(
                "moveTimeEvent", GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[1]); // "is false"
        conditions = new ArrayList<Condition>();
        conditions.add(ifNotMoveTimeEvent);
        Task accStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "AccStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(accStopTrigger);

        // Sinks

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        Task audioSink = TaskGenerator.createTaskWithPeriod(mapper,
                "AudioSink", TaskType.AudioSink, 1000);
        tasks.add(audioSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(wifiSensor.getName(), gpsTrigger
                                .getName()),
                        new TaskRelation(gpsTrigger.getName(), gpsSensor
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), gpsStopTrigger
                                .getName()),
                        new TaskRelation(gpsStopTrigger.getName(), gpsSensor
                                .getName()),
                        new TaskRelation(wifiSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(gpsSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(nfcSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(nfcSensor.getName(), audioTrigger
                                .getName()),
                        new TaskRelation(audioTrigger.getName(), audioSensor
                                .getName()),
                        new TaskRelation(audioSensor.getName(), audioSink
                                .getName()),
                        new TaskRelation(accSensor.getName(), movementFilter
                                .getName()),
                        new TaskRelation(movementFilter.getName(),
                                movementTimeFilter.getName()),
                        new TaskRelation(movementTimeFilter.getName(),
                                accTrigger.getName()),
                        new TaskRelation(movementTimeFilter.getName(),
                                accStopTrigger.getName()),
                        new TaskRelation(accTrigger.getName(), accSensor
                                .getName()),
                        new TaskRelation(accStopTrigger.getName(), accSensor
                                .getName()),
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);
        // project.put("mainSurvey", survey);

        //writeProject(context, mapper, project);
        writeProject_sdcard(context, mapper, project);
    }
    
    public static void buildProjectJsonS2(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 25L); // 21days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        // Sensors

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 30,
                10000, 10000);
        tasks.add(accSensor);


        // Sinks

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);
        
        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);
        // project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }
    
    public static void buildProjectJsonS3(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey survey = SurveyGenerator.createWanderingMindSurvey();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 25L); // 21days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        // Sensors

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 30,
                10000, 10000);
        tasks.add(accSensor);

        
        // Filters

        Task movementFilter = TaskGenerator.createMovementFilter(mapper, 1000,
                0.3f);
        tasks.add(movementFilter);

        Task movementTimeFilter = TaskGenerator.createMovementTimeFilter(
                mapper, 1000, 30000L, 5000L);
        tasks.add(movementTimeFilter);

        // Triggers

        Condition ifMoveTimeEvent = TaskGenerator.createCondition(
                "moveTimeEvent", GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[0]); // "is true"
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(ifMoveTimeEvent);
        Task accTrigger = TaskGenerator.createTrigger(mapper, "AccTrigger",
                1000, GeneralTrigger.matches[0], conditions);
        tasks.add(accTrigger);

        Condition ifNotMoveTimeEvent = TaskGenerator.createCondition(
                "moveTimeEvent", GeneralTrigger.DataType.BOOLEAN.name(),
                GeneralTrigger.booleanOperators[1]); // "is false"
        conditions = new ArrayList<Condition>();
        conditions.add(ifNotMoveTimeEvent);
        Task accStopTrigger = TaskGenerator.createStopTrigger(mapper,
                "AccStopTrigger", 1000, GeneralTrigger.matches[0], conditions);
        tasks.add(accStopTrigger);

        // Sinks

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);
        
        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(accSensor.getName(), movementFilter
                                .getName()),
                        new TaskRelation(movementFilter.getName(),
                                movementTimeFilter.getName()),
                        new TaskRelation(movementTimeFilter.getName(),
                                accTrigger.getName()),
                        new TaskRelation(movementTimeFilter.getName(),
                                accStopTrigger.getName()),
                        new TaskRelation(accTrigger.getName(), accSensor
                                .getName()),
                        new TaskRelation(accStopTrigger.getName(), accSensor
                                .getName()),
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()) });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);
        // project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    /**
     * Survey + Shake
     * 
     * @param resources
     */
    public static void buildProjectJsonMaythe(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Survey
//        Survey survey = SurveyGenerator.createWanderingMindSurvey();
        Survey survey = SurveyGenerator.createMindSurveyWithAudio();

        // Session
        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 25L); // 21days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        Task nfcSensor = TaskGenerator.createNfcSensor(mapper, 1000);
        tasks.add(nfcSensor);
        Task surveySensor = TaskGenerator.createSurveySensor(mapper, 500);
        tasks.add(surveySensor);

        Task audioSensor = TaskGenerator.createAudioSensor(mapper, 22050,
                1000 * 60 * 1); // rate: 44100Hz, duration: 2 minutes
        audioSensor.setTriggered(true);
        tasks.add(audioSensor);

        ArrayList<Condition> conditions = new ArrayList<Condition>();

        // Condition ifNotNull = TaskGenerator.createCondition("message",
        // GeneralTrigger.DataType.TEXT.name(),
        // GeneralTrigger.textOperators[3], null, "null"); // "is not"
        // conditions.add(ifNotNull);

        Condition ifPreocupado = TaskGenerator.createCondition("message",
                GeneralTrigger.DataType.TEXT.name(),
                GeneralTrigger.textOperators[0], null, "PREOCUPADO"); // "contains"
        conditions.add(ifPreocupado);

        Condition ifDeprimido = TaskGenerator.createCondition("message",
                GeneralTrigger.DataType.TEXT.name(),
                GeneralTrigger.textOperators[2], null, "DEPRIMIDO"); // "is"
        conditions.add(ifDeprimido);

        Condition ifTriste = TaskGenerator.createCondition("message",
                GeneralTrigger.DataType.TEXT.name(),
                GeneralTrigger.textOperators[2], null, "AGREDIO"); // "is"
        conditions.add(ifTriste);

        Task surveyTrigger = TaskGenerator.createTrigger(mapper,
                "SurveyTrigger", 1000, GeneralTrigger.matches[1], conditions);
        tasks.add(surveyTrigger);

        conditions = new ArrayList<Condition>();
        Condition ifGrabarAudio = TaskGenerator.createCondition("lastAnswer",
                GeneralTrigger.DataType.TEXT.name(),
                GeneralTrigger.textOperators[2], null, "0"); // "is"
        conditions.add(ifGrabarAudio);
        
        Task audioTrigger = TaskGenerator.createTrigger(mapper,
                "AudioTrigger", 1000, GeneralTrigger.matches[1], conditions);
        tasks.add(audioTrigger);

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        Task audioSink = TaskGenerator.createTaskWithPeriod(mapper,
                "AudioSink", TaskType.AudioSink, 1000);
        tasks.add(audioSink);

        List<TaskRelation> relations = Arrays.asList(new TaskRelation[]{
                new TaskRelation(nfcSensor.getName(), surveyTrigger.getName()),
                new TaskRelation(nfcSensor.getName(), dataSink.getName()),
                new TaskRelation(surveyTrigger.getName(), "mainSurvey"),
                new TaskRelation(surveySensor.getName(), audioTrigger.getName()),
                new TaskRelation(audioTrigger.getName(), audioSensor.getName()),
                new TaskRelation(audioSensor.getName(), audioSink.getName()),
        });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(1);
        project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }
    
    public static void buildProjectJsonU(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 4L); // 4days
        session.setDurationMeasure("hours");
        // session.setStartDate(new Calendar())

        List<Task> tasks = new ArrayList<Task>();

        // Sensors

        Task accSensor = TaskGenerator.createAccelerometerSensor(mapper, 30,
                10000, 10000);
        tasks.add(accSensor);
        
       
        // Filters

        Task stepCounterFilter = TaskGenerator.createStepsCounterFilter(mapper, 10000);
        tasks.add(stepCounterFilter);

     
        // Sinks

        Task dataSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(dataSink);

        List<TaskRelation> relations = Arrays
                .asList(new TaskRelation[] {
                        new TaskRelation(accSensor.getName(), dataSink
                                .getName()),
                        new TaskRelation(accSensor.getName(), stepCounterFilter
                                .getName()),
                        new TaskRelation(stepCounterFilter.getName(),
                                dataSink.getName()),

                });

        session.setTasks(tasks);
        session.setRelations(relations);

        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);
        // project.put("mainSurvey", survey);

        writeProject(context, mapper, project);
    }

    public static void buildProjectJsonEval(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 10L); // 10days
        session.setDurationMeasure("hours");
        // TaskList
        List<Task> tasks = new ArrayList<Task>();
        // Sensors

        Task accelerometerTask = TaskGenerator.createAccelerometerSensor(mapper, 5, 1000, 500);
        tasks.add(accelerometerTask);

        Task gpsTask = TaskGenerator.createGpsSensor(mapper, 1000);
        tasks.add(gpsTask);

        Task activityCountTask = TaskGenerator.CreateDataFilter(mapper, "ActivityCount",
                "ActivityCountFilter_9", "9", "1");
        tasks.add(activityCountTask);

        Task userLocationTask = TaskGenerator.CreateDataFilter(mapper, "UserLocation",
                "UserLocationFilter_10", "10", "1");
        tasks.add(userLocationTask);

        Task activityCountSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(activityCountSink);

        Task locationSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(locationSink);

        List<TaskRelation> relations = Arrays.asList(new TaskRelation[]{
                new TaskRelation(accelerometerTask.getName(), activityCountTask.getName()),
                new TaskRelation(activityCountTask.getName(), activityCountSink.getName()),
                new TaskRelation(gpsTask.getName(), userLocationTask.getName()),
                new TaskRelation(userLocationTask.getName(), locationSink.getName())});

        session.setTasks(tasks);
        session.setRelations(relations);
        //Project
        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);
    }

    public static void buildProjectJsonEvalAccel(Context context){
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 10L); // 10days
        session.setDurationMeasure("hours");
        // TaskList
        List<Task> tasks = new ArrayList<Task>();

        // Sensors
        Task accelerometerTask = TaskGenerator.createBareAccelerometerSensor(mapper, SensorManager.SENSOR_DELAY_NORMAL);
        tasks.add(accelerometerTask);

        Task gyroscopeTask = TaskGenerator.createGyroscopeSensor(mapper, SensorManager.SENSOR_DELAY_NORMAL);
        tasks.add(gyroscopeTask);

        Task orientationTask = TaskGenerator.createOrientationSensor(mapper, SensorManager.SENSOR_DELAY_NORMAL);
        tasks.add(orientationTask);

        Task imuTask = TaskGenerator.createImuSensor(mapper, SensorManager.SENSOR_DELAY_NORMAL);
        tasks.add(imuTask);

        Task globalSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(globalSink);

//        Task orientSink = TaskGenerator.createDataSink(mapper, 60);
//        tasks.add(orientSink);

        List<TaskRelation> relations = Arrays.asList(new TaskRelation[]{
                new TaskRelation(accelerometerTask.getName(), globalSink.getName()),
                new TaskRelation(gyroscopeTask.getName(), globalSink.getName()),
                new TaskRelation(orientationTask.getName(), globalSink.getName()),
                new TaskRelation(imuTask.getName(), globalSink.getName())});

        session.setTasks(tasks);
        session.setRelations(relations);
        //Project
        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);

    }

    public static void buildProjectJsonNutrition(Context context){
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 100L); // 10days
        session.setDurationMeasure("hours");
        // TaskList
        List<Task> tasks = new ArrayList<Task>();

        // Sensors
        Task accelerometerTask = TaskGenerator.createBareAccelerometerSensor(mapper, SensorManager.SENSOR_DELAY_NORMAL);
        tasks.add(accelerometerTask);

        Task gyroscopeTask = TaskGenerator.createGyroscopeSensor(mapper, SensorManager.SENSOR_DELAY_NORMAL);
        tasks.add(gyroscopeTask);

        Task orientationTask = TaskGenerator.createOrientationSensor(mapper, SensorManager.SENSOR_DELAY_NORMAL);
        tasks.add(orientationTask);

        Task imuTask = TaskGenerator.createImuSensor(mapper, SensorManager.SENSOR_DELAY_NORMAL);
        tasks.add(imuTask);

        Task screenTask = TaskGenerator.createScreenSensor(mapper, 1);
        tasks.add(screenTask);

        Task batteryStateTask = TaskGenerator.createBatteryStateSensor(mapper, 1);
        tasks.add(batteryStateTask);

        Task batteryLevelTask = TaskGenerator.createBatteryLevelSensor(mapper, 1);
        tasks.add(batteryLevelTask);

        Task globalSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(globalSink);

//        Task orientSink = TaskGenerator.createDataSink(mapper, 60);
//        tasks.add(orientSink);

        List<TaskRelation> relations = Arrays.asList(new TaskRelation[]{
                new TaskRelation(accelerometerTask.getName(), globalSink.getName()),
                new TaskRelation(gyroscopeTask.getName(), globalSink.getName()),
                new TaskRelation(orientationTask.getName(), globalSink.getName()),
                new TaskRelation(imuTask.getName(), globalSink.getName()),
                new TaskRelation(screenTask.getName(), globalSink.getName()),
                new TaskRelation(batteryStateTask.getName(), globalSink.getName()),
                new TaskRelation(batteryLevelTask.getName(), globalSink.getName())});

        session.setTasks(tasks);
        session.setRelations(relations);
        //Project
        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);

    }

    public static void buildProjectJsonEvalStudents(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        // Session
        Session session = new Session();
        session.setDurationUnits(24L * 10L); // 10days
        session.setDurationMeasure("hours");
        // TaskList
        List<Task> tasks = new ArrayList<Task>();
        // Sensors

        Task gpsTask = TaskGenerator.createGpsSensor(mapper, 1000);
        tasks.add(gpsTask);

        Task userLocationTask = TaskGenerator.CreateDataFilter(mapper, "UserLocation",
                "JAMA_15", "15", "1");
        tasks.add(userLocationTask);

        Task locationSink = TaskGenerator.createDataSink(mapper, 60);
        tasks.add(locationSink);

        List<TaskRelation> relations = Arrays.asList(new TaskRelation[]{
                new TaskRelation(gpsTask.getName(), userLocationTask.getName()),
                new TaskRelation(userLocationTask.getName(), locationSink.getName())});

        session.setTasks(tasks);
        session.setRelations(relations);
        //Project
        Project project = new Project();
        project.setSessionsSize(1);
        project.put("mainSession", session);
        project.setSurveysSize(0);

        writeProject(context, mapper, project);
    }

}
