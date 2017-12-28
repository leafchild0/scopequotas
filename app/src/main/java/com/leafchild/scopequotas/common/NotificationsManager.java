package com.leafchild.scopequotas.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.settings.SettingsActivity;

/**
 * @author leafchild
 * @date 27/04/2017
 * @project ScopeQuotas
 */

public class NotificationsManager {

	private static NotificationsManager instance;
	public static final int DAILY_NOTIF_ID = 96764556;
	public static final int WEEKLY_NOTIF_ID = 1234567906;

	public static NotificationsManager getInstance() {

		if (instance == null) {
			instance = new NotificationsManager();
		}

		return instance;
	}

	void sendImmediateNotification(Context context, Notification n, int id) {

		getSystemNotificationManager(context).notify(id, n);
	}

	Notification getSimpleNotification(Context context, PendingIntent pendingIntent, String title, String text) {

		SharedPreferences prefs = Utils.getDefaultSharedPrefs(context);
		boolean isNotifEnabled = prefs.getBoolean(SettingsActivity.DAILY_NOTIFICATIONS, true);
		boolean isVibrateEnabled = prefs.getBoolean(SettingsActivity.NOTIFICATIONS_VIBRATE, true);
		String strRingtonePreference = prefs.getString(SettingsActivity.NOTIFICATIONS_RINGTONE, "DEFAULT_SOUND");

		Notification.Builder builder = new Notification.Builder(context)
			.setContentTitle(title)
			.setContentText(text)
			.setContentIntent(pendingIntent)
			.setSmallIcon(R.drawable.ic_add_worklog)
			.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_foreground))
			.setAutoCancel(true);

		if (isNotifEnabled) {
			if (isVibrateEnabled) builder.setVibrate(new long[] {1000, 1000, 1000});
			builder.setSound(Uri.parse(strRingtonePreference));
		}

		return builder.build();
	}

	public void scheduleDailyReminder(Context context, long when, int notificationId) {

		scheduleReminderInternal(context, when, notificationId, AlarmManager.INTERVAL_DAY);
	}

	public void scheduleWeeklyReminder(Context context, long when, int notificationId) {

		scheduleReminderInternal(context, when, notificationId, AlarmManager.INTERVAL_DAY * 7);
	}

	private void scheduleReminderInternal(Context context, long when, int notificationId, long interval) {

		cancelReminder(context, notificationId);
		Intent intent = new Intent(context, NotificationsReciever.class);
		intent.putExtra("notificationId", notificationId);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, notificationId, intent,
			PendingIntent.FLAG_UPDATE_CURRENT);
		getSystemAlarmManager(context).setRepeating(AlarmManager.RTC_WAKEUP, when, interval, pIntent);
	}

	public void cancelReminder(Context context, int notificationId) {

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
			new Intent(context, NotificationsReciever.class), PendingIntent.FLAG_UPDATE_CURRENT);
		
		if (pendingIntent != null) {
			getSystemAlarmManager(context).cancel(pendingIntent);
			pendingIntent.cancel();
		}
	}

	private NotificationManager getSystemNotificationManager(Context context) {

		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private AlarmManager getSystemAlarmManager(Context context) {

		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

}
