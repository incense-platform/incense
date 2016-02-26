package edu.incense.android.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.util.Log;
import edu.incense.android.datatask.model.TaskType;
import edu.incense.android.session.JsonSession;
import edu.incense.android.session.Session;
import edu.incense.android.survey.JsonSurvey;
import edu.incense.android.survey.Survey;

public class JsonProject {
    public final static String TIMESTAMP = "timestamp";
    public final static String NAME = "name";
    public final static String SENSORS = "sensors";
    public final static String APPKEY = "appKey";

    private ObjectMapper mapper;

    public JsonProject() {
        mapper = new ObjectMapper(); // can reuse, share globally
    }

    public ProjectSignature getProjectSignature(InputStream input) {
        if(input == null) return null;
        JsonNode root = getRoot(input);
        return getProjectSignature(root);
    }

    public ProjectSignature getProjectSignature(File file) {
        JsonNode root = getRoot(file);
        return getProjectSignature(root);
    }

    private ProjectSignature getProjectSignature(JsonNode root) {
        if(root == null) return null;
        ProjectSignature projectSignature = null;
        try {
            projectSignature = new ProjectSignature();

            JsonNode attribute = root.get(TIMESTAMP);
            projectSignature.setTimestamp(attribute.getValueAsLong());

            attribute = root.get(NAME);
            projectSignature.setName(attribute.getValueAsText());

            attribute = root.get(APPKEY);
            projectSignature.setAppKey(attribute.getValueAsText());

            attribute = root.get(SENSORS);
            List<TaskType> sensors = mapper.readValue(attribute,
                    new TypeReference<List<TaskType>>() {
                    });
            projectSignature.setSensors(sensors);

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
        return projectSignature;
    }

    public void toJson(String fileName, ProjectSignature projectSignature) {
        toJson(new File(fileName), projectSignature);
    }

    public void toJson(OutputStream output, ProjectSignature projectSignature) {
        try {
            mapper.writeValue(output, projectSignature);
        } catch (JsonParseException e) {
            Log.e(getClass().getName(), "Parsing JSON file failed", e);
        } catch (JsonMappingException e) {
            Log.e(getClass().getName(), "Mapping JSON file failed", e);
        } catch (IOException e) {
            Log.e(getClass().getName(), "Reading JSON file failed", e);
        }
    }

    public void toJson(File file, ProjectSignature projectSignature) {
        try {
            mapper.writeValue(file, projectSignature);
        } catch (JsonParseException e) {
            Log.e(getClass().getName(), "Parsing JSON file failed", e);
        } catch (JsonMappingException e) {
            Log.e(getClass().getName(), "Mapping JSON file failed", e);
        } catch (IOException e) {
            Log.e(getClass().getName(), "Reading JSON file failed", e);
        }
    }

    public final static String SESSIONSSIZE = "sessionsSize";
    public final static String SURVEYSSIZE = "surveysSize";
    public final static String SESSIONS = "sessions";
    public final static String SURVEYS = "surveys";

    public Project getProject(String filename) {
        return getProject(new File(filename));
    }

    public Project getProject(File file) {
        JsonNode root = getRoot(file);
        return getProject(root);
    }

    public Project getProject(InputStream input) {
        if(input == null) return null;
        JsonNode root = getRoot(input);
        return getProject(root);
    }

    private Project getProject(JsonNode root) {
        if(root == null) return null;
        Project project = null;
        try {
            project = new Project();

            JsonNode attribute = root.get(SESSIONSSIZE);
            project.setSessionsSize(attribute.getValueAsInt());

            attribute = root.get(SURVEYSSIZE);
            project.setSurveysSize(attribute.getValueAsInt());

            attribute = root.get(SESSIONS);
            Map<String, JsonNode> map = mapper.readValue(attribute,
                    new TypeReference<Map<String, JsonNode>>() {
                    });

            JsonSession jsonSession = new JsonSession(mapper);
            Map<String, Session> sessions = new HashMap<String, Session>(
                    map.size());
            for (Entry<String, JsonNode> entry : map.entrySet()) {
                sessions.put(entry.getKey(),
                        jsonSession.toSession(entry.getValue()));
            }
            project.setSessions(sessions);

            if (project.getSurveysSize() > 0) {
                attribute = root.get(SURVEYS);
                map = mapper.readValue(attribute,
                        new TypeReference<Map<String, JsonNode>>() {
                        });

                JsonSurvey jsonSurvey = new JsonSurvey(mapper);
                Map<String, Survey> surveys = new HashMap<String, Survey>(
                        map.size());
                for (Entry<String, JsonNode> entry : map.entrySet()) {
                    surveys.put(entry.getKey(),
                            jsonSurvey.toSurvey(entry.getValue()));
                }
                project.setSurveys(surveys);
            } else {
                project.setSurveys(new HashMap<String, Survey>());
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
        return project;
    }

    private JsonNode getRoot(File file) {
        try {
            JsonNode root = mapper.readValue(file, JsonNode.class);
            return root;
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
    }

    private JsonNode getRoot(InputStream input) {
        try {
            JsonNode root = mapper.readValue(input, JsonNode.class);
            return root;
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
    }
}
