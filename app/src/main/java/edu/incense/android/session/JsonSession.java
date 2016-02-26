package edu.incense.android.session;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import edu.incense.android.datatask.model.Task;
import edu.incense.android.datatask.model.TaskRelation;

import android.util.Log;

public class JsonSession {
    public final static String NAME = "name";
    public final static String TASKS = "tasks";
    public final static String RELATIONS = "relations";
    public final static String DURATION_MEASURE = "durationMeasure";
    public final static String DURATION_UNITS = "durationUnits";
    public final static String AUTO_TRIGGERED = "autoTriggered";
    public final static String START_DATE = "startDate";
    public final static String END_DATE = "endDate";
    public final static String REPEATE_MEASURE = "repeatMeasure";
    public final static String REPEATE_UNITS = "repeatUnits";
    public final static String REPEAT = "repeat";
    public final static String NOTICES = "notices";
    public final static String SESSION_TYPE = "sessionType";

    private ObjectMapper mapper;

    public JsonSession() {
        mapper = new ObjectMapper(); // can reuse, share globally
    }

    public JsonSession(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Session toSession(JsonNode root) {
        Session session = null;
        try {
            session = new Session();
            JsonNode attribute = root.get(NAME);
            if (attribute != null && !attribute.isNull()) {
                String name = attribute.getValueAsText();
                session.setName(name);
            }

            attribute = root.get(DURATION_MEASURE);
            if (attribute != null && !attribute.isNull()) {
                String durationMeasure = attribute.getValueAsText();
                session.setDurationMeasure(durationMeasure);
            }

            attribute = root.get(DURATION_UNITS);
            if (attribute != null && !attribute.isNull()) {
                long durationUnits = attribute.getValueAsLong();
                session.setDurationUnits(durationUnits);
            }

            attribute = root.get(AUTO_TRIGGERED);
            if (attribute != null && !attribute.isNull()) {
                boolean autoTriggered = attribute.getValueAsBoolean();
                session.setAutoTriggered(autoTriggered);
            }

            attribute = root.get(START_DATE);
            if (attribute != null && !attribute.isNull()) {
                long startDate = attribute.getValueAsLong();
                session.setStartDate(startDate);
            }
            
            attribute = root.get(END_DATE);
            if (attribute != null && !attribute.isNull()) {
                long endDate = attribute.getValueAsLong();
                session.setEndDate(endDate);
            }
            
            attribute = root.get(REPEATE_MEASURE);
            if (attribute != null && !attribute.isNull()) {
                String repeatMeasure = attribute.getValueAsText();
                session.setRepeatMeasure(repeatMeasure);
            }
            
            attribute = root.get(REPEATE_UNITS);
            if (attribute != null && !attribute.isNull()) {
                int repeatUnits = attribute.getValueAsInt();
                session.setRepeatUnits(repeatUnits);
            }
            
            attribute = root.get(REPEAT);
            if (attribute != null && !attribute.isNull()) {
                boolean repeat = attribute.getValueAsBoolean();
                session.setRepeat(repeat);
            }
            
            attribute = root.get(NOTICES);
            if (attribute != null && !attribute.isNull()) {
                boolean notices = attribute.getValueAsBoolean();
                session.setNotices(notices);
            }
            
            attribute = root.get(SESSION_TYPE);
            if (attribute != null && !attribute.isNull()) {
                String sessionType = attribute.getValueAsText();
                session.setSessionType(sessionType);
            }
            
            attribute = root.get(TASKS);
            List<Task> tasks = mapper.readValue(attribute,
                    new TypeReference<List<Task>>() {
                    });
            if (tasks != null)
                session.setTasks(tasks);
            else {
                Log.e(getClass().getName(),
                        "Tasks JSON node was empty/null or doesn't exist");
            }
            attribute = root.get(RELATIONS);
            List<TaskRelation> relations = mapper.readValue(attribute,
                    new TypeReference<List<TaskRelation>>() {
                    });
            if (relations != null)
                session.setRelations(relations);
            else {
                Log.e(getClass().getName(),
                        "TaskRelation JSON node was empty/null or doesn't exist");
            }

        } catch (JsonParseException e) {
            Log.e(getClass().getName(), "Parsing JSON file failed", e);
            return null;
        } catch (JsonMappingException e) {
            Log.e(getClass().getName(), "Mapping JSON file failed", e);
            return null;
        } catch (IOException e) {
            Log.e(getClass().getName(), "Reading JSON file failed", e);
            return null;
        }
        return session;
    }

}
