package de.openfiresource.falarm.models;

import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.openfiresource.falarm.models.api.Device;
import de.openfiresource.falarm.models.api.UserLogin;
import de.openfiresource.falarm.utils.DeviceInfoHelper;
import de.openfiresource.falarm.utils.Preferences;
import io.reactivex.Completable;
import io.reactivex.functions.Consumer;

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

    public Completable sendDeviceInfo(String fcmToken) {
        Device device = DeviceInfoHelper.create(fcmToken);
        return rest.putDeviceInfo(device);
    }
}
