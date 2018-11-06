package de.openfiresource.openpager.models

import com.google.firebase.iid.FirebaseInstanceId
import de.openfiresource.openpager.models.api.UserLogin
import de.openfiresource.openpager.utils.DeviceInfoHelper
import de.openfiresource.openpager.utils.Preferences
import io.reactivex.Completable
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject internal constructor(private val rest: Rest, private val preferences: Preferences) {

    fun login(email: String, password: String): Completable {
        val user = UserLogin(email, password)

        return rest.login(user)
                .doOnSuccess { userKey ->
                    userKey.key?.let { preferences.userKey.set(it) }
                }
                .flatMapCompletable { (_) -> sendDeviceInfo(FirebaseInstanceId.getInstance().token) }
                .doOnError { preferences.userKey.set("") }
    }

    fun logout(): Completable {
        return rest.logout()
                .onErrorResumeNext { throwable ->
                    if (throwable is UnknownHostException) {
                        return@onErrorResumeNext Completable.error(throwable)
                    }

                    // We don't know if it's a server error so we have to log out the user
                    preferences.userKey.set("")
                    Completable.complete()
                }
                .doOnComplete { preferences.userKey.set("") }
    }

    fun sendDeviceInfo(fcmToken: String?): Completable {
        fcmToken?.let {
            val device = DeviceInfoHelper.create(it)
            return rest.putDeviceInfo(device)
        }
        return Completable.error(Throwable("No FCM-Token delivered"));
    }
}
