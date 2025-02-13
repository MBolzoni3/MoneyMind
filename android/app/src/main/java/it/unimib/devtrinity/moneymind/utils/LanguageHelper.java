package it.unimib.devtrinity.moneymind.utils;

import android.app.LocaleManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

public class LanguageHelper {

    public static void setAppLocale(Context context, String languageCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));
        } else {
            setLegacyLocale(context, languageCode);
        }
    }

    private static void setLegacyLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static String getCurrentLanguage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String languageTag = context.getSystemService(LocaleManager.class)
                    .getApplicationLocales()
                    .toLanguageTags();

            if (languageTag.isEmpty()) {
                return Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
            }

            return languageTag;
        } else {
            return Locale.getDefault().getLanguage();
        }
    }

}

