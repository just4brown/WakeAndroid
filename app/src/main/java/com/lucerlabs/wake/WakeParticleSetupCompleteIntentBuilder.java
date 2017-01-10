package com.lucerlabs.wake;

import android.content.Context;
import android.content.Intent;

import io.particle.android.sdk.devicesetup.SetupCompleteIntentBuilder;
import io.particle.android.sdk.devicesetup.SetupResult;

public class WakeParticleSetupCompleteIntentBuilder implements SetupCompleteIntentBuilder  {

	public WakeParticleSetupCompleteIntentBuilder() {
	}

	@Override
	public Intent buildIntent(Context ctx, SetupResult result) {
		Intent intent = new Intent(ctx, MainActivity.class);

		return intent;
	}
}
