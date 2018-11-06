package de.openfiresource.openpager.models

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

import de.openfiresource.openpager.R
import de.openfiresource.openpager.models.database.OperationRule

class Notification private constructor(private val ruleId: Long, private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(getSharedPreferencesName(ruleId), Context.MODE_PRIVATE)

    /**
     * Gibt an, ob ein Ton bei einem neuen Alarm gespielt werden soll.
     *
     * @return True, wenn ein Ton gespielt wird.
     */
    val isPlayingSound: Boolean
        get() = sharedPreferences.getBoolean("notifications_play_sound", true)

    /**
     * Gibt den Alarmton an.
     *
     * @return Alarmton.
     */
    val ringtone: String
        get() = sharedPreferences.getString("notifications_new_message_ringtone", "")!!

    /**
     * Gibt das Volumen in Prozent an, wie laut der Ton ist.
     *
     * @return Volume in Prozent, 0 wenn Telefonlautstärke.
     */
    val newMessageVolume: String
        get() = sharedPreferences.getString("notifications_new_message_volume", "0")!!

    /**
     * Gibt die Dauern an, wie lange das Handy bei einem neuen Alarm vibriert.
     *
     * @return Dauer, 0 wenn aus.
     */
    val newMessageVibrate: String
        get() = sharedPreferences.getString("notifications_new_message_vibrate", "1000")!!

    /**
     * Gibt an, ob die LED bei einen neuen Alarm blinken soll.
     *
     * @return True, wenn blinken
     */
    val isNewMessageLED: Boolean?
        get() = sharedPreferences.getBoolean("notifications_new_message_led", false)

    /**
     * Gibt an, ob der SpeakService aktiviert ist.
     *
     * @return True, wenn aktiviert
     */
    val isSpeakServiceEnabled: Boolean?
        get() = sharedPreferences.getBoolean("notifications_speak", false)

    fun loadDefault() {
        PreferenceManager.setDefaultValues(context, getSharedPreferencesName(ruleId), Context.MODE_PRIVATE, R.xml.pref_notification, false)
    }

    fun delete() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {

        operator fun get(ruleId: Long, context: Context): Notification {
            return Notification(ruleId, context)
        }

        fun byRule(rule: OperationRule?, context: Context): Notification {
            var id: Long = 0
            if (rule != null && rule.isOwnNotification) {
                id = rule.id!!
            }

            return get(id, context)
        }

        /**
         * A name for the shared preference.
         *
         * @return SharedPreferences Name
         */
        fun getSharedPreferencesName(id: Long): String {
            return "rule_$id"
        }
    }
}
