package de.openfiresource.falarm.models.rest;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;

import de.openfiresource.falarm.utils.Preferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private static final String header = "Authorization";

    private final Preferences preferences;

    /**
     * Constructor for Token interceptor
     * @param preferences
     */
    public TokenInterceptor(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request modifiedRequest = original;

        String token = preferences.getUserKey().get();
        if (!TextUtils.isEmpty(token)) {
            modifiedRequest = original.newBuilder()
                    .addHeader(header, "Token " + token)
                    .build();
        }

        return chain.proceed(modifiedRequest);
    }
}
