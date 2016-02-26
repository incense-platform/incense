/**
 * 
 */
package edu.incense.android.datatask.trigger;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.util.Log;

/**
 * @author mxpxgx
 *
 */
public class JsonTrigger {
    public final static String CONDITIONS = "conditions";
    public final static String MATCHES = "matches";

    private ObjectMapper mapper;

    public JsonTrigger() {
        mapper = new ObjectMapper(); // can reuse, share globally
    }

    public JsonTrigger(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<Condition> toConditions(JsonNode root) {
        List<Condition> conditions = null;
        try {
            JsonNode attribute = root.get(CONDITIONS);
            conditions = mapper.readValue(attribute,
                    new TypeReference<List<Condition>>() {
                    });
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
        return conditions;
    }
}
