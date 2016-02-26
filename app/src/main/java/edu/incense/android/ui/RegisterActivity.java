package edu.incense.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import edu.incense.android.R;

/**
 * Activity class to register a user with the given inputs. Once the user
 * completes this form, the data is validated and sent to a server. If he's
 * successfully registered, the Settings activity is started to let the user
 * personalize the application.
 * TODO validad email y otros datos
 * 
 * @author Moises Perez (mxpxgx@gmail.com)
 * @since 2011/05/05
 * @version 0.2, 2011/05/18
 */
public class RegisterActivity extends Activity {
    private Button bSubmit;
    private Button bCancel;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        bSubmit = (Button) findViewById(R.id.button_submit);
        bCancel = (Button) findViewById(R.id.button_cancel);

        bSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                submit();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                cancel();
            }
        });

    }

    /**
     *
     */
    private void submit() {

    }

    /**
     * Cancel this registration (activity), returning to the LoginActivity
     */
    private void cancel() {
        finish();
    }
}
