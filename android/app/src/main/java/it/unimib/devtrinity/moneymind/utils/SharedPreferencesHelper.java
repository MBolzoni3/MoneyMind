package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;
import android.content.SharedPreferences;

import it.unimib.devtrinity.moneymind.constant.Constants;

public class SharedPreferencesHelper {

    public static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

}
