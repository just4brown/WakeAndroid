package com.lucerlabs.wake;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

import java.util.Set;

public class WakeNotificationHandler extends NotificationsHandler {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	Context ctx;

	@Override
	public void onReceive(Context context, Bundle bundle) {
		ctx = context;
		if (bundle != null) {
			String message = bundle.getString("body");
			String from = bundle.getString("from");
			String category = bundle.getString("category");
			String sound = bundle.getString("sound");
			String title = bundle.getString("title");
			String time = bundle.getString("google.sent_time");
			Log.e("Notification received ", from + " " + category + " " + sound + " " + time + " " + title);
			sendNotification(ctx, message, title, category);

			Intent intent = new Intent("alarmStatus");
			intent.putExtra("category", category);
			ctx.sendBroadcast(intent);
		}
	}

	private void sendNotification(Context ctx, String msg, String title, String category) {

		Intent intent = new Intent(ctx, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// TODO: only show dismiss button for "alarm going off" notification

		Intent dismissIntent = new Intent(ctx, MainActivity.class);
		dismissIntent.putExtra("ALARM", "dismiss");

		PendingIntent dismissActionIntent = PendingIntent.getActivity(ctx, 200, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		mNotificationManager = (NotificationManager)
				ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(ctx)
						.setAutoCancel(true)
						.setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle(title)
						.setStyle(new NotificationCompat.BigTextStyle()
								.bigText(msg))
						.setSound(defaultSoundUri)
						.setContentText(msg)
						.addAction(R.drawable.ic_alarm_off_black_24dp, "Dismiss", dismissActionIntent);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
