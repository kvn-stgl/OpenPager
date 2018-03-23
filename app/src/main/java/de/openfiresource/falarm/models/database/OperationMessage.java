package de.openfiresource.falarm.models.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;
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

    private Boolean seen;

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

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public OperationRule getRule() {
        return new OperationRule("Example");
    }

    public void setRule(OperationRule operationRule) {

    }
}
