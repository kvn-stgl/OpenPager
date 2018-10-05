package de.openfiresource.falarm;


import android.content.Intent;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.UserRepository;
import de.openfiresource.falarm.service.AlarmService;
import de.openfiresource.falarm.ui.operation.OperationActivity;
import de.openfiresource.falarm.utils.OperationHelper;
import de.openfiresource.falarm.utils.Preferences;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Inject
    AppDatabase database;

    @Inject
    Preferences preferences;

    @Inject
    UserRepository userRepository;

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
        Timber.d("From: %s", remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 && preferences.isPushActive()) {
            Timber.d("Message data payload: %s", remoteMessage.getData());
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
                            Timber.e(e, "onError: error saving operation");
                            dispose();
                        }
                    });
        }
    }
    // [END receive_message]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Timber.d("Refreshed token: %s", token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        if(!TextUtils.isEmpty(preferences.getUserKey().get())) {
            userRepository.sendDeviceInfo(token)
                    .subscribe(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            Timber.d("Successfully sent token %s to server", token);
                            dispose();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e, "Error sending token to server");
                            dispose();
                        }
                    });
        }
    }
}