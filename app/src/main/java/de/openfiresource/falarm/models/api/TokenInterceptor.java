package de.openfiresource.falarm.models.api;

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
     *
     * @param preferences
     */
    public TokenInterceptor(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        builder.header("Accept", "application/json"); //if necessary, say to consume JSON

        String token = preferences.getUserKey().get(); //save token of this request for future
        setAuthHeader(builder, token); //write current token to request

        request = builder.build(); //overwrite old request
        Response response = chain.proceed(request); //perform request, here original request will be executed

        if (response.code() == 401) { //if unauthorized
            // TODO: 07.10.2018 logout
        }
        return response;
    }

    private void setAuthHeader(Request.Builder builder, String token) {
        if (!TextUtils.isEmpty(token)) //Add Auth token to each request if authorized
            builder.header("Authorization", String.format("Token %s", token));
    }
}
