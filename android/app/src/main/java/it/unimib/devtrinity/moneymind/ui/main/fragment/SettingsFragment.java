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

import java.util.Locale;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.repository.DatabaseRepository;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.SettingsViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.SettingsViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseRepository databaseRepository = ServiceLocator.getInstance().getDatabaseRepository(requireActivity().getApplication());
        SettingsViewModelFactory factory = new SettingsViewModelFactory(databaseRepository);
        SettingsViewModel viewModel = new ViewModelProvider(this, factory).get(SettingsViewModel.class);
        viewModel.initTheme(requireActivity().getApplication());
        viewModel.initLanguage(requireActivity().getApplication());

        MaterialButtonToggleGroup toggleGroupTheme = view.findViewById(R.id.theme_toggle_group);
        MaterialButton buttonLight = view.findViewById(R.id.button_light);
        MaterialButton buttonDark = view.findViewById(R.id.button_dark);
        MaterialButton buttonAuto = view.findViewById(R.id.button_auto);

        MaterialButtonToggleGroup toggleGroupLanguage = view.findViewById(R.id.language_toggle_group);
        MaterialButton buttonItalian = view.findViewById(R.id.button_it);
        MaterialButton buttonEnglish = view.findViewById(R.id.button_en);

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

        toggleGroupTheme.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                group.check(checkedId);
                return;
            }

            if (checkedId == R.id.button_light)
                viewModel.setTheme(requireActivity().getApplication(), AppCompatDelegate.MODE_NIGHT_NO);
            else if (checkedId == R.id.button_dark)
                viewModel.setTheme(requireActivity().getApplication(), AppCompatDelegate.MODE_NIGHT_YES);
            else if (checkedId == R.id.button_auto)
                viewModel.setTheme(requireActivity().getApplication(), AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        });

        viewModel.getLanguage().observe(getViewLifecycleOwner(), language -> {
            if (language == null) return;

            if (language.equals(Locale.ITALIAN.getLanguage())) {
                buttonItalian.setChecked(true);
            } else if (language.equals(Locale.ENGLISH.getLanguage())) {
                buttonEnglish.setChecked(true);
            }
        });

        toggleGroupLanguage.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                group.check(checkedId);
                return;
            }

            if (checkedId == R.id.button_it)
                viewModel.setLanguage(requireActivity().getApplication(), Locale.ITALIAN.getLanguage());
            else if (checkedId == R.id.button_en)
                viewModel.setLanguage(requireActivity().getApplication(), Locale.ENGLISH.getLanguage());
        });

        btnLogout.setOnClickListener(v -> viewModel.logout(requireActivity()));
        viewModel.getLogoutLiveData().observe(getViewLifecycleOwner(), loggedOut -> {
            if (loggedOut) {
                NavigationHelper.navigateToLogin(requireActivity());
            }
        });
    }

}
