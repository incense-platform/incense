package edu.incense.android.datatask.model;

public class TaskRelation {
    private String task1;
    private String task2;

    public TaskRelation() {
        task1 = null;
        task2 = null;
    }

    public TaskRelation(String task1, String task2) {
        this.setTask1(task1);
        this.setTask2(task2);
    }

    public void setTask1(String task1) {
        this.task1 = task1;
    }

    public String getTask1() {
        return task1;
    }

    public void setTask2(String task2) {
        this.task2 = task2;
    }

    public String getTask2() {
        return task2;
    }
}
