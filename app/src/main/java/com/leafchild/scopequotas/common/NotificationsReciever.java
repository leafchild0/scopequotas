package com.leafchild.scopequotas.common;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.leafchild.scopequotas.AppContants;
import com.leafchild.scopequotas.activity.MainActivity;
import com.leafchild.scopequotas.data.QuotaType;
import com.leafchild.scopequotas.activity.ReportsActivity;

import static com.leafchild.scopequotas.common.NotificationsManager.DAILY_NOTIF_ID;

/**
 * @author leafchild
 * @date 27/04/2017
 * @project ScopeQuotas
 */

public class NotificationsReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int notificationId = intent.getIntExtra("notificationId", 0);
		Notification n = null;

		if (notificationId == DAILY_NOTIF_ID) {

			Intent notificationIntent = new Intent(context, MainActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			n = NotificationsManager.getInstance().getSimpleNotification(context,
				getPendingIntent(context, notificationIntent, notificationId, MainActivity.class),
				"Review you weekly logged quotas report", "Click to open the app");

		}
		else if (notificationId == NotificationsManager.WEEKLY_NOTIF_ID) {

			Intent notificationIntent = new Intent(context, ReportsActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// By default show logged for Week, then you can switch to Day or month
			notificationIntent.putExtra(AppContants.TYPE, QuotaType.WEEKLY);

			n = NotificationsManager.getInstance().getSimpleNotification(context,
				getPendingIntent(context, notificationIntent, notificationId, ReportsActivity.class),
				"Don't forget to log you quotas for today", "Click to open the app");
		}
		else {
			Log.e("Notification Reciever", "Passed notification id is unknown");
		}

		if (n != null)
			NotificationsManager.getInstance().sendImmediateNotification(context, n, notificationId);

	}

	private PendingIntent getPendingIntent(Context context, Intent notificationIntent, int notificationId, Class cls) {

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(cls);
		stackBuilder.addNextIntent(notificationIntent);

		return stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
