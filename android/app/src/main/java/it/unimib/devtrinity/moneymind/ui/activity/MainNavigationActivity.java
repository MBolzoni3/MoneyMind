package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.fragment.BudgetFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.GoalFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.HomeFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.TransactionFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class MainNavigationActivity extends AppCompatActivity implements SelectionModeListener {

    private final HomeFragment homeFragment = new HomeFragment();
    private final BudgetFragment budgetFragment = new BudgetFragment();
    private final GoalFragment goalFragment = new GoalFragment();
    private final TransactionFragment transactionFragment = new TransactionFragment();

    private Fragment currentFragment;

    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        topAppBar = findViewById(R.id.top_app_bar);
        setSupportActionBar(topAppBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = homeFragment;
            } else if (itemId == R.id.nav_budget) {
                selectedFragment = budgetFragment;
            } else if (itemId == R.id.nav_goals) {
                selectedFragment = goalFragment;
            } else if (itemId == R.id.nav_more) {
            }

            if (selectedFragment != null) {
                switchFragment(selectedFragment);
                return true;
            }
            return false;
        });

        if(savedInstanceState == null) {
            NavigationHelper.addFragments(this, List.of(
                    homeFragment,
                    transactionFragment,
                    budgetFragment,
                    goalFragment
            ));

            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
    }

    private void switchFragment(Fragment fragment) {
        if (fragment != currentFragment) {
            NavigationHelper.showFragment(this, fragment);
            currentFragment = fragment;
            updateBottomNavigation(fragment);
        }
    }

    public void showTransactionFragment() {
        switchFragment(transactionFragment);
    }

    public void showHomeFragment() {
        switchFragment(homeFragment);
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
                Fragment activeFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                if (activeFragment != null) {
                    updateBottomNavigation(activeFragment);
                    currentFragment = activeFragment;
                }
            } else {
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
                setEnabled(true);
            }
        }
    };

    private void updateBottomNavigation(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (fragment instanceof BudgetFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_budget);
        } else if (fragment instanceof GoalFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_goals);
        } /*else if (fragment instanceof GoalFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_more);
        }*/ else {
            bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
            for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                bottomNavigationView.getMenu().getItem(i).setChecked(false);
            }
            bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        }
    }

    @Override
    public void onEnterSelectionMode() {
        topAppBar.getMenu().clear();
        topAppBar.inflateMenu(R.menu.selection_menu);
        topAppBar.setNavigationIcon(R.drawable.ic_close);
        topAppBar.setNavigationOnClickListener(v -> {
            if(currentFragment instanceof BudgetFragment){
                budgetFragment.onExitSelectionMode();
            } else if(currentFragment instanceof GoalFragment){
                goalFragment.onExitSelectionMode();
            }
        });

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                if(currentFragment instanceof BudgetFragment){
                    budgetFragment.deleteSelected();
                } else if(currentFragment instanceof GoalFragment){
                    goalFragment.deleteSelected();
                }

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
