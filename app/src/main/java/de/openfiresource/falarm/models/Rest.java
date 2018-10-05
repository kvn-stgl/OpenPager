package de.openfiresource.falarm.models;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.openfiresource.falarm.models.api.OpenPagerService;
import de.openfiresource.falarm.models.api.UserKey;
import de.openfiresource.falarm.models.api.UserLogin;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Singleton
public class Rest {

    private final OpenPagerService service;

    @Inject
    Rest() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Timber.tag("OkHttp").v(message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://area51.openpager.de/api/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        service = retrofit.create(OpenPagerService.class);
    }

    Single<UserKey> login(UserLogin userLogin) {
        return service.login(userLogin).subscribeOn(Schedulers.io());
    }

    public Single<ResponseBody> logout() {
        return service.logout().subscribeOn(Schedulers.io());
    }
}
