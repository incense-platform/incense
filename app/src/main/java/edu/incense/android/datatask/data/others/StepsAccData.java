package edu.incense.android.datatask.data.others;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.DataType;


public class StepsAccData extends Data {
    private short steps=0;
    private double velocity=0;
    private double distance=0;
    private double calories=0;

    public StepsAccData() {
        super(DataType.STEPS);
    }

    public StepsAccData(short steps, double velocity, double distance, double calories) {
        super(DataType.STEPS);
        this.setSteps(steps);
        this.setVelocity(velocity);
        this.setDistance(distance);
        this.setCalories(calories);
    }

   
    public void setSteps(short steps) {
        this.steps = steps;
    }

    public short getSteps() {
        return steps;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getCalories() {
        return calories;
    }

}
