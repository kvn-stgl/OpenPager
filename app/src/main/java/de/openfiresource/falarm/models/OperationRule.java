package de.openfiresource.falarm.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.Date;
import java.util.List;

/**
 * Created by stieglit on 02.08.2016.
 */
public class OperationRule extends SugarRecord {

    String title;
    boolean activate;
    boolean ownNotification;
    String searchText;
    int priority;
    String startTime;
    String stopTime;

    public OperationRule() {
    }

    public OperationRule(String title) {
        this.title = title;
        this.priority = 1;
        this.activate = true;
        this.ownNotification = false;
        this.startTime = "00:00";
        this.stopTime = "23:59";
        this.save();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = Integer.parseInt(priority);
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public boolean isOwnNotification() {
        return ownNotification;
    }

    public void setOwnNotification(boolean ownNotification) {
        this.ownNotification = ownNotification;
    }

    public Notification getNotification(Context context) {
        return Notification.byRule(this, context);
    }

    @Override
    public boolean delete() {
        return super.delete();
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s)", this.title,
                this.startTime, this.stopTime);

    }
}
