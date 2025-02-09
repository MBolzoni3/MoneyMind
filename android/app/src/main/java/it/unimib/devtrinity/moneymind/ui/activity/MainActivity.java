package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.auth.fragment.LoginFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.SyncHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class MainActivity extends AppCompatActivity {

    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(SharedPreferencesHelper.getTheme(this));

        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.loading_indicator);

        FirebaseApp.initializeApp(this);
        SyncHelper.scheduleSyncJob(this);

        new Handler().postDelayed(() -> {
            if (FirebaseHelper.getInstance().isUserLoggedIn()) {
                SyncHelper.triggerManualSyncAndNavigate(this, () -> {
                    NavigationHelper.navigateToMain(this);
                });
            } else {
                loadingIndicator.setVisibility(View.GONE);
                if (savedInstanceState == null) {
                    NavigationHelper.loadFragment(this, new LoginFragment());
                }
            }
        }, 1000);

    }

}
