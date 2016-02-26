package edu.incense.android.setup;

import android.os.AsyncTask;

/**
 * AsyncTask class to process an authentication without interrupting the UI.
 * Once it's completed sends a callback to the listener (a class implementing
 * LoginTaskListener). The listener has to be set before executing this task.
 * 
 * @author Moises Perez (mxpxgx@gmail.com)
 * @version 0.1, 2011/05/18
 * 
 */
public class LoginTask extends AsyncTask<String, Integer, Integer> {
    public final static int LOGGED_IN = 0;
    public final static int UNKNOWN_USER = 1;
    public final static int INCORRECT_PASSWORD = 2;
    public final static int NO_CONNECTION = 3;
    public final static int CANCELLED = 4;

    private LoginTaskListener listener;

    public static interface LoginTaskListener {
        public void onLoginTaskComplete(int result);
    }

    public LoginTask(LoginTaskListener listener) {
        super();
        setListener(listener);
    }

    public void setListener(LoginTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub, it has to return different values
        // depending on the results from the server
        // Send a post to the server with the username and password
        // Use SSL encryption
        return LOGGED_IN;
    }

    @Override
    protected void onCancelled() {
        // TODO Auto-generated method stub
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        listener.onLoginTaskComplete(result);
    }
    
    public void removeListener() {
        setListener(null);
    }
}
