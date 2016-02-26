/**
 * 
 */
package edu.incense.android.survey;

/**
 * @author mxpxgx
 *
 */
public interface ReadOnlyQuestion {
    public String getQuestion();
    public String[] getOptions();
    public QuestionType getType();
    public void setNextQuestions(int[] nextQuestions);
    public int[] getNextQuestions();
    public int getNextQuestion(int option);
    public boolean isSkippable();
    public int size();
}
