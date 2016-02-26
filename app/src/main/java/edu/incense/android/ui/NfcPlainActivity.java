/**
 * 
 */
package edu.incense.android.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import edu.incense.android.R;

/**
 * @author mxpxgx
 * 
 */
public class NfcPlainActivity extends Activity {
    private static final String TAG = "NfcPlainActivity";
    private NfcAdapter mNfcAdapter;
    private MediaPlayer ring;

    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mWriteTagFilters;
    private IntentFilter[] mNdefExchangeFilters;

    private TextView nfcText;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        setContentView(R.layout.nfc_plain);

        nfcText = (TextView) findViewById(R.id.textview_nfcmessage);

        // Handle all of our received NFC intents in this activity.
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("application/incense-nfctag");
        } catch (MalformedMimeTypeException e) {
            Log.e(TAG, "Ndef intent filter failed", e);
        }
        IntentFilter techDetected = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);

        mNdefExchangeFilters = new IntentFilter[] { ndefDetected, techDetected };

        // Intent filters for writing to a tag
        IntentFilter tagDetected = new IntentFilter(
                NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] { tagDetected };

        // Load NFC sound
        ring = MediaPlayer.create(this,
                Settings.System.DEFAULT_NOTIFICATION_URI);

        // Set volume according to user current settings
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int vol = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        ring.setVolume(vol, vol);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sticky notes received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            String message = new String(payload);
            setIntent(new Intent()); // Consume this intent.
            sendBroadcast(message);
        }
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                mNdefExchangeFilters, null);

        (new Thread(runnable)).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundNdefPush(this);
        if (ring != null) {
            try {
                ring.release();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Could not be released.", e);
            }
        }
        // finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] messages = getNdefMessages(intent);
            byte[] payload = messages[0].getRecords()[0].getPayload();
            String message = new String(payload);
            this.setIntent(intent);

            sendBroadcast(message);

            runnable.resetEventTime();
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            String message = getNfcAMessage(intent);
            this.setIntent(intent);
            sendBroadcast(message);

            runnable.resetEventTime();
        }

    }

    private CloseRunnable runnable = new CloseRunnable();

    public class CloseRunnable implements Runnable {
        private final static int TIME_LENGTH_PER_EVENT = 2000;
        volatile private long startTime;
        volatile private long timeLength;

        private synchronized boolean isTimeUp() {
            long currentTime = System.currentTimeMillis();
            long time = currentTime - startTime;
            // Log.d(TAG, currentTime+" - "+startTime+" = "+time);
            return (time >= timeLength);
        }

        public synchronized void resetEventTime() {
            startTime = System.currentTimeMillis();
            if (ring != null) {
                try {
                    ring.start();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Could not be started.", e);
                }
            }
        }

        private synchronized void setTimeLength(int timeLength) {
            this.timeLength = timeLength;
        }

        public void run() {
            resetEventTime();
            setTimeLength(TIME_LENGTH_PER_EVENT);
            while (!isTimeUp()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Sleep failed", e);
                }
            }
            NfcPlainActivity.this.finish();
        }
    }

    private NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
                        empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private String getNfcAMessage(Intent intent) {
        // Parse the intent
        String msg = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = (Tag) intent
                    .getParcelableExtra(NfcAdapter.ACTION_TECH_DISCOVERED);
            if (tag != null) {
                NfcA nfcA = NfcA.get(tag);
                msg = new String(nfcA.getAtqa());
            }
        }
        return msg;
    }

    public final static String NFC_TAG_ACTION = "edu.incense.android.NFC_TAG_ACTION";
    public final static String ACTION_NFC_TAG = "action_nfctag";

    public void sendBroadcast(String message) {
        //Show message in the screen
        nfcText.setText(message);
        // Send broadcast the end of this process
        Intent broadcastIntent = new Intent(NFC_TAG_ACTION);
        broadcastIntent.putExtra(ACTION_NFC_TAG, message);
        sendBroadcast(broadcastIntent);
        Log.d(TAG, "New NFC tag intent was broadcasted");
    }

    // private void toast(String text) {
    // Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    // }
}
