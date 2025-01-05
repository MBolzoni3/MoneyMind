package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.main.fragment.BudgetFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.HomeFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;

public class MainNavigationActivity extends AppCompatActivity {

    private HomeFragment homeFragment = new HomeFragment();
    private BudgetFragment budgetFragment = new BudgetFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        setSupportActionBar(topAppBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        NavigationHelper.addFragments(this, List.of(homeFragment, budgetFragment));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                NavigationHelper.showFragment(this, homeFragment);
                return true;
            } else if (itemId == R.id.nav_budget) {
                NavigationHelper.showFragment(this, budgetFragment);
                return true;
            } else if (itemId == R.id.nav_goals) {
                // loadFragment(new ChartsFragment());
                //return true;
            } else if (itemId == R.id.nav_more) {
                //return true;
            }

            return false;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
}
