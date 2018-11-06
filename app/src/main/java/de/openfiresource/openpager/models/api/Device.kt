package de.openfiresource.openpager.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Device(
        @SerializedName("fcm_token")
        @Expose
        val fcmToken: String,

        @SerializedName("device")
        @Expose
        val device: String,

        @SerializedName("manufacturer")
        @Expose
        val manufacturer: String,

        @SerializedName("device_name")
        @Expose
        val deviceName: String,

        @SerializedName("version")
        @Expose
        val version: String,

        @SerializedName("platform")
        @Expose
        val platform: String
)
