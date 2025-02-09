package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import android.app.Activity;
import android.app.Application;
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

    public void initTheme(Application application) {
        if (themeLiveData.getValue() == null) {
            themeLiveData.setValue(SharedPreferencesHelper.getTheme(application));
        }
    }

    public void setTheme(Application application, int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        SharedPreferencesHelper.setTheme(application, theme);
        themeLiveData.setValue(theme);
    }

    public void logout(Activity activity) {
        FirebaseHelper.getInstance().logoutUser();
        SharedPreferencesHelper.clearSharedPrefs(activity.getApplication());
        Executors.newSingleThreadExecutor().execute(() -> {
            DatabaseClient.getInstance(activity.getApplication()).clearAllTables();
        });
        NavigationHelper.navigateToLogin(activity);
    }
}

