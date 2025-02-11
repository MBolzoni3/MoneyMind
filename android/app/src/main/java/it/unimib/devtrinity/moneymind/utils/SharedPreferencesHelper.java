package it.unimib.devtrinity.moneymind.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import it.unimib.devtrinity.moneymind.constant.Constants;

public class SharedPreferencesHelper {

    private static final String[] keysToRemove = {
            Constants.BUDGETS_LAST_SYNC_KEY,
            Constants.GOALS_LAST_SYNC_KEY,
            Constants.TRANSACTIONS_LAST_SYNC_KEY,
            Constants.RECURRING_TRANSACTIONS_LAST_SYNC_KEY,
            Constants.CATEGORIES_LAST_SYNC_KEY
    };

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
        SharedPreferences.Editor editor = prefs.edit();

        for (String key : keysToRemove) {
            editor.remove(key);
        }

        editor.apply();
    }

}
