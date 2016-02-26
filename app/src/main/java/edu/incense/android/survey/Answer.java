package edu.incense.android.survey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Answer implements Serializable {
    private static final long serialVersionUID = 3111517536971905268L;
    private boolean answered;
    private boolean skipped;
    private String answer;
    private List<Integer> selectedOptions;

    public Answer() {
        answered = false;
        skipped = false;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    private void setAnswered(boolean answered) {
        this.answered = answered;
        setSkipped(false);
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setSelectedOptions(List<Integer> selectedOptions) {
        this.selectedOptions = selectedOptions;
        setAnswered(true);
    }

    public List<Integer> getSelectedOptions() {
        return selectedOptions;
    }

    public void selectOption(int option, QuestionType questionType) {
        if (selectedOptions == null)
            selectedOptions = new ArrayList<Integer>();
        if (questionType == QuestionType.RADIOBUTTONS) {
            selectedOptions.add(0, option);
            setAnswered(true);
        } else if (questionType == QuestionType.CHECKBOXES
                && !selectedOptions.contains(option)) {
            selectedOptions.add(option);
            setAnswered(true);
        }
    }

    public boolean deselectOption(int option) {
        boolean removed = false;
        if (selectedOptions != null) {
            removed = selectedOptions.remove(Integer.valueOf(option));
            if (selectedOptions.isEmpty())
                setAnswered(false);
        }
        return removed;
    }

    public int getSelectedOption() {
        if (selectedOptions == null)
            return -1;
        if (selectedOptions.isEmpty()) {
            return -1;
        }
        return selectedOptions.get(0);
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        setAnswered(true);
    }

    public void setAnswer(int answer) {
        this.answer = String.valueOf(answer);
        setAnswered(true);
    }

    public String getAnswer() {
        return answer;
    }
}
