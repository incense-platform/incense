package edu.incense.android.datatask.data;

/*
 * Accelerometer data contains a frame consisting of a matrix of n*4 of double values.
 */
public class AccelerometerFrameData extends Data {
    public final static int X_AXIS = 0;
    public final static int Y_AXIS = 1;
    public final static int Z_AXIS = 2;
    public final static int TIMESTAMP = 3;
    private double[][] frame;

    private AccelerometerFrameData(DataType type, double[][] frame) {
        super(type);
        this.frame = frame;
    }
    
    public AccelerometerFrameData(double[][] frame) {
        this(DataType.ACCELEROMETER, frame);
    }
    
    public static AccelerometerFrameData createGyroFrameData(double[][] frame) {
        AccelerometerFrameData gyroData = new AccelerometerFrameData(
                DataType.GYROSCOPE, frame);
        return gyroData;
    }

    /**
     * @param frame
     *            the frame to set
     */
    public void setFrame(double[][] frame) {
        this.frame = frame;
    }

    /**
     * @return the frame
     */
    public double[][] getFrame() {
        return frame;
    }

}
