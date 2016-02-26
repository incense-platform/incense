/**
 * 
 */
package edu.incense.android.datatask.data;

import java.util.List;

import android.util.Log;
import edu.incense.android.survey.Answer;
import edu.incense.android.survey.AnswersContainer;

/**
 * @author mxpxgx
 *
 */
public class SurveyAnswersData extends Data {
    private List<Answer> answers;
    private String answer;
    private String lastAnswer;
    private String survey;
    private int surveyId;

    public SurveyAnswersData(AnswersContainer ac) {
        super(DataType.SURVEY_ANSWERS);
        setAnswers(ac.getAnswers());
        setSurvey(ac.getSurvey());
        setSurveyId(ac.getSurveyId());
        setAnswers(ac.getAnswers());
        setTimestamp(ac.getTimestamp());
        
        //Convert array to String in order to be analyzed by a Trigger
        //It only takes the first and last answer in the survey.
        //TODO Improvements could be made in the GeneralTrigger, to support arrays.
        setAnswer(answers.get(0).getAnswer()); //sets first answer
        if(getAnswer() == null){
            setAnswer(String.valueOf(answers.get(0).getSelectedOption()));
        }
        setLastAnswer(answers.get(answers.size()-1).getAnswer()); //sets last answer
        if(getLastAnswer() == null){
            setLastAnswer(String.valueOf(answers.get(answers.size()-1).getSelectedOption()));
        }
        Log.d("SurveyAnswersData", "Last answer: " +lastAnswer);
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
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(String answer) {
        this.answer = answer;
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
     * @param lastAnswer the lastAnswer to set
     */
    public void setLastAnswer(String lastAnswer) {
        this.lastAnswer = lastAnswer;
    }

    /**
     * @return the lastAnswer
     */
    public String getLastAnswer() {
        return lastAnswer;
    }
    
    

}
