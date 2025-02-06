package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(SharedPreferencesHelper.getTheme(this));

        FirebaseApp.initializeApp(this);
        SyncHelper.scheduleSyncJob(this);

        setContentView(R.layout.activity_main);

        if (FirebaseHelper.getInstance().isUserLoggedIn()) {
            NavigationHelper.navigateToMain(this);
        }

        if (savedInstanceState == null) {
            NavigationHelper.loadFragment(this, new LoginFragment());
        }
    }

}
