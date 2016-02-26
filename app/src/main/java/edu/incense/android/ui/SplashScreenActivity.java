/**
 * 
 */
package edu.incense.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import edu.incense.android.R;

/**
 * Splash screen that shows a logo of InCense, checks if the user has already
 * setup its username/account
 * 
 * @author Moises Perez (incense.cicese@gmail.com)
 * @version 0.1, May 23, 2011
 * 
 */
public class SplashScreenActivity extends Activity {

    private boolean userLogged;

    private Thread thread;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Check if the application was started for the first time (the user has
        // already logged in)
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        userLogged = sp.getBoolean("loggedin", false);
        
        // Set the time this screen will be visible
        final int splashScreenTime = Integer.parseInt(getResources().getString(
                R.string.splashscreen_time));

        // thread for displaying the SplashScreen
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(splashScreenTime);
                    }

                } catch (InterruptedException e) {
                } finally {

                    // If the application has been started before, start
                    // RecordActivity. If not (it's started for the first time),
                    // start LoginActivity.
                    if (userLogged) {
                        startRecordActivity();
                    } else {
                        startLoginActivity();
                    }

                    finish();
                }
            }
        };

        thread.start();
    }

    private void startRecordActivity() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            synchronized (thread) {
                thread.notifyAll();
            }
        }
        return true;
    }

}