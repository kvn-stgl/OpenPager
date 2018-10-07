package de.openfiresource.falarm.models.api;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OpenPagerService {

    @POST("auth/login/")
    Single<UserKey> login(@Body UserLogin user);

    @POST("auth/logout/")
    Completable logout();

    @GET("auth/user/")
    Single<User> getUser();

    @PUT("auth/user/")
    Single<User> putUser();

    @PUT("devices/{token}/")
    Completable putDeviceInfo(@Path("token") String token, @Body Device deviceInfo);
}
