package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.SettingsViewModel;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SettingsViewModel viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        viewModel.initTheme(requireActivity().getApplication());

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.theme_toggle_group);
        MaterialButton buttonLight = view.findViewById(R.id.button_light);
        MaterialButton buttonDark = view.findViewById(R.id.button_dark);
        MaterialButton buttonAuto = view.findViewById(R.id.button_auto);
        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);

        viewModel.getTheme().observe(getViewLifecycleOwner(), theme -> {
            if (theme == null) return;

            if (theme == AppCompatDelegate.MODE_NIGHT_NO) {
                buttonLight.setChecked(true);
            } else if (theme == AppCompatDelegate.MODE_NIGHT_YES) {
                buttonDark.setChecked(true);
            } else {
                buttonAuto.setChecked(true);
            }
        });

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.button_light)
                viewModel.setTheme(requireActivity().getApplication(), AppCompatDelegate.MODE_NIGHT_NO);
            else if (checkedId == R.id.button_dark)
                viewModel.setTheme(requireActivity().getApplication(), AppCompatDelegate.MODE_NIGHT_YES);
            else if (checkedId == R.id.button_auto)
                viewModel.setTheme(requireActivity().getApplication(), AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        });

        btnLogout.setOnClickListener(v -> viewModel.logout(requireActivity()));
    }

}
