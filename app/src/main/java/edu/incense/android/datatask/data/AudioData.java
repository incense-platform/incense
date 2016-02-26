package edu.incense.android.datatask.data;

public class AudioData extends Data {
    private byte[] audioFrame;

    public AudioData() {
        super(DataType.AUDIO);
    }

    /**
     * @param audioFrame the audioFrame to set
     */
    public void setAudioFrame(byte[] audioFrame) {
        this.audioFrame = audioFrame;
    }

    /**
     * @return the audioFrame
     */
    public byte[] getAudioFrame() {
        return audioFrame;
    }
}
