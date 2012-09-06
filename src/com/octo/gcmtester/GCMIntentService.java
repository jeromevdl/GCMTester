package com.octo.gcmtester;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.octo.gcmtester.notifications.NotificationBuilder16IntentService;
import com.octo.gcmtester.notifications.NotificationBuilderIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private Handler mHandler = new Handler();
	ServiceResultReceiver mResultReceiver = new ServiceResultReceiver(mHandler);

	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	protected void onError(Context context, String errorId) {
		Log.e(getString(R.string.app_name), "Unrecoverable error " + errorId);
		// ERROR_SERVICE_NOT_AVAILABLE is managed by parent class with a retry, no need to manage here
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		Intent notifIntent;

		if (Build.VERSION.SDK_INT >= 16) {
			notifIntent = new Intent(this, NotificationBuilder16IntentService.class);
		}
		else {
			notifIntent = new Intent(this, NotificationBuilderIntentService.class);
		}

		notifIntent.putExtra(NotificationBuilder16IntentService.INTENT_EXTRA_RECEIVER, mResultReceiver);
		notifIntent.putExtras(intent.getExtras());

		// launch an IntentService to prepare the Notification (specially for sdk >= 16 when an image must be download)
		startService(notifIntent);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(getString(R.string.app_name), "Register id " + registrationId + " on your server");
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(getString(R.string.app_name), "Unregister id " + registrationId + " on your server");
	}

	/**
	 * {@link ResultReceiver} used to receive {@link Notification} built by {@link NotificationBuilderIntentService} or {@link NotificationBuilder16IntentService} and to notify the user with
	 * {@link NotificationManager}
	 */
	protected class ServiceResultReceiver extends ResultReceiver {
		ServiceResultReceiver(final Handler handler) {
			super(handler);
		}

		@Override
		public void onReceiveResult(final int resultCode, final Bundle resultData) {
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			Notification notification = (Notification) resultData.getParcelable(NotificationBuilderIntentService.BUNDLE_RESULT_NOTIF);
			if (notification != null) {
				notificationManager.notify(10498, notification);
			}
			else {
				Log.e(getString(R.string.app_name), "Unable to build and send notification");
			}
		}
	}
}