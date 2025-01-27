package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.fragment.BudgetFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.HomeFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class MainNavigationActivity extends AppCompatActivity implements SelectionModeListener {

    private final HomeFragment homeFragment = new HomeFragment();
    private final BudgetFragment budgetFragment = new BudgetFragment();

    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        topAppBar = findViewById(R.id.top_app_bar);
        setSupportActionBar(topAppBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        NavigationHelper.addFragments(this, List.of(homeFragment, budgetFragment));

        bottomNavigationView = findViewById(R.id.bottom_navigation);
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

    @Override
    public void onEnterSelectionMode() {
        topAppBar.getMenu().clear();
        topAppBar.inflateMenu(R.menu.selection_menu);
        topAppBar.setNavigationIcon(R.drawable.ic_close);
        topAppBar.setNavigationOnClickListener(v -> budgetFragment.onExitSelectionMode()); //TODO change this to current fragment

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                budgetFragment.deleteSelected();
                return true;
            }

            return false;
        });

        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onExitSelectionMode() {
        topAppBar.getMenu().clear();
        topAppBar.setTitle(R.string.app_name);
        topAppBar.setNavigationIcon(null);
        topAppBar.setNavigationOnClickListener(v -> {});

        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSelectionCountChanged(int count) {
        String title = count + " elemento" + (count > 1 ? "i" : "") + " selezionato" + (count > 1 ? "i" : "");
        topAppBar.setTitle(title);

        topAppBar.setTitle(count + (count == 1 ? " elemento selezionato" : " elementi selezionati"));
    }
}
