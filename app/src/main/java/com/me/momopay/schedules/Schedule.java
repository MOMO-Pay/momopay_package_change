package com.me.momopay.schedules;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hover.sdk.actions.HoverAction;
import com.me.momopay.R;
import com.me.momopay.contacts.StaxContact;
import com.me.momopay.utils.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Entity(tableName = "schedules")
public class Schedule {
    public final static int DAILY = 0, WEEKLY = 1, BIWEEKLY = 2, MONTHLY = 3, ONCE = 4;
    public final static String SCHEDULE_ID = "schedule_id", DATE_KEY = "schedule_date", REQUEST_TYPE = "request";

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @NonNull
    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "channel_id")
    public int channel_id;

    @ColumnInfo(name = "action_id")
    public String action_id;

    @NonNull
    @ColumnInfo(name = "recipient_ids")
    public String recipient_ids;

    @ColumnInfo(name = "amount")
    public String amount;

    @ColumnInfo(name = "note")
    public String note;

    @NonNull
    @ColumnInfo(name = "description")
    public String description;

    @NonNull
    @ColumnInfo(name = "start_date", defaultValue = "CURRENT_TIMESTAMP")
    public Long start_date;

    @ColumnInfo(name = "end_date", defaultValue = "CURRENT_TIMESTAMP")
    public Long end_date;

    @NonNull
    @ColumnInfo(name = "frequency")
    public int frequency;

    @NonNull
    @ColumnInfo(name = "complete", defaultValue = "false")
    public boolean complete;

    public Schedule() {
    }

    public Schedule(HoverAction action, Long start, Boolean isRepeat, int frequency, Long end, StaxContact contact, String a, String n, Context c) {
        this(start, Collections.singletonList(contact), a, n);
        setRepeatVals(isRepeat, frequency, end);
        type = action.transaction_type;
        channel_id = action.channel_id;
        action_id = action.public_id;
        description = generateDescription(action, Collections.singletonList(contact), c);
    }

    public Schedule(Long start, Boolean isRepeat, int frequency, Long end, List<StaxContact> contacts, String a, String n, Context c) {
        this(start, contacts, a, n);
        setRepeatVals(isRepeat, frequency, end);
        type = REQUEST_TYPE;
        description = generateDescription(null, contacts, c);
    }

    public Schedule(Long date, List<StaxContact> cs, String a, String n) {
        start_date = date == null ? DateUtils.today() : date;
        StringBuilder contacts = new StringBuilder();
        for (StaxContact c : cs) {
            if (contacts.length() > 0) contacts.append(",");
            contacts.append(c.id);
        }
        recipient_ids = contacts.toString();
        amount = a;
        note = n;
        complete = false;
    }

    private void setRepeatVals(Boolean isRepeat, int freq, Long end) {
        frequency = isRepeat ? freq : ONCE;
        end_date = isRepeat ? end : start_date;
    }

    private String generateDescription(HoverAction action, List<StaxContact> contacts, Context c) {
        switch (type) {
            case HoverAction.AIRTIME:
                return c.getString(R.string.descrip_airtime_sched, action.from_institution_name, !action.requiresRecipient() ? c.getString(R.string.self_choice) : StaxContact.shortName(contacts, c));
            case HoverAction.P2P:
                return c.getString(R.string.descrip_transfer_sent, action.from_institution_name, StaxContact.shortName(contacts, c));
            case HoverAction.ME2ME:
                return c.getString(R.string.descrip_transfer_sent, action.from_institution_name, action.to_institution_name);
            case REQUEST_TYPE:
                return c.getString(R.string.descrip_request, StaxContact.shortName(contacts, c));
            default:
                return "Other";
        }
    }

    public String humanFrequency(Context c) {
        if (frequency == ONCE)
            return DateUtils.humanFriendlyDate(start_date);
        else
            return c.getResources().getStringArray(R.array.frequency_choices)[frequency];
    }

    public String title(Context c) {
        switch (type) {
            case HoverAction.P2P:
            case HoverAction.ME2ME:
                return c.getString(R.string.notify_transfer_cta);
            case HoverAction.AIRTIME:
                return c.getString(R.string.notify_airtime_cta);
            case REQUEST_TYPE:
                return c.getString(R.string.notify_request_cta);
            default:
                return null;
        }
    }

    public String notificationMsg(Context c, List<StaxContact> contacts) {
        switch (type) {
            case HoverAction.P2P:
            case HoverAction.ME2ME:
                return c.getString(R.string.notify_transfer, description);
            case HoverAction.AIRTIME:
                return c.getString(R.string.notify_airtime);
            case REQUEST_TYPE:
                return c.getString(R.string.notify_request, StaxContact.shortName(contacts, c));
            default:
                return null;
        }
    }

    boolean isScheduledForToday() {
        switch (frequency) {
            case DAILY:
                return dateInRange();
            case WEEKLY:
                return onDayOfWeek();
            case BIWEEKLY:
                return onDayOfBiweek();
            case MONTHLY:
                return onDayOfMonth();
            default:
                return checkDateMatch(new Date(start_date));
        }
    }

    private boolean checkDateMatch(Date d) {
        Date today = new Date(DateUtils.now());
        SimpleDateFormat comparisonFormat = new SimpleDateFormat("MM dd yyyy");
        return comparisonFormat.format(d).equals(comparisonFormat.format(today));
    }

    private boolean dateInRange() {
        Date today = new Date(DateUtils.today());
        return !today.before(new Date(start_date)) && (end_date == null || !today.after(new Date(end_date)));
    }

    private boolean onDayOfWeek() {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(DateUtils.today());
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(start_date);
        return dateInRange() && today.get(Calendar.DAY_OF_WEEK) == start.get(Calendar.DAY_OF_WEEK);
    }

    private boolean onDayOfBiweek() {
        return onDayOfWeek() && isEvenWeeksSince();
    }

    private boolean isEvenWeeksSince() {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(DateUtils.today());
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(start_date);
        return (Math.abs(start.get(Calendar.WEEK_OF_YEAR) - today.get(Calendar.WEEK_OF_YEAR)) % 2) == 0;
    }

    private boolean onDayOfMonth() {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(DateUtils.today());
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(start_date);
        return dateInRange() && (start.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) ||
                (start.get(Calendar.DAY_OF_MONTH) > 28 && start.get(Calendar.DAY_OF_MONTH) > today.getActualMaximum(Calendar.DAY_OF_MONTH) && today.get(Calendar.DAY_OF_MONTH) == today.getActualMaximum(Calendar.DAY_OF_MONTH)));
    }

    @NotNull
    @Override
    public String toString() {
        return description;
    }
}
