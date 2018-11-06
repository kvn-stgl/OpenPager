package de.openfiresource.openpager.models.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface OpenPagerService {

    @get:GET("auth/user/")
    val user: Single<User>

    @POST("auth/login/")
    fun login(@Body user: UserLogin): Single<UserKey>

    @POST("auth/logout/")
    fun logout(): Completable

    @PUT("auth/user/")
    fun putUser(): Single<User>

    @PUT("v1/devices/{token}/")
    fun putDeviceInfo(@Path("token") token: String, @Body deviceInfo: Device): Completable
}
