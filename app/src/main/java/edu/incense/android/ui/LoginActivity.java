package edu.incense.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import edu.incense.android.R;
import edu.incense.android.setup.LoginTask;
import edu.incense.android.ui.RecordActivity;

/**
 * This is the first screen the user sees when starts the InCense application
 * for the first time. The user can enter his username and password to login and
 * setup, or select to sign-up to the InCense project (starting SignupActivity).
 * Alerts/notices will be presented with Toasts (eg. unknown user, wrong
 * password).
 * 
 * @author Moises Perez (mxpxgx@gmail.com)
 * @since 2011/05/17
 * @version 0.3, 2011/05/18
 */
public class LoginActivity extends Activity implements
        LoginTask.LoginTaskListener {
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";

    private ProgressBar progressBar = null;
    private LoginTask loginTask = null;
    private String username = "";

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final EditText etUsername = (EditText) findViewById(R.id.edittext_username);
        final EditText etPassword = (EditText) findViewById(R.id.edittext_password);

        final Button bLogin = (Button) findViewById(R.id.button_login);
        final Button bRegister = (Button) findViewById(R.id.button_register);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        // If necessary, initialize LoginTaks.
        // If not then set to local member.
        loginTask = (LoginTask) getLastNonConfigurationInstance();
        if (loginTask == null) {
            loginTask = new LoginTask(this);
        } else {
            loginTask.setListener(this);
            // TODO test orientation change, it may need the commented code
            // here:
            /*
             * if (progressBar.getProgress() == 50) { // TODO set a constant
             * with // 50? progressBar.setVisibility(ProgressBar.VISIBLE); }
             */

        }

        bLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                username = etUsername.getText().toString();
                login(username, etPassword.getText().toString());
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startRegisterActivity();
            }
        });

    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        // TODO move this code to a splash screen?
        // Check if the user has already logged in
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean loggedin = sp.getBoolean("loggedin", false);
        // If he has, move/go directly to the main activity
        if(loggedin){
            startRecordActivity();
        }
    }

    /**
     * Called before activity is destroyed
     * 
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        loginTask.removeListener();
        return loginTask;
    }

    /**
     * Starts a login task with the username and password provided, if it's
     * successful, starts a new setup session (TODO setup session?).
     */
    private void login(String username, String password) {
        if (username.length() == 0) {
            Toast.makeText(this, getText(R.string.username_empty),
                    Toast.LENGTH_SHORT).show();
        } else if (password.length() == 0) {
            Toast.makeText(this, getText(R.string.password_empty),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Feedback to the user (progressBar)
            progressBar.setVisibility(ProgressBar.VISIBLE);
            // Start login task: authentication of the user
            loginTask.execute(username, password);
        }
    }

    /**
     * Starts the MainActivity. This method is called by a LoginTask if the user
     * was successfully logged in.
     */
    private void startRecordActivity() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    /**
     * Starts a RegisterActivity in order to register a new user
     */
    private void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Saves the username to the SharedPreferences
     * 
     * @param username
     */
    private void setUsernameInPreferences(String username) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);

        // Retrieve an editor to modify the shared preferences.
        SharedPreferences.Editor editor = sp.edit();

        // Store new primitive types in the shared preferences object.
        editor.putString("editTextUsername", username);

        // Set a flag to prevent this activity from starting automatically
        // again.
        editor.putBoolean("loggedin", true);

        // Commit the changes.
        editor.commit();
    }

    /**
     * Receives the the result from the authentication process and shows the
     * result to the user. If logged successfully, start the MainActivity. Also,
     * stops the progress bar.
     * 
     * @param result
     */
    public void onLoginTaskComplete(int result) {

        String resultMessage = null;
        if (result == LoginTask.LOGGED_IN) {
            setUsernameInPreferences(username);
            resultMessage = getString(R.string.logged_in_message) + " "
                    + username + "!";
            startRecordActivity();
        } else if (result == LoginTask.UNKNOWN_USER) {
            resultMessage = getString(R.string.unknown_username_message);
        } else if (result == LoginTask.INCORRECT_PASSWORD) {
            resultMessage = getString(R.string.logged_in_message);
        } else if (result == LoginTask.NO_CONNECTION) {
            resultMessage = getString(R.string.no_connection_message);
        }

        // Hides/Stops the progressBar
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        // Show a feedback message to the user
        if (resultMessage != null) {
            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
