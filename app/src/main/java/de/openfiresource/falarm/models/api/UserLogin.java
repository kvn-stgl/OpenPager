package de.openfiresource.falarm.models.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLogin {

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

    private UserLogin(Builder builder) {
        email = builder.email;
        password = builder.password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static final class Builder {
        private String email;
        private String password;

        public Builder() {
        }

        public Builder(UserLogin copy) {
            this.email = copy.getEmail();
            this.password = copy.getPassword();
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public UserLogin build() {
            return new UserLogin(this);
        }
    }
}