package com.lucerlabs.wake;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.particle.android.sdk.devicesetup.SetupCompleteIntentBuilder;
import io.particle.android.sdk.devicesetup.SetupResult;

public class WakeParticleSetupCompleteIntentBuilder implements SetupCompleteIntentBuilder  {

	public WakeParticleSetupCompleteIntentBuilder() {
	}

	@Override
	public Intent buildIntent(Context ctx, SetupResult result) {
		if (!result.wasSuccessful()) {
			Log.e("ERROR", " Particle setup failed!");
		}

		Intent intent = new Intent(ctx, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra("configuredDeviceId", result.getConfiguredDeviceId());
		return intent;
	}
}
