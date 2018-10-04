package de.openfiresource.falarm.models.api;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface OpenPagerService {

    @POST("auth/login")
    Single<ResponseBody> login(@Body UserLogin user);

    @POST("auth/logout")
    Single<ResponseBody> logout();

    @GET("auth/user")
    Single<User> getUser();

    @PUT("auth/user")
    Single<User> putUser();

}
