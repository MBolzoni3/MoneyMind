package it.unimib.devtrinity.moneymind.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.fragment.BudgetFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.ExchangeFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.GoalFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.HomeFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.SettingsFragment;
import it.unimib.devtrinity.moneymind.ui.main.fragment.TransactionFragment;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;

public class MainNavigationActivity extends AppCompatActivity implements SelectionModeListener {

    private HomeFragment homeFragment;
    private BudgetFragment budgetFragment;
    private GoalFragment goalFragment;
    private TransactionFragment transactionFragment;
    private SettingsFragment settingsFragment;
    private ExchangeFragment exchangeFragment;

    private Fragment currentFragment;
    private Fragment previousFragment;

    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        topAppBar = findViewById(R.id.top_app_bar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        setupBottomNavigation();
        setTopAppBarMainMenu();

        if (savedInstanceState == null) {
            initializeFragments();
        } else {
            homeFragment = (HomeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "homeFragment");
            transactionFragment = (TransactionFragment) getSupportFragmentManager().getFragment(savedInstanceState, "transactionFragment");
            budgetFragment = (BudgetFragment) getSupportFragmentManager().getFragment(savedInstanceState, "budgetFragment");
            goalFragment = (GoalFragment) getSupportFragmentManager().getFragment(savedInstanceState, "goalFragment");
            settingsFragment = (SettingsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "settingsFragment");
            exchangeFragment = (ExchangeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "exchangeFragment");

            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
            previousFragment = getSupportFragmentManager().getFragment(savedInstanceState, "previousFragment");

            String currentFragmentTag = savedInstanceState.getString("currentFragmentTag");
            if (currentFragmentTag != null) {
                currentFragment = getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
            }

            if (currentFragment == null) {
                currentFragment = homeFragment;
            }
        }

        if (currentFragment != null) {
            NavigationHelper.showFragment(this, currentFragment);
        }

        if (currentFragment instanceof SettingsFragment) {
            setNavigationBackButton();
            setBottomNavigationVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "homeFragment", homeFragment);
        getSupportFragmentManager().putFragment(outState, "transactionFragment", transactionFragment);
        getSupportFragmentManager().putFragment(outState, "budgetFragment", budgetFragment);
        getSupportFragmentManager().putFragment(outState, "goalFragment", goalFragment);
        getSupportFragmentManager().putFragment(outState, "settingsFragment", settingsFragment);
        getSupportFragmentManager().putFragment(outState, "exchangeFragment", exchangeFragment);

        if (currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
            outState.putString("currentFragmentTag", currentFragment.getClass().getName());
        }

        if (previousFragment != null) {
            getSupportFragmentManager().putFragment(outState, "previousFragment", previousFragment);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentFromMenuId(item.getItemId());
            if (selectedFragment != null) {
                switchFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void initializeFragments() {
        homeFragment = new HomeFragment();
        transactionFragment = new TransactionFragment();
        budgetFragment = new BudgetFragment();
        goalFragment = new GoalFragment();
        settingsFragment = new SettingsFragment();
        exchangeFragment = new ExchangeFragment();

        NavigationHelper.addFragments(this, List.of(
                homeFragment,
                transactionFragment,
                budgetFragment,
                goalFragment,
                settingsFragment,
                exchangeFragment
        ));
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private Fragment getFragmentFromMenuId(int itemId) {
        if (itemId == R.id.nav_home) {
            return homeFragment;
        } else if (itemId == R.id.nav_movements) {
            return transactionFragment;
        } else if (itemId == R.id.nav_budget) {
            return budgetFragment;
        } else if (itemId == R.id.nav_goals) {
            return goalFragment;
        } else {
            return null;
        }
    }

    private void switchFragment(Fragment fragment) {
        if (fragment != currentFragment) {
            if (getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName()) != null) {
                NavigationHelper.showFragment(this, fragment);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment, fragment.getClass().getName())
                        .commit();
            }

            currentFragment = fragment;
            updateBottomNavigation(fragment);
        }
    }

    private void updateBottomNavigation(Fragment fragment) {
        int itemId = getMenuIdFromFragment(fragment);
        if (itemId != -1) {
            bottomNavigationView.setSelectedItemId(itemId);
            bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        } else {
            deselectAllBottomNavigationItems();
        }
    }


    private int getMenuIdFromFragment(Fragment fragment) {
        if (fragment instanceof HomeFragment) return R.id.nav_home;
        if (fragment instanceof TransactionFragment) return R.id.nav_movements;
        if (fragment instanceof BudgetFragment) return R.id.nav_budget;
        if (fragment instanceof GoalFragment) return R.id.nav_goals;

        return -1;
    }

    private void deselectAllBottomNavigationItems() {
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
    }

    public void restorePreviousFragment() {
        if (previousFragment != null && getSupportFragmentManager().findFragmentByTag(previousFragment.getClass().getName()) != null) {
            switchFragment(previousFragment);
        } else {
            previousFragment = homeFragment;
            switchFragment(homeFragment);
        }

        previousFragment = null;
        setTopAppBarMainMenu();
        setBottomNavigationVisibility(View.VISIBLE);
    }


    @Override
    public void onEnterSelectionMode() {
        setTopAppBarSelectionMenu();
        setBottomNavigationVisibility(View.GONE);
    }

    @Override
    public void onExitSelectionMode() {
        setTopAppBarMainMenu();
        setBottomNavigationVisibility(View.VISIBLE);
    }

    public void setBottomNavigationVisibility(int visibility) {
        bottomNavigationView.setVisibility(visibility);
    }

    private void setTopAppBarMainMenu() {
        setupAppBarMenu(R.menu.menu_overflow, R.string.app_name, null, null, item -> handleMenuClick(item.getItemId()));
    }

    private void setTopAppBarSelectionMenu() {
        setupAppBarMenu(R.menu.selection_menu, -1, R.drawable.ic_close, v -> onSelectionExit(), item -> onDeleteClick(item.getItemId()));
    }

    private void setupAppBarMenu(int menuRes, int titleRes, Integer navIconRes, View.OnClickListener navClickListener, Toolbar.OnMenuItemClickListener menuClickListener) {
        topAppBar.getMenu().clear();
        topAppBar.inflateMenu(menuRes);

        if (titleRes != -1) topAppBar.setTitle(titleRes);

        if (navIconRes != null) topAppBar.setNavigationIcon(navIconRes);
        else topAppBar.setNavigationIcon(null);

        topAppBar.setNavigationOnClickListener(navClickListener);
        topAppBar.setOnMenuItemClickListener(menuClickListener);
    }

    private void onSelectionExit() {
        if (currentFragment instanceof BudgetFragment) budgetFragment.onExitSelectionMode();
        if (currentFragment instanceof GoalFragment) goalFragment.onExitSelectionMode();
        if (currentFragment instanceof TransactionFragment)
            transactionFragment.onExitSelectionMode();
    }

    private boolean onDeleteClick(int itemId) {
        if (itemId == R.id.action_delete) {
            if (currentFragment instanceof BudgetFragment) budgetFragment.deleteSelected();
            if (currentFragment instanceof GoalFragment) goalFragment.deleteSelected();
            if (currentFragment instanceof TransactionFragment)
                transactionFragment.deleteSelected();

            return true;
        }

        return false;
    }

    private boolean handleMenuClick(int itemId) {
        if (itemId == R.id.menu_currency_converter && currentFragment != exchangeFragment) {
            navigateToFragment(exchangeFragment);
            return true;
        } else if (itemId == R.id.menu_settings && currentFragment != settingsFragment) {
            navigateToFragment(settingsFragment);
            return true;
        }
        return false;
    }

    private void navigateToFragment(Fragment fragment) {
        if (currentFragment != fragment) {
            if (currentFragment != settingsFragment && currentFragment != exchangeFragment) {
                previousFragment = currentFragment;
            }

            setNavigationBackButton();
            switchFragment(fragment);
            setBottomNavigationVisibility(View.GONE);
        }
    }

    private void setNavigationBackButton() {
        topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        topAppBar.setNavigationOnClickListener(v -> restorePreviousFragment());
    }

    @Override
    public void onSelectionCountChanged(int count) {
        topAppBar.setTitle(getResources().getQuantityString(R.plurals.selection_count, count, count));
    }

}
