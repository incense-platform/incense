package edu.incense.android.survey;

import java.io.Serializable;
import java.util.Stack;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SurveyController implements Serializable {
    private static final String TAG = "SurveyController";
    private static final long serialVersionUID = 3516516719837433621L;
    private ReadOnlySurvey survey;
    private Answer[] answers;
    private int index; // Index of current question
    private Stack<Integer> surveyPath; // stores the path (indexes) the user
                                       // takes in a survey.

    public SurveyController(Survey survey) {
        this.survey = survey;
        answers = new Answer[getSize()];
        surveyPath = new Stack<Integer>();
        index = 0;
    }

    public void saveAnswersTo(String fileName) {
        JsonSurvey jsonSurvey = new JsonSurvey();
        AnswersContainer container = new AnswersContainer(answers,
                (Survey) survey);
        jsonSurvey.toJson(fileName, container);
    }

    public void saveAnswersTo(AnswersContainer container, String fileName) {
        JsonSurvey jsonSurvey = new JsonSurvey();
        jsonSurvey.toJson(fileName, container);
    }
    
    public static final String SUERVEY_ANSWERS_ACTION = "edu.incense.android.SURVEY_ANSWERS";
    public static final String SURVEY_FIELDNAME = "answersContainer";

    public void sendBroadcast(AnswersContainer container, Context context){
        Log.d(TAG, "Survey [" + survey.getTitle() + "] answered");
        // Send broadcast the end of this process
        Intent broadcastIntent = new Intent(SUERVEY_ANSWERS_ACTION);
        broadcastIntent.putExtra(SURVEY_FIELDNAME, container);
        context.sendBroadcast(broadcastIntent);
        Log.d(TAG, "Survey answers broadcasted");
    }
    
    public void saveFileAndSendBroadcast(Context context, String fileName){
        AnswersContainer container = new AnswersContainer(answers,
                (Survey) survey);
        saveAnswersTo(container, fileName);
        sendBroadcast(container, context);
    }

    public boolean isEmpty() {
        if (survey == null) {
            return true;
        }
        if (getQuestion() == null) {
            return true;
        }
        return false;
    }

    /***
     * Initializes answer of the current question if necessary, that is, if it
     * hasn't been set, it calls a constructor of a new answer and adds it to
     * the given index of the answers array.
     * 
     * @param i
     *            index of the answer to initialize (number of question in the
     *            survey)
     */
    private Answer initializeAnswer(int i) {
        if (answers[i] == null) {
            answers[i] = new Answer();
        }
        return answers[i];
    }

    /***
     * Returns current answer (initializes it if necessary)
     * 
     * @return Answer - current answer
     */
    public Answer getAnswer() {
        return initializeAnswer(index);
    }

    /**
     * Get current question
     * 
     * @return
     */
    public ReadOnlyQuestion getQuestion() {
        return survey.getQuestion(index);
    }

    public boolean isFirstQuestion() {
        if (index == 0)
            return true;
        return false;
    }

    public boolean isLastQuestion() {
        if (index == (getSize() - 1))
            return true;
        return false;
    }

    public boolean isSurveyComplete() {
        if ((index + 1) < getSize()) {
            return false;
        }
        for (int i = 0; i < answers.length; i++) {
            if (answers[i] == null)
                return false;
            if (!answers[i].isSkipped() && !answers[i].isAnswered())
                return false;
        }
        return true;
    }

    /**
     * Advances to the next question. Reasons for not advancing to the next
     * question are:
     * 
     * @return "true" if it successfully advanced to next question. "false" if
     *         for some reason it didn't advance to the next question.
     */
    public boolean next() {
        surveyPath.push(index); // register the question number
        // Obtains array with possible next questions, depending on the answer.
        int[] nextQuestions = getQuestion().getNextQuestions();
        // e.g. If answer is option 0, then the value of index=0 in
        // nextQuestions is the number of the next question.
        // NOTE: This only applies when when QuestionType is RADIOBUTTONS.
        if (getQuestion().getType() == QuestionType.RADIOBUTTONS
                && nextQuestions != null && getAnswer().isAnswered()) {
            index = nextQuestions[getAnswer().getSelectedOption()];
        } else {
            // Otherwise, advance to the next index.
            index = index < getSize() ? index + 1 : index;
        }
        // If resulting index is the same as the next one in the surveyPath
        // it means it didn't move.
        if (surveyPath.peek() == index)
            return false;
        return true;
    }

    public boolean back() {
        if (surveyPath.empty())
            return false;
        else
            index = surveyPath.pop();
        return true;
    }

    public boolean skip() {
        if (getQuestion().isSkippable()) {
            getAnswer().setSkipped(true);
            return next();
        }
        return false;
    }

    private int getSize() {
        return survey.getSize();
    }
}
