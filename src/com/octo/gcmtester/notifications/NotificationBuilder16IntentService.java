package com.octo.gcmtester.notifications;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpConnection;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;
import com.octo.gcmtester.R;
import com.octo.gcmtester.activity.MainActivity;

/**
 * {@link IntentService} used to generate {@link Notification} in application background<br/>
 * This is necessary to download image in background and generate a {@link BigPictureStyle} for the {@link Notification}
 */
@TargetApi(16)
public class NotificationBuilder16IntentService extends NotificationBuilderIntentService {

	public static final String BUNDLE_DATA_IMAGE_URL = "image_url";

	private Builder builder16;

	public NotificationBuilder16IntentService() {
		super("NotificationBuilder16IntentService");
	}

	/**
	 * Build a notification for sdk version >= 16, use the new {@link Notification.Builder} API
	 * 
	 * @param intent
	 *            {@link Intent} received from {@link GCMBroadcastReceiver}
	 */
	@Override
	protected void buildNotification(Intent intent) {
		String title = intent.getStringExtra(BUNDLE_DATA_TITLE);
		String message = intent.getStringExtra(BUNDLE_DATA_MESSAGE);

		builder16 = new Notification.Builder(this);

		// notification default action
		Intent notifIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 50882, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder16.setContentIntent(contentIntent);

		// common config
		builder16.setAutoCancel(true);
		builder16.setSmallIcon(R.drawable.notif);
		builder16.setContentTitle(title);
		builder16.setContentText(message);
		builder16.setWhen(System.currentTimeMillis());
		builder16.setAutoCancel(true);

		// ----------------
		// specific sdk 16
		// ----------------
		builder16.setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());

		// big picture style
		String imageUrl = null;
		if ((imageUrl = intent.getStringExtra(BUNDLE_DATA_IMAGE_URL)) != null) {
			InputStream stream = downloadImage(imageUrl);
			if (stream != null) {
				Bitmap bm = BitmapFactory.decodeStream(stream);
				builder16.setStyle(new BigPictureStyle().bigPicture(bm));
			}
		}

		// big text style
		else if (message.length() > 15) {
			builder16.setStyle(new BigTextStyle().bigText(message));
		}

		// actions with PendingIntent (max 3)
		// example : share action
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
		String share = getString(R.string.share);
		PendingIntent sharePendingIntent = PendingIntent.getActivity(this, 21245, Intent.createChooser(sharingIntent, share), PendingIntent.FLAG_UPDATE_CURRENT);
		builder16.addAction(android.R.drawable.ic_menu_share, share, sharePendingIntent);

		// build notif and send back to GCMIntentService
		sendResult(builder16.build());
	}

	/**
	 * Simple method to download an image with {@link HttpConnection}
	 * 
	 * @param imageUrl
	 *            Url of the image to download
	 * @return an {@link InputStream} of the image
	 */
	private InputStream downloadImage(String imageUrl) {
		InputStream is = null;

		URL url;
		try {
			url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			is = conn.getInputStream();
		}
		catch (Exception e) {
			Log.e(getString(R.string.app_name), "Unable to download image");
		}

		return is;
	}
}
