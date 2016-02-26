package edu.incense.android.sensor;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import edu.incense.android.datatask.data.PhoneStateData;

/**
 * Implements a PhoneStateListener and updates every possible state obtained
 * with this class. This includes: Call forwarding, call state, cell location,
 * data connection state, message waiting indicator, service state, signal
 * strength
 * 
 * Permissions needed: READ_PHONE_STATE, ACCESS_COARSE_LOCATION
 * 
 * @author mxpxgx
 * @version 0.1, 05/09/2011
 */

public class PhoneStateSensor extends Sensor {
    private TelephonyManager telephonyManager;

    // private PhoneStateData phoneState;
    // private PhoneCallData phoneCall;

    public PhoneStateSensor(Context context) {
        super(context);
        setName("States");
        String service = Context.TELEPHONY_SERVICE;
        telephonyManager = (TelephonyManager) context.getSystemService(service);

        int values = PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                | PhoneStateListener.LISTEN_CALL_STATE
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_ACTIVITY
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
                | PhoneStateListener.LISTEN_SERVICE_STATE
                | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;

        // Testing with 511 (1FF hex) is pending, not sure if it's going to work
        // int values = 0x000001FF;

        telephonyManager.listen(listener, values);// PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    private void newState(String state) {
        currentData = new PhoneStateData();
        ((PhoneStateData) currentData).setState(state);
    }

    private void newState(String state, String extra) {
        newState(state);
        ((PhoneStateData) currentData).setExtra(extra);
    }

    private PhoneStateListener listener = new PhoneStateListener() {

        /**
         * Indicates that the call-forwarding indicator changes
         * 
         * @see android.telephony.PhoneStateListener#onCallForwardingIndicatorChanged(boolean)
         */
        @Override
        public void onCallForwardingIndicatorChanged(boolean cfi) {
            if (cfi) {
                newState("callForwarding");
            }
        }

        /**
         * Indicates if phone is idle, ringing or offhook (call exists)
         * 
         * @see android.telephony.PhoneStateListener#onCallStateChanged(int,
         *      java.lang.String)
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {

            case TelephonyManager.CALL_STATE_IDLE:
                newState("idle", incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                newState("offhook", incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                newState("ringing", incomingNumber);
                break;

            }
        }

        /**
         * Indicates if cell location changed
         * 
         * @see android.telephony.PhoneStateListener#onCellLocationChanged(android.telephony.CellLocation)
         */
        @Override
        public void onCellLocationChanged(CellLocation location) {
            switch (telephonyManager.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                newState("cdmaLocation", location.toString());
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                newState("gsmLocation", location.toString());
                break;
            }
        }

        /**
         * Indicates if data activity changes (none, in, out, inout, dormant)
         * 
         * Dormant: Data connection is active, but physical link is down.
         * 
         * @see android.telephony.PhoneStateListener#onDataActivity(int)
         */
        @Override
        public void onDataActivity(int direction) {
            switch (direction) {

            case TelephonyManager.DATA_ACTIVITY_NONE:
                newState("dataNone");
                break;
            case TelephonyManager.DATA_ACTIVITY_IN:
                newState("dataActivityIn");
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                newState("dataActivityOut");
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                newState("dataActivityInOut");
                break;
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                newState("dataActivityDormant");
                break;

            }
        }

        /**
         * Indicates if data is disconnected, connecting, connected or suspended
         * 
         * @see android.telephony.PhoneStateListener#onDataConnectionStateChanged(int,
         *      int)
         */
        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            String netType = null;
            switch (networkType) {

            case TelephonyManager.NETWORK_TYPE_1xRTT:
                netType = "1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                netType = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                netType = "EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                netType = "EVDO0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                netType = "EVDOA";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                netType = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                netType = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                netType = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                netType = "HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                netType = "UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                netType = "UNKNOWN";
                break;

            }

            switch (state) {

            case TelephonyManager.DATA_DISCONNECTED:
                newState("dataDisconnected", netType);
                break;
            case TelephonyManager.DATA_CONNECTING:
                newState("dataConnecting", netType);
                break;
            case TelephonyManager.DATA_CONNECTED:
                newState("dataConnected", netType);
                break;
            case TelephonyManager.DATA_SUSPENDED:
                newState("dataSuspended", netType);
                break;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.telephony.PhoneStateListener#onMessageWaitingIndicatorChanged
         * (boolean)
         */
        @Override
        public void onMessageWaitingIndicatorChanged(boolean mwi) {
            if (mwi) {
                newState("messageWaiting");
            }
        }

        /*
         * Indicates the device service states
         * 
         * @see
         * android.telephony.PhoneStateListener#onServiceStateChanged(android
         * .telephony.ServiceState)
         */
        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            switch (serviceState.getState()) {

            case ServiceState.STATE_EMERGENCY_ONLY:
                newState("emergencyOnly");
                break;
            case ServiceState.STATE_IN_SERVICE:
                newState("inService");
                break;
            case ServiceState.STATE_OUT_OF_SERVICE:
                newState("outOfService");
                break;
            case ServiceState.STATE_POWER_OFF:
                newState("powerOff");
                break;
            }
        }

        /**
         * Indicates if the signal stregth has changed. NOTE: only suppots GSM
         * and CDMA at the moment. TODO add support for EDVO
         * 
         * @see android.telephony.PhoneStateListener#onSignalStrengthsChanged(android.telephony.SignalStrength)
         */
        @Override
        public void onSignalStrengthsChanged(SignalStrength ss) {
            if (ss.isGsm()) {
                newState("signalStrength",
                        String.valueOf(ss.getGsmSignalStrength()));
            } else if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                newState("signalStrength", String.valueOf(ss.getCdmaDbm()));
            }
        }

    };

}
