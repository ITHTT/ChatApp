package com.tysci.chatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {
	
	private static SharedPreferences preferences;
	private  Context mContext;
	private static final String PREFS_NAME = "com.funsine";
	public SharedPreferencesUtils(Context context) {
		this.mContext = context;
	}

	public static void setStringByKey(Context context, String key, String value) {
		preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getStringByKey(Context context, String key) {
		preferences = context
				.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getString(key, null);
	}

	public static boolean getBooleanByKey(Context context, String key) {
		preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, false);
	}

	public static void setBooleanByKey(Context context, String key, boolean value) {
		preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static void setIntegerByKey(Context context,String key,Integer value){
		preferences=context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static int getIntegerByKey(Context context,String key){
		preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getInt(key, 0);
	}

	public  String getStringByKey(String key)
	{
		preferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getString(key, null);
	}

}