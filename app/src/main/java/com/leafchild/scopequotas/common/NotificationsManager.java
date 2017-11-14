package com.leafchild.scopequotas.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.leafchild.scopequotas.MainActivity;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.settings.SettingsActivity;

import java.util.Calendar;

/**
 * @author leafchild
 * @date 27/04/2017
 * @project ScopeQuotas
 */

public class NotificationsManager {

	private static NotificationsManager instance;
	static final int DAILY_NOTIF_ID = 1234;

	public static NotificationsManager getInstance() {

		if (instance == null) {
			instance = new NotificationsManager();
		}

		return instance;
	}

	void sendImmediateNotification(Context context, Notification n, int id) {

		getSystemNotificationManager(context).notify(id, n);
	}

	Notification getSimpleNotification(Context context, Class<?> cls, String title, String text) {

		SharedPreferences prefs = Utils.getDefaultSharedPrefs(context);
		boolean isNotifEnabled = prefs.getBoolean(SettingsActivity.DAILY_NOTIFICATIONS, true);
		boolean isVibrateEnabled = prefs.getBoolean(SettingsActivity.NOTIFICATIONS_VIBRATE, true);
		String strRingtonePreference = prefs.getString(SettingsActivity.NOTIFICATIONS_RINGTONE, "DEFAULT_SOUND");

		Intent notificationIntent = new Intent(context, cls);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(cls);
		stackBuilder.addNextIntent(notificationIntent);

		PendingIntent pendingIntent = stackBuilder.getPendingIntent(
			DAILY_NOTIF_ID, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(context)
			.setContentTitle(title)
			.setContentText(text)
			.setContentIntent(pendingIntent)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setAutoCancel(true);

		if (isNotifEnabled) {
			if (isVibrateEnabled) builder.setVibrate(new long[] {1000, 1000});
			builder.setSound(Uri.parse(strRingtonePreference));
		}

		return builder.build();
	}

	public void scheduleNotification(Context context, Class<?> cls, long delay) {

		AlarmManager alarmManager = getSystemAlarmManager(context);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(delay);

		// Enable a receiver

		ComponentName receiver = new ComponentName(context, cls);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
			PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
			PackageManager.DONT_KILL_APP);

		cancelReminder(context, cls);

		Intent intent = new Intent(context, cls);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, DAILY_NOTIF_ID, intent,
			PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
			AlarmManager.INTERVAL_DAY, pIntent);
	}

	private void cancelReminder(Context context, Class<?> cls) {

		ComponentName receiver = new ComponentName(context, cls);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
			PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
			PackageManager.DONT_KILL_APP);

		Intent intent1 = new Intent(context, cls);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_NOTIF_ID, intent1, PendingIntent
			.FLAG_UPDATE_CURRENT);
		getSystemAlarmManager(context).cancel(pendingIntent);
		pendingIntent.cancel();
	}

	private NotificationManager getSystemNotificationManager(Context context) {

		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private AlarmManager getSystemAlarmManager(Context context) {

		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

}
