package com.pockru.dongzakgol.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preference {

	private static final String PREF_NAME = "bestiz";

	private static final String KEY_TUMBLR_TOKEN = "tumblr_token";
	private static final String KEY_TUMBLR_SECRET = "tumblr_secret";

	private static String getString(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.getString(key, "");
	}

	private static void setString(Context context, String key, String value) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.apply();
	}
	
	private static int getInt(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.getInt(key, 0);
	}

	private static void setInt(Context context, String key, int value) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.apply();
	}

	private static boolean getBooean(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.getBoolean(key, false);
	}

	private static void setBoolean(Context context, String key, boolean value) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public static String getTumblrToken(Context context) {
		return getString(context, KEY_TUMBLR_TOKEN);
	}

	public static String getTumblrSecret(Context context) {
		return getString(context, KEY_TUMBLR_SECRET);
	}

	public static void setTumblrToken(Context context, String token) {
		setString(context, KEY_TUMBLR_TOKEN, token);
	}

	public static void setTumblrSecret(Context context, String secret) {
		setString(context, KEY_TUMBLR_SECRET, secret);
	}
}
