package com.octo.gcmtester.activity;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.Sender;
import com.octo.gcmtester.R;

public class MainActivity extends Activity {

	private EditText mTitleEditText;
	private EditText mMessageEditText;
	private EditText mImageUrlEditText;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		initializeScreen();

		initializeGCMToReceiveNotifications();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		GCMRegistrar.onDestroy(getApplicationContext());
	}

	private void initializeGCMToReceiveNotifications() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, getString(R.string.gcm_project_id));
		}
		else {
			Log.d(getString(R.string.app_name), "Already registered with " + regId);
		}
	}

	public void onSendNotificationClick(View v) {
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			Toast.makeText(this, "Not registered", Toast.LENGTH_LONG).show();
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				Sender sender = new Sender(getString(R.string.gcm_auth_key));
				Builder builder = new Message.Builder();
				builder.addData("title", mTitleEditText.getText().toString());
				builder.addData("message", mMessageEditText.getText().toString());
				String imageUrl = mImageUrlEditText.getText().toString();
				if (StringUtils.isNotEmpty(imageUrl)) {
					builder.addData("image_url", imageUrl);
				}
				try {
					sender.send(builder.build(), regId, 3);
				}
				catch (IOException e) {
					Log.e("GCM", "Unable to send message", e);
				}
			}
		}).start();
	}

	private void initializeScreen() {
		mTitleEditText = (EditText) findViewById(R.id.edittext_title);
		mMessageEditText = (EditText) findViewById(R.id.edittext_message);
		mImageUrlEditText = (EditText) findViewById(R.id.edittext_image_url);

		if (Build.VERSION.SDK_INT < 16) {
			mImageUrlEditText.setEnabled(false);
		}
		else {
			mImageUrlEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus && StringUtils.isEmpty(mImageUrlEditText.getText().toString())) {
						mImageUrlEditText.setText("http://");
					}
				}
			});
		}
	};
}
