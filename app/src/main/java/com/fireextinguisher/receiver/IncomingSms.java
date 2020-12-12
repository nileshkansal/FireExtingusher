package com.fireextinguisher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingSms extends BroadcastReceiver {

    public static final String TAG = IncomingSms.class.getSimpleName();
    private static SmsListener mListener;

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public static void unbindListener() {
        mListener = null;
    }

    @Override

    public void onReceive(Context context, Intent intent) {
        // this function is trigged when each time a new SMS is received on device.
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        String format = data.getString("format");
        SmsMessage smsMessage = null;
        Log.e(TAG, "onReceive format =====> " + format);

        if (pdus != null) {
            for (Object pdu : pdus) { // loop through and pick up the SMS of interest
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                } else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                }

                String messageBody = smsMessage.getMessageBody();

                if (messageBody != null && messageBody.contains("Your STRAP Verification code is: ")) {
                    Log.e(TAG, "messageBody =====>  " + messageBody);
                    // your custom logic to filter and extract the OTP from relevant SMS - with regex or any other way.
                    String temp = messageBody.replace("Your STRAP Verification code is: ", "");
                    //Pass on the text to our listener.
                    if (mListener != null)
                        mListener.messageReceived(temp);
                    break;
                }
            }
        }
    }
}