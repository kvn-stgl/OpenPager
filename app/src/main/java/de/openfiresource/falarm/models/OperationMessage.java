package de.openfiresource.falarm.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.contextmanager.internal.InterestUpdateBatchImpl;
import com.orhanobut.logger.Logger;
import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.openfiresource.falarm.utils.EncryptionUtils;

import static de.openfiresource.falarm.utils.EncryptionUtils.decrypt;

/**
 * Created by stieglit on 02.08.2016.
 */
public class OperationMessage extends SugarRecord {
    OperationRule rule;
    String title;
    Date timestamp;
    Date timestampIncoming;

    String key;
    String message;
    String latlng;
    Boolean seen;
    JSONObject content;

    public OperationMessage() {

    }

    public OperationRule getRule() {
        return rule;
    }

    public void setRule(OperationRule rule) {
        this.rule = rule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public static OperationMessage fromFCM(Context context, Map<String, String> extras) {

        OperationMessage incoming = new OperationMessage();
        Set<String> keys = extras.keySet();
        JSONObject content = new JSONObject();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean encryption = prefs.getBoolean("sync_encryption", false);
        String encryptionKey = prefs.getString("sync_password", "");

        try {
            for (String string : keys) {
                String value = URLDecoder.decode(extras.get(string), EncryptionUtils.CHARACTER_ENCODING);
                switch (string) {
                    case "awf_message":
                        if (encryption) value = decrypt(value, encryptionKey);
                        incoming.setMessage(value);
                        break;
                    case "awf_title":
                        if (encryption) value = decrypt(value, encryptionKey);
                        incoming.setTitle(value);
                        break;
                    case "awf_key":
                        if (encryption) value = decrypt(value, encryptionKey);
                        incoming.setKey(value);
                        break;
                    case "awf_latlng":
                        if (encryption) value = decrypt(value, encryptionKey);
                        incoming.setLatlng(value);
                        break;
                    case "awf_timestamp":
                        if (encryption) value = decrypt(value, encryptionKey);
                        try {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            incoming.setTimestamp(dateFormat.parse(value));
                        } catch (ParseException e) {
                            // TODO to user
                            Logger.e(e, "Error parsing incoming date");
                        }
                        break;
                    default:
                        try {
                            if (string.startsWith("awf_")) {
                                if (encryption) value = decrypt(value, encryptionKey);
                            }
                            content.put(string, value);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                }
            }
        } catch (Exception exception) {
            Logger.e(exception, "Error parsing incoming Operation");
            return null;
        }

        //Get the rule
        OperationRule bestRule = null;
        SimpleDateFormat ho = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        Calendar now = Calendar.getInstance();

        for (OperationRule rule : SugarRecord.listAll(OperationRule.class)) {
            Calendar startValue = GregorianCalendar.getInstance();
            Calendar stopValue = GregorianCalendar.getInstance();
            try {
                Date start = ho.parse(rule.getStartTime());
                Date stop = ho.parse(rule.getStopTime());

                startValue.setTime(start);
                stopValue.setTime(stop);

                //No (Milli)Seconds -> 00:00 to 23:59
                startValue.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                startValue.set(Calendar.SECOND, 0);
                startValue.set(Calendar.MILLISECOND, 0);
                stopValue.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                stopValue.set(Calendar.MILLISECOND, 0);
                stopValue.set(Calendar.SECOND, 0);

                //22:00 to 06:00 as example. 06:00 is next night
                if (start.after(stop) && start.after(now.getTime())) {
                    startValue.add(Calendar.DATE, -1);
                } else if (start.after(stop) && start.before(now.getTime())) {
                    stopValue.add(Calendar.DATE, +1);
                }
            } catch (ParseException e) {
                Logger.e(e, "Error parsing start/stop time");
                e.printStackTrace();
            }

            //if rule in date?
            if (now.compareTo(startValue) >= 0 && now.compareTo(stopValue) <= 0) {
                //SearchText in message?
                if (TextUtils.isEmpty(rule.getSearchText())
                        || incoming.getTitle().matches(rule.getSearchText())
                        || incoming.getMessage().matches(rule.getSearchText())) {

                    //Bigger priority
                    if (bestRule == null || bestRule.getPriority() < rule.priority)
                        bestRule = rule;
                }
            }
        }

        Logger.d("Incoming operation: %s\n%s", incoming.getTitle(), incoming.getMessage());

        incoming.setContent(content);
        incoming.setSeen(false);
        incoming.setRule(bestRule);
        incoming.setTimestampIncoming(now.getTime());

        if (incoming.getTimestamp() == null)
            incoming.setTimestamp(now.getTime());

        return incoming;
    }
}
