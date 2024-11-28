package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.auth.fragment.LoginFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseHelper.getInstance().isUserLoggedIn()) {
            NavigationHelper.navigateToMain(this);
        }

        if (savedInstanceState == null) {
            NavigationHelper.loadFragment(this, new LoginFragment(), false);
        }
    }

}
