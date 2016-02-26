/**
 * 
 */
package edu.incense.android.survey;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author mxpxgx
 *
 */
public class AnswersContainer implements Serializable  {
    private static final long serialVersionUID = 3812253052610498131L;
    private List<Answer> answers;
    private static String dataType = "SURVEY";
    private String survey;
    private int surveyId;
    private long timestamp;

    public AnswersContainer(Answer[] answers, Survey survey){
        this.answers = Arrays.asList(answers);
        timestamp = System.currentTimeMillis();
        this.survey = survey.getTitle();
        surveyId = survey.getId();
    }
    
    /**
     * @return the answers
     */
    public List<Answer> getAnswers() {
        return answers;
    }

    /**
     * @param answers the answers to set
     */
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    /**
     * @return the dataType
     */
    public static String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public static void setDataType(String dataType) {
        AnswersContainer.dataType = dataType;
    }

    /**
     * @return the survey
     */
    public String getSurvey() {
        return survey;
    }

    /**
     * @param survey the survey to set
     */
    public void setSurvey(String survey) {
        this.survey = survey;
    }

    /**
     * @return the surveyId
     */
    public int getSurveyId() {
        return surveyId;
    }

    /**
     * @param surveyId the surveyId to set
     */
    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
}
