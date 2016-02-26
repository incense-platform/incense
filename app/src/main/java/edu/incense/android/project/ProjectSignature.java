package edu.incense.android.project;

import java.util.List;

import edu.incense.android.datatask.model.TaskType;

public class ProjectSignature {
    private long timestamp;
    private String name;
    private List<TaskType> sensors;
    private String appKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskType> getSensors() {
        return sensors;
    }

    public void setSensors(List<TaskType> sensors) {
        this.sensors = sensors;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
