package it.unimib.devtrinity.moneymind.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.MainActivity;
import it.unimib.devtrinity.moneymind.ui.MainNavigationActivity;

public class NavigationHelper {
   private static final String TAG = "NavigationHelper";

   public static void navigateToActivity(Context context, Class<?> targetActivity) {
      Intent intent = new Intent(context, targetActivity);
      context.startActivity(intent);

      // Se il Context è un'Activity, chiudila
      if (context instanceof Activity) {
         ((Activity) context).finish();
      }
   }

   public static void navigateToMain(Context context){
      navigateToActivity(context, MainNavigationActivity.class);
   }

   public static void navigateToLogin(Context context){
      navigateToActivity(context, MainActivity.class);
   }

   public static void loadFragment(AppCompatActivity activity, Fragment fragment, boolean addToBackStack) {
      String fragmentTag = fragment.getClass().getSimpleName();

      // Controlla se il Fragment è già nello stack
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

   public static void loadFragment(AppCompatActivity activity, Fragment fragment){
      loadFragment(activity, fragment, true);
   }

}
