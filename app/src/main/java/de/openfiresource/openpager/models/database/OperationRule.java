package de.openfiresource.openpager.models.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "operation_rule")
public class OperationRule {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String searchText;
    private String startTime;
    private String stopTime;

    private boolean activate;
    private boolean ownNotification;

    private int priority;

    public OperationRule(String title) {
        this.title = title;
        this.priority = 1;
        this.activate = true;
        this.ownNotification = false;
        this.startTime = "00:00";
        this.stopTime = "23:59";
    }

    void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public boolean isOwnNotification() {
        return ownNotification;
    }

    public void setOwnNotification(boolean ownNotification) {
        this.ownNotification = ownNotification;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Cast the string to int.
     * The is used by the preferences.
     *
     * @param priority the priority as string
     */
    public void setPriority(String priority) {
        this.priority = Integer.parseInt(priority);
    }
}
