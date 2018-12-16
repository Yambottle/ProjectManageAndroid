package com.example.projectmanagesec.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.example.projectmanagesec.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		};
		timer.schedule(task, 3*1000);
	}
}
