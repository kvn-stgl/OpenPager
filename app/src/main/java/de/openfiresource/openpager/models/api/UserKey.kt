package de.openfiresource.openpager.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserKey(
        @SerializedName("key")
        @Expose
        val key: String?
)
