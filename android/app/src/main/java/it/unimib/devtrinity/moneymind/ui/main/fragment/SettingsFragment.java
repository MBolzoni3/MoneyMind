package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.activity.MainNavigationActivity;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainNavigationActivity) requireActivity()).restorePreviousFragment();
            }
        });

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.theme_toggle_group);
        MaterialButton buttonLight = view.findViewById(R.id.button_light);
        MaterialButton buttonDark = view.findViewById(R.id.button_dark);
        MaterialButton buttonAuto = view.findViewById(R.id.button_auto);

        int currentTheme = SharedPreferencesHelper.getTheme(getContext());
        switch (currentTheme) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                buttonLight.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                buttonDark.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                buttonAuto.setChecked(true);
                break;
        }

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(!isChecked) return;

            if (checkedId == R.id.button_light) {
                changeTheme(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.button_dark) {
                changeTheme(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (checkedId == R.id.button_auto) {
                changeTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });

        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            FirebaseHelper.getInstance().logoutUser();
            SharedPreferencesHelper.clearSharedPrefs(getContext());
            NavigationHelper.navigateToLogin(getContext());
        });

    }

    private void changeTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        SharedPreferencesHelper.setTheme(getContext(), theme);
    }

}
