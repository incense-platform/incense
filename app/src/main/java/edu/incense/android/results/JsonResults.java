package edu.incense.android.results;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.util.Log;

public class JsonResults {
    public final static String FILEQUEUE = "fileQueue";
    public final static String MAXFILES = "maxFiles";

    private ObjectMapper mapper;

    public JsonResults() {
        mapper = new ObjectMapper(); // can reuse, share globally
    }

    public FileQueue toFileQueue(File file) {
        FileQueue fileQueue = new FileQueue();
        try {

            JsonNode root = mapper.readValue(file, JsonNode.class);

            JsonNode attribute = root.get(MAXFILES);
            fileQueue.setMaxFiles(attribute.getValueAsInt());

            attribute = root.get(FILEQUEUE);
            Queue<ResultFile> queue = mapper.readValue(attribute,
                    new TypeReference<Queue<ResultFile>>() {
                    });
            if (queue != null)
                fileQueue.setFileQueue(queue);
            else {
                Log.i(getClass().getName(),
                        "Queue JSON node was empty/null or doesn't exist");
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
        return fileQueue;
    }
    
    public FileQueue toFileQueue(InputStream is) {
        FileQueue fileQueue = new FileQueue();
        try {
            
            JsonNode root = mapper.readValue(is, JsonNode.class);
            
            JsonNode attribute = root.get(MAXFILES);
            fileQueue.setMaxFiles(attribute.getValueAsInt());
            
            attribute = root.get(FILEQUEUE);
            Queue<ResultFile> queue = mapper.readValue(attribute,
                    new TypeReference<Queue<ResultFile>>() {
            });
            if (queue != null)
                fileQueue.setFileQueue(queue);
            else {
                Log.i(getClass().getName(),
                "Queue JSON node was empty/null or doesn't exist");
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
        return fileQueue;
    }

    public String toString(String fileName) {
        return toString(new File(fileName));
    }

    public String toString(File file) {
        String jsonContent;
        try {

            JsonNode root = mapper.readValue(file, JsonNode.class);

            jsonContent = root.toString();
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
        return jsonContent;
    }

    /*
     * public void toJson(String fileName, FileQueue fileQueue){ toJson(new
     * File(fileName), fileQueue); }
     */

    public void toJson(File file, FileQueue fileQueue) {
        Log.i(getClass().getName(),
                "Writting file to: " + file.getAbsoluteFile());
        try {
            mapper.writeValue(file, fileQueue);
        } catch (JsonGenerationException e) {
            Log.e(getClass().getName(), "Creating JSON file failed", e);
        } catch (JsonMappingException e) {
            Log.e(getClass().getName(), "Mapping JSON file failed", e);
        } catch (IOException e) {
            Log.e(getClass().getName(), "IO JSON file failed", e);
        }
    }
    
    public void toJson(OutputStream os, FileQueue fileQueue) {
        try {
            mapper.writeValue(os, fileQueue);
        } catch (JsonGenerationException e) {
            Log.e(getClass().getName(), "Creating JSON file failed", e);
        } catch (JsonMappingException e) {
            Log.e(getClass().getName(), "Mapping JSON file failed", e);
        } catch (IOException e) {
            Log.e(getClass().getName(), "IO JSON file failed", e);
        }
    }
}
