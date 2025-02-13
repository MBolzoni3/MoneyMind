package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.devtrinity.moneymind.data.repository.DatabaseRepository;
import it.unimib.devtrinity.moneymind.utils.LanguageHelper;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;

public class SettingsViewModel extends ViewModel {

    private final DatabaseRepository databaseRepository;
    private final MutableLiveData<Integer> themeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> languageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutLiveData = new MutableLiveData<>();

    public SettingsViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public LiveData<Integer> getTheme() {
        return themeLiveData;
    }

    public LiveData<String> getLanguage() {
        return languageLiveData;
    }

    public LiveData<Boolean> getLogoutLiveData() {
        return logoutLiveData;
    }

    public void initTheme(Application application) {
        if (themeLiveData.getValue() == null) {
            themeLiveData.setValue(SharedPreferencesHelper.getTheme(application));
        }
    }

    public void initLanguage(Application application) {
        if (languageLiveData.getValue() == null) {
            String lang = SharedPreferencesHelper.getLanguage(application);
            if (lang == null) {
                lang = LanguageHelper.getCurrentLanguage(application);
            }

            languageLiveData.setValue(lang);
        }
    }

    public void setTheme(Application application, int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        SharedPreferencesHelper.setTheme(application, theme);
        themeLiveData.setValue(theme);
    }

    public void setLanguage(Application application, String language) {
        LanguageHelper.setAppLocale(application, language);
        SharedPreferencesHelper.setLanguage(application, language);
        languageLiveData.setValue(language);
    }

    public void logout(Activity activity) {
        NavigationHelper.logout(activity, databaseRepository).thenRun(() -> logoutLiveData.postValue(true));
    }

}

