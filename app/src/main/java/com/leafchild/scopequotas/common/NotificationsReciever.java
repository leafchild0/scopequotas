package com.leafchild.scopequotas.common;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.leafchild.scopequotas.MainActivity;

import java.util.Objects;

/**
 * @author leafchild
 * @date 27/04/2017
 * @project ScopeQuotas
 */

public class NotificationsReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
			Notification n = NotificationsManager.getInstance().getSimpleNotification(context,
				MainActivity.class, "Don't forget to log you quotas for today", "Click to open the app");

			NotificationsManager.getInstance()
								.sendImmediateNotification(context, n, NotificationsManager.DAILY_NOTIF_ID);
		}
	}
}
