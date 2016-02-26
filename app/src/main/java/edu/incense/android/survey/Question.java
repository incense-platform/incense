package edu.incense.android.survey;

import java.io.Serializable;

public class Question implements Serializable, ReadOnlyQuestion {
    private static final long serialVersionUID = 3311962415192464801L;
    public final static int MAX_OPTIONS = 5; // Maximum number of answers
    private String question;
    private String[] options; // all possible answers
    private int[] nextQuestions; // position of the next question, based on the
                                 // selected option
    private QuestionType type;
    private boolean skippable;

    public Question() {
        type = QuestionType.NULL;
        skippable = true;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public void setNextQuestions(int[] nextQuestions) {
        this.nextQuestions = nextQuestions;
    }

    public int[] getNextQuestions() {
        return nextQuestions;
    }

    public int getNextQuestion(int option) {
        return nextQuestions[option];
    }

    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }

    public boolean isSkippable() {
        return skippable;
    }
    
    public int size() {
        if (getOptions().length > MAX_OPTIONS) {
            return MAX_OPTIONS;
        } else {
            return getOptions().length;
        }
    }

}
