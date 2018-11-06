package de.openfiresource.openpager.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserLogin(
        @SerializedName("email")
        @Expose
        val email: String,

        @SerializedName("password")
        @Expose
        val password: String
)