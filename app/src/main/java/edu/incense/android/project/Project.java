package edu.incense.android.project;

import java.util.HashMap;
import java.util.Map;

import edu.incense.android.session.Session;
import edu.incense.android.survey.Survey;

public class Project {
    private int sessionsSize;
    private int surveysSize;
    private Map<String, Session> sessions;
    private Map<String, Survey> surveys;

    public void put(String key, Session session) {
        if (sessions == null) {
            sessions = new HashMap<String, Session>();
        }
        sessions.put(key, session);
    }

    public void put(String key, Survey survey) {
        if (surveys == null) {
            surveys = new HashMap<String, Survey>();
        }
        surveys.put(key, survey);
    }

    public int getSessionsSize() {
        return sessionsSize;
    }

    public void setSessionsSize(int sessionsSize) {
        this.sessionsSize = sessionsSize;
    }

    public int getSurveysSize() {
        return surveysSize;
    }

    public void setSurveysSize(int surveysSize) {
        this.surveysSize = surveysSize;
    }

    public Map<String, Session> getSessions() {
        return sessions;
    }

    public void setSessions(Map<String, Session> sessions) {
        this.sessions = sessions;
    }

    public Map<String, Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(Map<String, Survey> surveys) {
        this.surveys = surveys;
    }

    public Session getSession(String key) {
        return sessions.get(key);
    }

    public Survey getSurvey(String key) {
        return surveys.get(key);
    }

}
