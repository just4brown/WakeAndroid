package com.lucerlabs.wake;

/**
 * Created by bbrown on 1/22/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.result.Credentials;

public class CredentialsManager {

    public static void saveCredentials(Context context, Credentials credentials){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        sharedPref.edit()
                .putString(Constants.ID_TOKEN, credentials.getIdToken())
                .putString(Constants.REFRESH_TOKEN, credentials.getRefreshToken())
                .putString(Constants.ACCESS_TOKEN, credentials.getAccessToken())
                .putString(Constants.CREDENTIAL_TYPE, credentials.getType())
                .putLong(Constants.EXPIRES_IN,credentials.getExpiresIn())
                .commit();
    }

    public static Credentials getCredentials(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        Credentials credentials = new Credentials(
                sharedPref.getString(Constants.ID_TOKEN, null),
                sharedPref.getString(Constants.ACCESS_TOKEN, null),
                sharedPref.getString(Constants.CREDENTIAL_TYPE, null),
                sharedPref.getString(Constants.REFRESH_TOKEN, null),
                sharedPref.getLong(Constants.EXPIRES_IN, 0));

        return credentials;
    }

    public static void deleteCredentials(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        sharedPref.edit()
                .putString(Constants.ID_TOKEN, null)
                .putString(Constants.REFRESH_TOKEN, null)
                .putString(Constants.ACCESS_TOKEN, null)
                .putString(Constants.CREDENTIAL_TYPE, null)
                .putLong(Constants.EXPIRES_IN, 0)
                .commit();
    }
}
