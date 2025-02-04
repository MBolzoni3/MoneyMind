package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import it.unimib.devtrinity.moneymind.constant.Constants;

public class SharedPreferencesHelper {

    public static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getTheme(Context context) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getInt(Constants.THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void setTheme(Context context, int theme) {
        SharedPreferences prefs = getPreferences(context);
        prefs.edit().putInt(Constants.THEME_KEY, theme).apply();
    }

    public static void clearSharedPrefs(Context context) {
        SharedPreferences prefs = getPreferences(context);
        prefs.edit().clear().apply();
    }

}
