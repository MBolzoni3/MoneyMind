package it.unimib.devtrinity.moneymind.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import it.unimib.devtrinity.moneymind.constant.Constants;

public class SharedPreferencesHelper {

    public static SharedPreferences getPreferences(Application application) {
        return application.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getTheme(Application application) {
        SharedPreferences prefs = getPreferences(application);
        return prefs.getInt(Constants.THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void setTheme(Application application, int theme) {
        SharedPreferences prefs = getPreferences(application);
        prefs.edit().putInt(Constants.THEME_KEY, theme).apply();
    }

    public static void clearSharedPrefs(Application application) {
        SharedPreferences prefs = getPreferences(application);
        prefs.edit().clear().apply();
    }

}
