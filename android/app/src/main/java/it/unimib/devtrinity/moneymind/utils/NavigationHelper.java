package it.unimib.devtrinity.moneymind.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.activity.MainActivity;
import it.unimib.devtrinity.moneymind.ui.activity.MainNavigationActivity;

public class NavigationHelper {
    private static final String TAG = NavigationHelper.class.getSimpleName();

    public static void navigateToActivity(Context context, Class<?> targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    public static void navigateToMain(Context context) {
        navigateToActivity(context, MainNavigationActivity.class);
    }

    public static void navigateToLogin(Context context) {
        navigateToActivity(context, MainActivity.class);
    }

    public static void addFragments(AppCompatActivity activity, List<Fragment> fragments) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment fragment : fragments) {
            if (fragmentManager.findFragmentByTag(fragment.getClass().getName()) == null) {
                fragmentTransaction.add(R.id.fragment_container, fragment, fragment.getClass().getName());
                fragmentTransaction.hide(fragment);
            }
        }

        fragmentTransaction.commitNow();
    }

    public static void showFragment(AppCompatActivity activity, Fragment fragmentToShow) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment == fragmentToShow) {
                fragmentTransaction.show(fragment);
            } else if (fragment.isAdded()) {
                fragmentTransaction.hide(fragment);
            }
        }

        fragmentTransaction.commit();
    }

    public static void loadFragment(AppCompatActivity activity, Fragment fragment, boolean addToBackStack) {
        String fragmentTag = fragment.getClass().getSimpleName();

        if (activity.getSupportFragmentManager().findFragmentByTag(fragmentTag) != null) {
            return;
        }

        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, fragmentTag);

        if (addToBackStack) {
            transaction.addToBackStack(fragmentTag);
        }

        transaction.commit();
    }

    public static void loadFragment(AppCompatActivity activity, Fragment fragment) {
        loadFragment(activity, fragment, true);
    }

}
