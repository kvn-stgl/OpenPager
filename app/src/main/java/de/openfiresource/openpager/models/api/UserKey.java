package de.openfiresource.openpager.models.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserKey {

    @SerializedName("key")
    @Expose
    private String key;

    public String getKey() {
        return key;
    }
}
