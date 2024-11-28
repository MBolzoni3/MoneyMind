package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.main.fragment.HomeFragment;
import it.unimib.devtrinity.moneymind.utils.FirebaseHelper;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainNavigationActivity extends AppCompatActivity {

    private static final String TAG = "MainNavigationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        setSupportActionBar(topAppBar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, topAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                NavigationHelper.loadFragment(this, new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_movements) {
                // loadFragment(new MovementsFragment());
                //return true;
            } else if (itemId == R.id.nav_budget) {
                // loadFragment(new BudgetFragment());
                //return true;
            } else if (itemId == R.id.nav_charts) {
                // loadFragment(new ChartsFragment());
                //return true;
            }

            return false;
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.action_logout) {
                FirebaseHelper.getInstance().logoutUser();
                NavigationHelper.navigateToLogin(this);
            }

            return false;
        });

        View headerView = navigationView.getHeaderView(0);
        TextView drawerUsername = headerView.findViewById(R.id.drawer_username);
        drawerUsername.setText(FirebaseHelper.getInstance().getCurrentUser().getDisplayName());

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
}
