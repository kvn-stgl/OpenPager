package de.openfiresource.falarm;


import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.service.AlarmService;
import de.openfiresource.falarm.ui.operation.OperationActivity;
import de.openfiresource.falarm.utils.OperationHelper;
import de.openfiresource.falarm.utils.Preferences;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Inject
    AppDatabase database;

    @Inject
    Preferences preferences;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

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

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 && preferences.isPushActive()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            OperationHelper.createOperationFromFCM(preferences, database, remoteMessage.getData())
                    .observeOn(Schedulers.io())
                    .map(operationMessage -> database.operationMessageDao().insertOperationMessage(operationMessage))
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<Long>() {
                        @Override
                        public void onSuccess(Long id) {
                            //Start alarm Service
                            Intent intentData = new Intent(getBaseContext(), AlarmService.class);
                            intentData.putExtra(OperationActivity.OPERATION_ID, id);

                            //First stop old service when exist, then start new
                            stopService(intentData);
                            startService(intentData);

                            dispose();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: error saving operation", e);
                            dispose();
                        }
                    });
        }
    }
    // [END receive_message]
}