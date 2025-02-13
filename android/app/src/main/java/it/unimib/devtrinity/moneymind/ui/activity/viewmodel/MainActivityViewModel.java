package it.unimib.devtrinity.moneymind.ui.activity.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.devtrinity.moneymind.utils.WorkerHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class MainActivityViewModel extends ViewModel {

    private final MutableLiveData<Integer> userState = new MutableLiveData<>(-1);

    public MutableLiveData<Integer> getUserState() {
        return userState;
    }

    public void checkUserState(Context context) {
        if (FirebaseHelper.getInstance().isUserLoggedIn()) {
            WorkerHelper.triggerManualSync(context)
                    .thenRun(() -> {
                        WorkerHelper.triggerManualRecurring(context)
                                .thenRun(() -> userState.postValue(1));
                    });
        } else {
            userState.postValue(0);
        }
    }

}

