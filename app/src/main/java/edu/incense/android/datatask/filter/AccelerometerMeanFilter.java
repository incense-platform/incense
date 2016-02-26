package edu.incense.android.datatask.filter;

import edu.incense.android.datatask.data.AccelerometerData;
import edu.incense.android.datatask.data.Data;

public class AccelerometerMeanFilter extends DataFilter {
    private double totalX = 0;
    private double totalY = 0;
    private double totalZ = 0;

    private double meanX = 0;
    private double meanY = 0;
    private double meanZ = 0;

    private int counter = 0;

    public AccelerometerMeanFilter() {
        super();
        setFilterName(this.getClass().getName());

        totalX = 0;
        totalY = 0;
        totalZ = 0;

        meanX = 0;
        meanY = 0;
        meanZ = 0;

        counter = 0;
    }

    @Override
    protected void computeSingleData(Data data) {
        AccelerometerData newData = (AccelerometerData) data;
        newData = computeNewMean(newData);
        pushToOutputs(newData);
    }

    private AccelerometerData computeNewMean(AccelerometerData newData) {
        double newX = newData.getAxisX();
        double newY = newData.getAxisY();
        double newZ = newData.getAxisZ();

        // Add new values
        totalX += newX;
        totalY += newY;
        totalZ += newZ;

        counter++;

        // Calculate mediums
        meanX = totalX / counter;
        meanY = totalY / counter;
        meanZ = totalZ / counter;

        return new AccelerometerData(meanX, meanY, meanZ);
    }

}
