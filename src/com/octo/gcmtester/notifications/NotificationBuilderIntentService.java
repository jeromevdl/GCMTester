package com.octo.gcmtester.notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.google.android.gcm.GCMBroadcastReceiver;
import com.octo.gcmtester.GCMIntentService;
import com.octo.gcmtester.R;
import com.octo.gcmtester.activity.MainActivity;

/**
 * {@link IntentService} used to generate {@link Notification} in application background<br/>
 * Not necessary in this case but mandatory in case of {@link NotificationBuilder16IntentService} which can show pictures
 * 
 */
public class NotificationBuilderIntentService extends IntentService {

	public static final String INTENT_EXTRA_RECEIVER = "INTENT_EXTRA_RECEIVER";
	public static final String BUNDLE_RESULT_NOTIF = "BUNDLE_RESULT_NOTIF";
	public static final String BUNDLE_DATA_TITLE = "title";
	public static final String BUNDLE_DATA_MESSAGE = "message";

	public static final int RESULT_OK = 0;
	public static final int RESULT_ERROR = -1;

	protected ResultReceiver mResultReceiver;

	public NotificationBuilderIntentService(String name) {
		super(name);
	}

	public NotificationBuilderIntentService() {
		this("NotificationBuilderIntentService");
	}

	@Override
	protected final void onHandleIntent(Intent intent) {

		mResultReceiver = (ResultReceiver) intent.getExtras().get(INTENT_EXTRA_RECEIVER);

		buildNotification(intent);
	}

	/**
	 * Build a notification for sdk version < 16, use the compatibility {@link NotificationCompat}
	 * 
	 * @param intent
	 *            {@link Intent} received from {@link GCMBroadcastReceiver}
	 */
	protected void buildNotification(Intent intent) {
		Builder builder = new NotificationCompat.Builder(this);

		// notification default action
		Intent notifIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 50882, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);

		// common config
		builder.setAutoCancel(true);
		builder.setSmallIcon(R.drawable.notif);
		builder.setContentTitle(intent.getStringExtra(BUNDLE_DATA_TITLE));
		builder.setContentText(intent.getStringExtra(BUNDLE_DATA_MESSAGE));
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);

		// build notif and send back to GCMIntentService
		sendResult(builder.getNotification());
	}

	/**
	 * Send result (notification) back to {@link GCMIntentService} via the {@link ResultReceiver}
	 * 
	 * @param notification
	 */
	protected void sendResult(Notification notification) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(BUNDLE_RESULT_NOTIF, notification);
		mResultReceiver.send(RESULT_OK, bundle);
	}
}
