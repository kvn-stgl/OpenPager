package de.openfiresource.falarm.models.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("pk")
    @Expose
    private Integer pk;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    private User(Builder builder) {
        pk = builder.pk;
        username = builder.username;
        email = builder.email;
        firstName = builder.firstName;
        lastName = builder.lastName;
    }

    public Integer getPk() {
        return pk;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public static final class Builder {
        private Integer pk;
        private String username;
        private String email;
        private String firstName;
        private String lastName;

        public Builder() {
        }

        public Builder(User copy) {
            this.pk = copy.getPk();
            this.username = copy.getUsername();
            this.email = copy.getEmail();
            this.firstName = copy.getFirstName();
            this.lastName = copy.getLastName();
        }

        public Builder pk(Integer val) {
            pk = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}