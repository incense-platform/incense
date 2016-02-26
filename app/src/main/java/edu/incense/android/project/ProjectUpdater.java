package edu.incense.android.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import edu.incense.android.R;
import edu.incense.android.comm.Downloader;
import edu.incense.android.project.validator.DeviceProjectValidator;
import edu.incense.android.project.validator.ProjectValidator;
import edu.incense.android.project.validator.UserProjectValidator;

/***
 * ProjectUpdater checks if there is a new version of the project assigned to
 * this application. If there is a new project, replaces the old one with
 * it.
 * 
 * @author Moises Perez (mxpxgx@gmail.com)
 * @version 0.2, 2011/06/01
 */

public class ProjectUpdater {
    private final static String TAG = "ProjectUpdater";
    private Context context;
    private ProjectSignature projectSignature;
    private volatile boolean updating;
    private JsonProject jsonProject;
    private ProjectUpdateListener listener;

    public ProjectUpdater(Context context) {
        this.context = context;
        updating = false;
        jsonProject = new JsonProject();
    }

    public synchronized void setListener(ProjectUpdateListener listener) {
        this.listener = listener;
    }

    private ProjectSignature getProjectSignature() {
        return projectSignature;
    }

    /**
     * isOnline - Check if there is a NetworkConnection
     * 
     * @return boolean, true if there's a connection
     */
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * getJsonProjectFromServer - Get JSON file containing the Project data from
     * server
     * 
     * @return File, a JSON file
     */
    private File getJsonSignatureFromServer() {
        String signatureFilename = context.getResources().getString(
                R.string.project_new_signature_filename);
        Downloader downloader = new Downloader(context);
        boolean downloaded = downloader.getProjectDataTo(signatureFilename);
        if (downloaded) {
            return new File(signatureFilename);
        } else {
            return null;
        }
    }

    private File getJsonProjectFromServer() {
        String projectDataFilename = context.getResources().getString(
                R.string.project_filename);
        Downloader downloader = new Downloader(context);
        boolean downloaded = downloader.getProjectConfigTo(projectDataFilename);
        if (downloaded) {
            return new File(projectDataFilename);
        } else {
            return null;
        }
    }

    private boolean newProject() {
        if (isOnline()) {
            File signatureFile = getJsonSignatureFromServer();
            if (signatureFile == null) {
                Log.i(getClass().getName(),
                        "Project signature file doesn't exist.");
                return false;
            } else {
                projectSignature = jsonProject
                        .getProjectSignature(signatureFile);
                if (projectSignature == null) {
                    Log.i(getClass().getName(),
                            "Failed to parse JSON to ProjectSignature.");
                    return false;
                }
                return true;
            }
        } else {
            Log.i(getClass().getName(), "Internet connection unavailable.");
            return false;
        }
    }

    private boolean isDifferentProject(ProjectSignature newSignature) {
        String filename = context.getResources().getString(
                R.string.project_signature_filename);
//        File oldFile = new File(context.getResources().getString(
//                R.string.project_signature_filename));
        InputStream input=null;
        try {
            input = context.openFileInput(filename);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File [" + filename + "] not found", e);
        }
        ProjectSignature oldSignature = jsonProject
                .getProjectSignature(input);
        if (oldSignature == null) {
            Log.i(getClass().getName(),
                    "Failed to parse JSON to ProjectSignature.");
            // Return true to include a new valid file
            return true;
        }
        if (oldSignature.getName().compareTo(newSignature.getName()) == 0) {
            if (oldSignature.getTimestamp() == oldSignature.getTimestamp()) {
                return false;
            }
        }
        return true;
    }

    public void replaceOldSignatureWith(ProjectSignature projectSignature) {
        jsonProject.toJson(
                context.getResources().getString(
                        R.string.project_signature_filename), projectSignature);
    }

    private boolean isSignatureValid(ProjectSignature projectSignature) {
        ProjectValidator validator;
        validator = new UserProjectValidator(context);
        boolean valid = validator.isValid(projectSignature);
        validator = new DeviceProjectValidator(context);
        valid = valid && validator.isValid(projectSignature);
        valid = valid && isDifferentProject(projectSignature);
        return valid;
    }

    public synchronized boolean updateProject() {
        if (newProject()) {
            if (isSignatureValid(getProjectSignature())) {
                updating = true;
                replaceOldSignatureWith(getProjectSignature());
                File projectFile = getJsonProjectFromServer();
                InputStream input = null;
                try {
                    input = context.openFileInput(projectFile.getName());
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "File [" + projectFile.getName() + "] not found", e);
                }
//                Project project = jsonProject.getProject(projectFile);
                Project project = jsonProject.getProject(input);
                if (listener != null && project != null) {
                    listener.update(project);
                }
                updating = false;
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public synchronized boolean isUpdating() {
        return updating;
    }
}
