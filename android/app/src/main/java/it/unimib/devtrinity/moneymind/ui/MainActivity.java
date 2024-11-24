package it.unimib.devtrinity.moneymind.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.fragment.login.LoginFragment;
import it.unimib.devtrinity.moneymind.utils.FirebaseHelper;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseHelper.getInstance().isUserLoggedIn()){
            NavigationHelper.navigateToMain(this);
        }

        if (savedInstanceState == null) {
            NavigationHelper.loadFragment(this, new LoginFragment(), false);
        }
    }

}
