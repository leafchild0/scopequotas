package com.leafchild.scopequotas.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.settings.SettingsActivity;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leafchild
 * @date 27/04/2017
 * @project ScopeQuotas
 */

public class NotificationsManager {

    private static NotificationsManager instance;
    private static AlarmManager alarmManager;
    private static SharedPreferences prefs;
    private final static AtomicInteger N_COUNT = new AtomicInteger(0);

    public static NotificationsManager getInstance() {
        if(instance == null) {
            instance = new NotificationsManager();
        }

        return instance;
    }

    void sendImmediateNotification(Context context, Notification n, int id) {
        getSystemNotificationManager(context).notify(id, n);
    }

    Notification getSimpleNotification(Context context, PendingIntent pendingIntent, String title, String text) {

        prefs = Utils.getDefaultSharedPrefs(context);
        boolean isNotifEnabled = prefs.getBoolean(SettingsActivity.DAILY_NOTIFICATIONS, true);
        boolean isVibrateEnabled = prefs.getBoolean(SettingsActivity.NOTIFICATIONS_VIBRATE, true);
        String strRingtonePreference = prefs.getString(SettingsActivity.NOTIFICATIONS_RINGTONE, "DEFAULT_SOUND");

        Notification.Builder builder = new Notification.Builder(context)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true);

        if(isNotifEnabled) {
            if(isVibrateEnabled) builder.setVibrate(new long[]{1000, 1000});
            builder.setSound(Uri.parse(strRingtonePreference));
        }

        return builder.build();
    }

    public void scheduleNotification(Context context, long delay) {

        alarmManager = getSystemAlarmManager(context);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(delay);

        Intent intent = new Intent(context, NotificationsReciever.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY, pIntent);
    }

    private NotificationManager getSystemNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private AlarmManager getSystemAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    static int getNotificationId() {
        return N_COUNT.incrementAndGet();
    }

}
