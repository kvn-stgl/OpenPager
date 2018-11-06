package de.openfiresource.openpager.models.api

import android.text.TextUtils
import de.openfiresource.openpager.utils.Preferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Constructor for Token interceptor
 *
 * @param preferences
 */
class TokenInterceptor(private val preferences: Preferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val builder = request.newBuilder()
        builder.header("Accept", "application/json") //if necessary, say to consume JSON

        val token = preferences.userKey.get() //save token of this request for future
        setAuthHeader(builder, token) //write current token to request

        request = builder.build() //overwrite old request
        val response = chain.proceed(request) //perform request, here original request will be executed

        if (response.code() == 401) { //if unauthorized
            // TODO: 07.10.2018 logout
        }
        return response
    }

    private fun setAuthHeader(builder: Request.Builder, token: String) {
        //Add Auth token to each request if authorized
        if (!TextUtils.isEmpty(token)) {
            builder.header("Authorization", String.format("Token %s", token))
        }
    }
}
