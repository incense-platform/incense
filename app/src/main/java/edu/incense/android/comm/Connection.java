package edu.incense.android.comm;

import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import edu.incense.android.R;

public abstract class Connection {
    protected final static String JSON_TYPE = "application/json";
    protected final static String AUDIO_3GP_TYPE = "audio/3gp";

    protected final static int MAX_BUFFER_SIZE = 4 * 1024; // In bytes

    protected HttpURLConnection connection;
    protected String httpClientId;
    protected String parentDirectory;
    protected Context context;
    protected final String mBoundary = "*****";
    protected final String mLineEnd = "\r\n";
    protected final String mTwoHyphens = "--";


    protected Connection(Context context) {
        connection = null;
        this.context = context;
        setServersFromPreferences();
        setPathsFromResource();
    }

    protected abstract void setServersFromPreferences();

    protected void setPathsFromResource() {
        httpClientId = context.getResources()
                .getString(R.string.http_client_id);
        parentDirectory = context.getResources()
        .getString(R.string.application_root_directory);
    }

    protected enum ConnectionType {
        INPUT, OUTPUT, BOTH
    }

    protected boolean setupHttpRequest(String serverAddress,
                                       ConnectionType connectionType, String contentType) {
        connection = null;
        try {
            Log.i(getClass().getName(), "Trying to stablish connection to"
                    + serverAddress + "...");

            // Initialize connection
            URL url = new URL(serverAddress);
            connection = (HttpURLConnection) url.openConnection();

//            if (Build.VERSION.SDK_INT > 13)
//                connection.setRequestProperty("Connection", "close"); // This line prevents known bug that throws EOFException.

            // Allow Inputs/Outputs
            if (connectionType == ConnectionType.INPUT
                    || connectionType == ConnectionType.BOTH) {
                connection.setDoInput(true);
                // Enable GET method
                connection.setRequestMethod("GET");
            } else
                connection.setDoInput(false);

            if (connectionType == ConnectionType.OUTPUT
                    || connectionType == ConnectionType.BOTH) {
                connection.setDoOutput(true);
                // Enable POST method
                connection.setRequestMethod("POST");
            } else
                connection.setDoOutput(false);

            connection.setUseCaches(false);

            // Set properties
            connection.setRequestProperty("User-Agent", httpClientId);
            //connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Connection", "Keep-Alive");

            if (connectionType == ConnectionType.OUTPUT)
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + mBoundary);


            // Establish connection
            //connection.setupHttpRequest();

            Log.i(getClass().getName(), "Connection stablished.");
            return true;

        } catch (Exception e) {
            // Exception handling
            Log.i(getClass().getName(), "Connection failed.");
            Log.e(getClass().getName(), "Connection failed.", e);
            return false;
        }
    }

    public boolean isConnected() {
        if (connection != null)
            return true;
        else
            return false;
    }

    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
            Log.i(getClass().getName(), "Disconnected.");
        } else {
            Log.i(getClass().getName(), "Already disconnected.");
        }
    }
}
