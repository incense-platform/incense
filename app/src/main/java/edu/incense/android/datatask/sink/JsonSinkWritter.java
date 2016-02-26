package edu.incense.android.datatask.sink;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.util.Log;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.results.QueueFileTask;
import edu.incense.android.results.ResultFile;
import edu.incense.android.results.ResultsUploader;

public class JsonSinkWritter implements SinkWritter {
    private static final String TAG = "JsonSinkWritter";
    private Context context;
    private ObjectMapper mapper;

    public JsonSinkWritter(Context context) {
        this.context = context;
        mapper = new ObjectMapper();
    }

    public void writeSink(DataSink dataSink) {
        write(dataSink.getName(), dataSink.getSink());
    }
    
    
    /**
     * @see edu.incense.android.datatask.sink.SinkWritter#writeSink(java.lang.String, java.util.List)
     */
    public void writeSink(String name, List<Data> sink) {
        Log.d(TAG, "Sink received by writter with size: "+sink.size());
        Map<String, List<Data>> sinkByType = new HashMap<String, List<Data>>();
        for(Data d: sink){
            if(sinkByType.get(d.getDataType().name())==null){
                Log.d(TAG, "Creating subsink of type ["+d.getDataType().name()+"]...");
                List<Data> newList = new ArrayList<Data>();
                newList.add(d);
                sinkByType.put(d.getDataType().name(), newList);
            } else {
                sinkByType.get(d.getDataType().name()).add(d);
            }
        }
        
        for(List<Data> typeSink: sinkByType.values()){
            write(name, typeSink);
        }
    }
    
    private void write(String name, List<Data> sink) {
        ResultFile resultFile = ResultFile.createDataInstance(context,
                name);
        try {
            Log.d(TAG, "Writing ["+resultFile.getFileName()+"]...");
            mapper.writeValue(new File(resultFile.getFileName()), sink);
        } catch (JsonParseException e) {
            Log.e(TAG, "Parsing JSON file failed", e);
        } catch (JsonMappingException e) {
            Log.e(TAG, "Mapping JSON file failed", e);
        } catch (IOException e) {
            Log.e(TAG, "Writing JSON file failed", e);
        }
        queueFileTask(resultFile);
        System.gc();
        System.runFinalization();
        System.gc();
    }
    
    private void queueFileTask(ResultFile rf) {
        ResultsUploader resultsUploader = new ResultsUploader(context);
        resultsUploader.offerFile(rf);
    }


}
