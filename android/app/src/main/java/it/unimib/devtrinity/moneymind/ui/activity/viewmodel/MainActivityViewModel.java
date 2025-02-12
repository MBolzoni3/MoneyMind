package it.unimib.devtrinity.moneymind.ui.activity.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.devtrinity.moneymind.utils.WorkerHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class MainActivityViewModel extends ViewModel {
    private final MutableLiveData<Boolean> navigateToMain = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showLogin = new MutableLiveData<>();

    public LiveData<Boolean> getNavigateToMain() {
        return navigateToMain;
    }

    public LiveData<Boolean> getShowLogin() {
        return showLogin;
    }

    public void checkUserState(Context context) {
        if (FirebaseHelper.getInstance().isUserLoggedIn()) {
            WorkerHelper.triggerManualSync(context)
                    .thenRun(() -> {
                        WorkerHelper.triggerManualRecurring(context)
                                .thenRun(() -> navigateToMain.postValue(true));
                    });
        } else {
            showLogin.postValue(true);
        }
    }
}

