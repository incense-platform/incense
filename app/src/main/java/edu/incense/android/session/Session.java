package edu.incense.android.session;

import java.io.Serializable;
import java.util.List;

import edu.incense.android.datatask.model.Task;
import edu.incense.android.datatask.model.TaskRelation;

/**
 * Session model, contains the session configuration.
 * Used by the session manager to start a recording session.
 * @author mxpxgx
 * @version 1.0
 * @since 10/18/2011
 */

public class Session implements Serializable {
    private static final long serialVersionUID = 3232566373112570131L;
    private String name;
    private List<Task> tasks;
    private List<TaskRelation> relations;
    private String durationMeasure; // Time length of recording session
    private long durationUnits; // Time length of recording session
    private boolean autoTriggered; // automatically triggered
    private long startDate; // The date when this session will be executed for
                            // the first time
    private long endDate; // If it's repeating, the date it will stop repeating.
    private String repeatMeasure; // Type of repeating units (eg. Hours)
    private int repeatUnits; // The repeating units length (eg. 8)
                             // If repeatType = hours and repeatUnits = 8, then
                             // this session will repeat every 8 hours
    private boolean repeat;
    private boolean notices;
    private String sessionType;
    private static final String[] SESSION_TYPES = { "User", "Automatic" };
    private static final String[] DURATION_MEASURES = { "minutes", "hours" };
    private static final String[] REPEAT_MEASURES = { "minutes", "hours",
            "days", "weeks", "months" };
    public enum RepeatType {
        NOT_REPEATABLE, MINUTES, HOURS, DAYS, WEEKS, MONTHS
    };

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<TaskRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<TaskRelation> relations) {
        this.relations = relations;
    }

    public void setDurationMeasure(String durationMeasure) {
        this.durationMeasure = durationMeasure;
    }

    public String getDurationMeasure() {
        return durationMeasure;
    }
    
    public void setDurationUnits(long durationUnits) {
        this.durationUnits = durationUnits;
    }
    
    public long getDurationUnits() {
        return durationUnits;
    }

    /**
     * @param autoTriggered
     *            the autoTriggered to set
     */
    public void setAutoTriggered(boolean autoTriggered) {
        this.autoTriggered = autoTriggered;
    }

    /**
     * @return the autoTriggered
     */
    public boolean isAutoTriggered() {
        return autoTriggered;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the startDate
     */
    public long getStartDate() {
        return startDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the endDate
     */
    public long getEndDate() {
        return endDate;
    }

    /**
     * @param repeatType the repeatType to set
     */
    public void setRepeatMeasure(String repeatMeasure) {
        this.repeatMeasure = repeatMeasure;
    }

    /**
     * @return the type
     */
    public String getRepeatMeasure() {
        return repeatMeasure;
    }

    /**
     * @param repeatUnits the repeatUnits to set
     */
    public void setRepeatUnits(int repeatUnits) {
        this.repeatUnits = repeatUnits;
    }

    /**
     * @return the repeatUnits
     */
    public int getRepeatUnits() {
        return repeatUnits;
    }

    /**
     * @return the repeat
     */
    public boolean isRepeat() {
        return repeat;
    }

    /**
     * @param repeat the repeat to set
     */
    public void setRepeat(boolean repeatType) {
        this.repeat = repeatType;
    }

    /**
     * @return the notices
     */
    public boolean isNotices() {
        return notices;
    }

    /**
     * @param notices the notices to set
     */
    public void setNotices(boolean notices) {
        this.notices = notices;
    }

    /**
     * @param sessionType the sessionType to set
     */
    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    /**
     * @return the sessionType
     */
    public String getSessionType() {
        return sessionType;
    }
}
