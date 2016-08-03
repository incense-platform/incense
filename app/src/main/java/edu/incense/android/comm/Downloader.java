package edu.incense.android.comm;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.incense.android.R;

public class Downloader extends Connection {

    private String serverProjectAddress;
    private String serverProjectPath;
    private String serverProjectConfigPath;
    private String componentGeneratorPath;

    public Downloader(Context context) {
        super(context);
    }

    protected void setServersFromPreferences() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        serverProjectAddress = sp.getString("editTextProjectServerAddress",
                "http://incense.itson.edu.mx");
    }

    @Override
    protected void setPathsFromResource() {
        super.setPathsFromResource();
        serverProjectPath = context.getResources().getString(
                R.string.server_project_signature_path);
        serverProjectConfigPath = context.getResources().getString(
                R.string.server_project_path);
        componentGeneratorPath = "/componentgenerator";
    }

    public boolean getProjectDataTo(String filePath) {
        if (!setupHttpRequest(serverProjectAddress + serverProjectPath,
                ConnectionType.INPUT, JSON_TYPE))
            return false;
        return getData(filePath, false);
    }

    public boolean getProjectConfigTo(String filePath) {
        if (!setupHttpRequest(serverProjectAddress + serverProjectConfigPath,
                ConnectionType.INPUT, JSON_TYPE))
            return false;
        return getData(filePath, false);
    }

    public boolean getComponent(String componentID, String campaignID, String fileSavePath){
        if (!setupHttpRequest(serverProjectAddress + componentGeneratorPath + "/" + componentID +
                "/" + campaignID, ConnectionType.INPUT, ""))
            return false;
        return getData(fileSavePath, false);
    }
    
    // GET implemented with HttpURLConnection
    private boolean getData(String filePath, boolean isResource) {

        if (!isConnected()) {
            Log.i(getClass().getName(), "File transmistion failed.");
            return false;
        }

        try {

            // Set streams
            FileOutputStream fos;
            if (!isResource) {
                File parent = new File(Environment.getExternalStorageDirectory(), parentDirectory);
                parent.mkdirs();
                File file = new File(filePath);
                fos = new FileOutputStream(file);
            } else {
                fos = context.openFileOutput(filePath, 0);
            }

            DataInputStream dis = new DataInputStream(connection.getInputStream());

            // GET
            // Read bytes until EOF to write
            int bytesAvailable = dis.available();
            int bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];

            // How many bytes in buffer
            int bytes_read;
            while ((bytes_read = dis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytes_read);
            }

            // Close streams
            fos.flush();
            fos.close();
            dis.close();

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            Log.i("HTTP", serverResponseCode + " : " + serverResponseMessage);
            Log.i(getClass().getName(), "Succesful file transmistion.");
            return true;
        } catch (Exception e) {
            // Exception handling
            Log.i(getClass().getName(), "File transmistion failed.");
            Log.e(getClass().getName(), "File transmistion failed.", e);
            return false;
        } finally {
            disconnect();
            Log.i(getClass().getName(), "Disconnected after file transmition.");
        }
    }

}
