package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executors;

import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<Integer> themeLiveData = new MutableLiveData<>();

    public LiveData<Integer> getTheme() {
        return themeLiveData;
    }

    public void initTheme(Context context) {
        if (themeLiveData.getValue() == null) {
            themeLiveData.setValue(SharedPreferencesHelper.getTheme(context));
        }
    }

    public void setTheme(Context context, int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        SharedPreferencesHelper.setTheme(context, theme);
        themeLiveData.setValue(theme);
    }

    public void logout(Context context) {
        FirebaseHelper.getInstance().logoutUser();
        SharedPreferencesHelper.clearSharedPrefs(context);
        Executors.newSingleThreadExecutor().execute(() -> {
            DatabaseClient.getInstance(context).clearAllTables();
        });
        NavigationHelper.navigateToLogin(context);
    }
}

