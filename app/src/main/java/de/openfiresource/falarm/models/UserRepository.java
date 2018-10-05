package de.openfiresource.falarm.models;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.openfiresource.falarm.models.api.UserKey;
import de.openfiresource.falarm.models.api.UserLogin;
import de.openfiresource.falarm.utils.Preferences;
import io.reactivex.Single;

@Singleton
public class UserRepository {

    private final Rest rest;

    private final Preferences preferences;

    @Inject
    UserRepository(Rest rest, Preferences preferences) {
        this.rest = rest;
        this.preferences = preferences;
    }

    public Single<UserKey> login(String email, String password) {
        UserLogin user = new UserLogin.Builder()
                .email(email)
                .password(password)
                .build();

        return rest.login(user).doOnSuccess(userKey -> {
            if (userKey != null) {
                preferences.getUserKey().set(userKey.getKey());
            }
        });
    }
}
