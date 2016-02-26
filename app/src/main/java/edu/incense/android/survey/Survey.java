package edu.incense.android.survey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Survey implements Serializable, ReadOnlySurvey {
    private static final long serialVersionUID = 5496128017069179229L;
    private int id;
    private String title;
    private List<Question> questions;

    public void add(Question question) {
        if (questions == null) {
            questions = new ArrayList<Question>();
        }
        questions.add(question);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Question getQuestion(int index) {
        return questions.get(index);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return questions.size();
    }
}
