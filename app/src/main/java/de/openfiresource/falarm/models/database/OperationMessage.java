package de.openfiresource.falarm.models.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity(tableName = "operation_message")
public class OperationMessage {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String key;

    private String title;
    private String message;
    private String latlng;

    private Date timestamp;
    private Date timestampIncoming;

    private boolean seen;
    private boolean alarm;

    @Ignore
    public OperationMessage() {
        this(UUID.randomUUID().toString());
    }

    public OperationMessage(@NonNull String key) {
        this.key = key;
    }

    void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLatlng() {
        return latlng;
    }

    @Nullable
    public Pair<Double, Double> getLatLngPair() {
        if(TextUtils.isEmpty(latlng)) {
            return null;
        }

        String[] latlng = this.latlng.split(";");
        double lat = Double.parseDouble(latlng[0]);
        double lng = Double.parseDouble(latlng[1]);

        return new Pair<>(lat, lng);
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestampIncoming() {
        return timestampIncoming;
    }

    public void setTimestampIncoming(Date timestampIncoming) {
        this.timestampIncoming = timestampIncoming;
    }

    public Boolean isSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public OperationRule getRule() {
        return new OperationRule("Example");
    }

    public void setRule(OperationRule operationRule) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationMessage that = (OperationMessage) o;
        return id == that.id &&
                Objects.equals(key, that.key) &&
                Objects.equals(title, that.title) &&
                Objects.equals(message, that.message) &&
                Objects.equals(latlng, that.latlng) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(timestampIncoming, that.timestampIncoming) &&
                Objects.equals(seen, that.seen) &&
                Objects.equals(alarm, that.alarm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, title, message, latlng, timestamp, timestampIncoming, seen, alarm);
    }
}
