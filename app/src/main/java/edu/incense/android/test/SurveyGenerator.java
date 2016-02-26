/**
 * 
 */
package edu.incense.android.test;

import edu.incense.android.survey.Question;
import edu.incense.android.survey.QuestionType;
import edu.incense.android.survey.Survey;

/**
 * @author mxpxgx
 *
 */
public class SurveyGenerator {
    
    public static Survey createWanderingMindSurvey(){
        Survey survey = new Survey();
        survey.setId(101);
        survey.setTitle("Wandering Mind");
        
        Question question = new Question();
        question.setQuestion("How are you feeling right now?");
        question.setType(QuestionType.SEEKBAR);
        question.setSkippable(false);
        String[] options = { "Bad", "Good" };
        question.setOptions(options);
        int[] nextQuestions1 = { 1 };
        question.setNextQuestions(nextQuestions1);
        survey.add(question);
        
        question = new Question();
        question.setQuestion("¿Por qué ha salido a caminar?");
        question.setType(QuestionType.OPENTEXT);
        question.setSkippable(false);
        int[] nextQuestions2 = { 2 };
        question.setNextQuestions(nextQuestions2);
        survey.add(question);
        
        question = new Question();
        question.setQuestion("Are you thinking about something other than what you're currently doing?");
        question.setType(QuestionType.RADIOBUTTONS);
        question.setSkippable(false);
        String[] options2 = { "No", "Yes, something pleasant",
                "Yes, something neutral", "Yes, something unpleasant" };
        question.setOptions(options2);
        int[] nextQuestions3 = { 0, 0, 0, 0 };
        question.setNextQuestions(nextQuestions3);
        survey.add(question);
        
        return survey;
    }
    
    public static Survey createMindSurveyWithAudio(){
        Survey survey = new Survey();
        survey.setId(101);
        survey.setTitle("Wandering Mind");
        
        Question question = new Question();
        question.setQuestion("How are you feeling right now?");
        question.setType(QuestionType.SEEKBAR);
        question.setSkippable(false);
        String[] options = { "Bad", "Good" };
        question.setOptions(options);
        int[] nextQuestions1 = { 1 };
        question.setNextQuestions(nextQuestions1);
        survey.add(question);
        
        question = new Question();
        question.setQuestion("Are you thinking about something other than what you�re currently doing?");
        question.setType(QuestionType.RADIOBUTTONS);
        question.setSkippable(false);
        String[] options2 = { "No", "Yes, something pleasant",
                "Yes, something neutral", "Yes, something unpleasant" };
        question.setOptions(options2);
        int[] nextQuestions3 = { 2, 2, 2, 2 };
        question.setNextQuestions(nextQuestions3);
        survey.add(question);
        
        question = new Question();
        question.setQuestion("Desea grabar?");
        question.setType(QuestionType.RADIOBUTTONS);
        question.setSkippable(false);
        String[] options3 = { "Si", "No" };
        question.setOptions(options3);
        int[] nextQuestions4 = { 0, 0, 0, 0 };
        question.setNextQuestions(nextQuestions4);
        survey.add(question);
        
        return survey;
    }

}
