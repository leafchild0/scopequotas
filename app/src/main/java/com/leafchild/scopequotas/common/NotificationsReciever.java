package com.leafchild.scopequotas.common;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.leafchild.scopequotas.MainActivity;

/**
 * @author leafchild
 * @date 27/04/2017
 * @project ScopeQuotas
 */

public class NotificationsReciever extends BroadcastReceiver {

    public NotificationsReciever(){}

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = NotificationsManager.getInstance().getSimpleNotification(context, pendingIntent,
            "Don't forget to log you quotas for today", "Click to open the app");

        NotificationsManager.getInstance().sendImmediateNotification(context, n, NotificationsManager.getNotificationId());
    }
}
