package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.activity.viewmodel.MainActivityViewModel;
import it.unimib.devtrinity.moneymind.ui.auth.fragment.LoginFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.SyncHelper;

public class MainActivity extends AppCompatActivity {

    private ProgressBar loadingIndicator;
    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(SharedPreferencesHelper.getTheme(getApplication()));

        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.loading_indicator);
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        FirebaseApp.initializeApp(this);
        SyncHelper.scheduleSyncJob(this);

        mainActivityViewModel.getNavigateToMain().observe(this, navigate -> {
            if (navigate) {
                NavigationHelper.navigateToMain(this);
            }
        });

        mainActivityViewModel.getShowLogin().observe(this, showLogin -> {
            if (showLogin && savedInstanceState == null) {
                loadingIndicator.setVisibility(View.GONE);
                NavigationHelper.loadFragment(this, new LoginFragment(), false);
            }
        });

        mainActivityViewModel.checkUserState(this);
    }

}
