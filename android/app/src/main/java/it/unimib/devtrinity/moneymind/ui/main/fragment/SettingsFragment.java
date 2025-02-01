package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.activity.MainNavigationActivity;

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

        ChipGroup chipGroupTheme = view.findViewById(R.id.chip_group_theme);
        Chip chipLight = view.findViewById(R.id.chip_light);
        Chip chipDark = view.findViewById(R.id.chip_dark);
        Chip chipAuto = view.findViewById(R.id.chip_auto);

        String currentTheme = getCurrentTheme();

        if ("light".equals(currentTheme)) {
            chipLight.setChecked(true);
        } else if ("dark".equals(currentTheme)) {
            chipDark.setChecked(true);
        } else {
            chipAuto.setChecked(true);
        }

        chipGroupTheme.setOnCheckedStateChangeListener((group, checkedChipIds) -> {
            if (checkedChipIds.contains(R.id.chip_light)) {
                ((MainNavigationActivity) requireActivity()).changeTheme("light");
            } else if (checkedChipIds.contains(R.id.chip_dark)) {
                ((MainNavigationActivity) requireActivity()).changeTheme("dark");
            } else if (checkedChipIds.contains(R.id.chip_auto)) {
                ((MainNavigationActivity) requireActivity()).changeTheme("auto");
            }
        });

    }

    private String getCurrentTheme() {
        return "auto";
    }

}
