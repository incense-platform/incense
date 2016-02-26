package edu.incense.android.datatask.sink;

import java.util.List;

import edu.incense.android.datatask.data.Data;

public interface SinkWritter {
    public void writeSink(DataSink dataSink);
    public void writeSink(String name, List<Data> sink);
}
