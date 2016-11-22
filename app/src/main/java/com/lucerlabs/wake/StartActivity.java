package com.lucerlabs.wake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent lockIntent = new Intent(this, LoginActivity.class);
		startActivity(lockIntent);
	}
}
