package de.openfiresource.falarm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.openfiresource.falarm.models.OperationMessage;
import de.openfiresource.falarm.service.AlarmService;
import de.openfiresource.falarm.ui.MainActivity;
import de.openfiresource.falarm.ui.OperationActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean activate = preferences.getBoolean("activate", true);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 && activate) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            OperationMessage operationMessage = OperationMessage.fromFCM(this, remoteMessage.getData());
            if (operationMessage != null) {
                long notificationId = operationMessage.save();

                //Send Broadcast
                Intent brIntent = new Intent();
                brIntent.setAction(MainActivity.INTENT_RECEIVED_MESSAGE);
                sendBroadcast(brIntent);

                //Start alarm Service
                Intent intentData = new Intent(getBaseContext(),
                        AlarmService.class);
                intentData.putExtra(OperationActivity.EXTRA_ID, notificationId);

                //Firt stop old service when exist, then start new
                stopService(intentData);
                startService(intentData);
            }
        }
    }
    // [END receive_message]
}