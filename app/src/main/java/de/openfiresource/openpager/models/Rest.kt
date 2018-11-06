package de.openfiresource.openpager.models

import de.openfiresource.openpager.models.api.*
import de.openfiresource.openpager.utils.Constants
import de.openfiresource.openpager.utils.Preferences
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Rest @Inject internal constructor(preferences: Preferences) {

    private val service: OpenPagerService

    init {
        val logging = HttpLoggingInterceptor { message -> Timber.tag("OkHttp").v(message) }
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(TokenInterceptor(preferences))
                .addInterceptor(logging)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BACKEND_URL_API)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        service = retrofit.create(OpenPagerService::class.java)
    }

    internal fun login(userLogin: UserLogin): Single<UserKey> {
        return service.login(userLogin).subscribeOn(Schedulers.io())
    }

    internal fun logout(): Completable {
        return service.logout().subscribeOn(Schedulers.io())
    }

    internal fun putDeviceInfo(device: Device): Completable {
        return service.putDeviceInfo(device.fcmToken, device).subscribeOn(Schedulers.io())
    }
}
