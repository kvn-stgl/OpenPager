package de.openfiresource.falarm.models.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("fcm_token")
    @Expose
    private String fcmToken;

    @SerializedName("device")
    @Expose
    private String device;

    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;

    @SerializedName("device_name")
    @Expose
    private String deviceName;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("platform")
    @Expose
    private String platform;

    private Device(Builder builder) {
        fcmToken = builder.fcmToken;
        device = builder.device;
        manufacturer = builder.manufacturer;
        deviceName = builder.deviceName;
        version = builder.version;
        platform = builder.platform;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getDevice() {
        return device;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getVersion() {
        return version;
    }

    public String getPlatform() {
        return platform;
    }

    public static final class Builder {
        private String fcmToken;
        private String device;
        private String manufacturer;
        private String deviceName;
        private String version;
        private String platform;
        private String idiom;

        public Builder() {
        }

        public Builder(Device copy) {
            this.fcmToken = copy.getFcmToken();
            this.device = copy.getDevice();
            this.manufacturer = copy.getManufacturer();
            this.deviceName = copy.getDeviceName();
            this.version = copy.getVersion();
            this.platform = copy.getPlatform();
        }

        public Builder fcmToken(String val) {
            fcmToken = val;
            return this;
        }

        public Builder device(String val) {
            device = val;
            return this;
        }

        public Builder manufacturer(String val) {
            manufacturer = val;
            return this;
        }

        public Builder deviceName(String val) {
            deviceName = val;
            return this;
        }

        public Builder version(String val) {
            version = val;
            return this;
        }

        public Builder platform(String val) {
            platform = val;
            return this;
        }

        public Device build() {
            return new Device(this);
        }
    }
}
