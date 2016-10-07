package edu.incense.android.datatask.model;

import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;

public class Task {
    private TaskType taskType;
    private String name;
    private float sampleFrequency;
    private long periodTime;
    private boolean triggered;
    private JsonNode jsonNode;
    // These fields is used if the task is of type DataFilter, meaning it is using a downloaded component.
    private String componentName;
    private String componentID;
    private String campaignID;

    public Task() {
        taskType = TaskType.NULL;
        sampleFrequency = -1.0f;
        periodTime = -1L;
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }

    public void setJsonNode(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * Gets the name of the component to be used in this task.
     * @return the component name for this task.
     */
    public String getComponentName(){
        return this.componentName;
    }

    /**
     * Sets the component name for this task.
     * @param componentName Name of the component.
     */
    public void setComponentName(String componentName){
        this.componentName = componentName;
    }

    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    public String getCampaignID() {
        return campaignID;
    }

    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (jsonNode == null)
            return defValue;
        JsonNode attribute = jsonNode.get(key);
        if (attribute != null) {
            boolean value = attribute.getValueAsBoolean();
            return value;
        }
        return defValue;
    }

    public int getInt(String key, int defValue) {
        if (jsonNode == null)
            return defValue;
        JsonNode attribute = jsonNode.get(key);
        if (attribute != null) {
            int value = attribute.getValueAsInt();
            return value;
        }
        return defValue;
    }

    public long getLong(String key, long defValue) {
        if (jsonNode == null)
            return defValue;
        JsonNode attribute = jsonNode.get(key);
        if (attribute != null) {
            long value = attribute.getValueAsLong();
            return value;
        }
        return defValue;
    }
    
    public double getDouble(String key, float defValue) {
        if (jsonNode == null)
            return defValue;
        JsonNode attribute = jsonNode.get(key);
        if (attribute != null) {
            double value = attribute.getDoubleValue();
            return value;
        }
        return defValue;
    }

    public String getString(String key, String defValue) {
        if (jsonNode == null)
            return defValue;
        JsonNode attribute = jsonNode.get(key);
        if (attribute != null) {
            String value = attribute.getValueAsText();
            return value;
        }
        return defValue;
    }
    public String[] getStringArray(String key) {
        if (jsonNode == null)
            return null;
        JsonNode attribute = jsonNode.get(key);
        if (attribute != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (JsonNode node : attribute) {
                list.add(node.getValueAsText());
            }
            String[] array = new String[list.size()];
            return list.toArray(array);
        }
        return null;
    }

    public void setSampleFrequency(float sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }

    public float getSampleFrequency() {
        return sampleFrequency;
    }

    /**
     * @param periodTime the periodTime to set
     */
    public void setPeriodTime(long periodTime) {
        this.periodTime = periodTime;
    }

    /**
     * @return the periodTime
     */
    public long getPeriodTime() {
        return periodTime;
    }

    /**
     * @param triggered the triggered to set
     */
    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    /**
     * @return the triggered
     */
    public boolean isTriggered() {
        return triggered;
    }
}