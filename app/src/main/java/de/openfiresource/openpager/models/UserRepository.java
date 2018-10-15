package de.openfiresource.openpager.models;

import com.google.firebase.iid.FirebaseInstanceId;

import java.net.UnknownHostException;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.openfiresource.openpager.models.api.Device;
import de.openfiresource.openpager.models.api.UserLogin;
import de.openfiresource.openpager.utils.DeviceInfoHelper;
import de.openfiresource.openpager.utils.Preferences;
import io.reactivex.Completable;

@Singleton
public class UserRepository {

    private final Rest rest;

    private final Preferences preferences;

    @Inject
    UserRepository(Rest rest, Preferences preferences) {
        this.rest = rest;
        this.preferences = preferences;
    }

    public Completable login(String email, String password) {
        UserLogin user = new UserLogin.Builder()
                .email(email)
                .password(password)
                .build();

        return rest.login(user)
                .doOnSuccess(userKey -> {
                    if (userKey.getKey() != null) {
                        preferences.getUserKey().set(userKey.getKey());
                    }
                })
                .flatMapCompletable(userKey -> sendDeviceInfo(FirebaseInstanceId.getInstance().getToken()))
                .doOnError(throwable -> preferences.getUserKey().set(""));
    }

    public Completable logout() {
        return rest.logout()
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof UnknownHostException) {
                        return Completable.error(throwable);
                    }

                    // We don't know if it's a server error so we have to log out the user
                    preferences.getUserKey().set("");
                    return Completable.complete();
                })
                .doOnComplete(() -> preferences.getUserKey().set(""));
    }

    public Completable sendDeviceInfo(String fcmToken) {
        Device device = DeviceInfoHelper.create(fcmToken);
        return rest.putDeviceInfo(device);
    }
}
