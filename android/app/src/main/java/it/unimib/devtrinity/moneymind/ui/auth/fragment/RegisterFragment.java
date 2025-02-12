package it.unimib.devtrinity.moneymind.ui.auth.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.RegisterViewModel;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.RegisterViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericState;
import it.unimib.devtrinity.moneymind.utils.TextInputHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class RegisterFragment extends Fragment {
    private RegisterViewModel registerViewModel;

    private TextInputEditText emailInput, passwordInput, confirmPasswordInput, nameInput;
    private TextInputLayout emailLayout, passwordLayout, confirmPasswordLayout, nameLayout;
    private ProgressBar loadingIndicator;
    private View loadingOverlay;
    private View thisView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thisView = view;

        nameInput = view.findViewById(R.id.register_name_input);
        emailInput = view.findViewById(R.id.register_email_input);
        passwordInput = view.findViewById(R.id.register_password_input);
        confirmPasswordInput = view.findViewById(R.id.register_confirm_password_input);

        nameLayout = view.findViewById(R.id.register_name_layout);
        emailLayout = view.findViewById(R.id.register_email_layout);
        passwordLayout = view.findViewById(R.id.register_password_layout);
        confirmPasswordLayout = view.findViewById(R.id.register_confirm_password_layout);

        loadingIndicator = view.findViewById(R.id.loading_indicator);
        loadingOverlay = view.findViewById(R.id.loading_overlay);

        Button registerButton = view.findViewById(R.id.register_button);
        TextView loginLink = view.findViewById(R.id.login_link);

        registerButton.setOnClickListener(v -> {
            Utils.closeKeyboard(requireContext(), view);
            registerAction();
        });
        loginLink.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        confirmPasswordInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerButton.performClick();
                return true;
            }
            return false;
        });

        UserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
        RegisterViewModelFactory factory = new RegisterViewModelFactory(userRepository);
        registerViewModel = new ViewModelProvider(this, factory).get(RegisterViewModel.class);

        registerViewModel.getRegisterState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof GenericState.Loading) {
                toggleLoadingView(true);
            } else if (state instanceof GenericState.Success) {
                Utils.makeSnackBar(view, getString(R.string.completed_registration));
                getParentFragmentManager().popBackStack();
            } else if (state instanceof GenericState.Failure) {
                toggleLoadingView(false);
                String error = ((GenericState.Failure<Void>) state).getErrorMessage();
                Utils.makeSnackBar(view, error);
            }
        });

        bindInputValidation();
    }

    public void bindInputValidation() {
        TextInputHelper.addValidationWatcher(nameLayout, nameInput, getString(R.string.error_field_required), getString(R.string.error_name_invalid), TextInputHelper.LOGIN_NAME_REGEX);
        TextInputHelper.addValidationWatcher(emailLayout, emailInput, getString(R.string.error_field_required), getString(R.string.error_email_invalid), TextInputHelper.LOGIN_EMAIL_REGEX);
        TextInputHelper.addValidationWatcher(passwordLayout, passwordInput, getString(R.string.error_field_required), getString(R.string.error_password_invalid), TextInputHelper.LOGIN_PASSWORD_REGEX);
    }

    public void registerAction() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (!TextInputHelper.validateField(nameLayout, nameInput, getString(R.string.error_field_required), getString(R.string.error_name_invalid), TextInputHelper.LOGIN_NAME_REGEX)) {
            return;
        }

        if (!TextInputHelper.validateField(emailLayout, emailInput, getString(R.string.error_field_required), getString(R.string.error_email_invalid), TextInputHelper.LOGIN_EMAIL_REGEX)) {
            return;
        }

        if (!TextInputHelper.validateField(passwordLayout, passwordInput, getString(R.string.error_field_required), getString(R.string.error_password_invalid), TextInputHelper.LOGIN_PASSWORD_REGEX)) {
            return;
        }

        if (!TextInputHelper.validateField(confirmPasswordLayout, confirmPasswordInput, getString(R.string.error_field_required), getString(R.string.error_confirm_password_invalid), Pattern.quote(password))) {
            return;
        }

        registerViewModel.register(name, email, password);
    }

    private void toggleLoadingView(boolean isLoading) {
        int visibility = isLoading ? View.VISIBLE : View.GONE;
        loadingIndicator.setVisibility(visibility);
        loadingOverlay.setVisibility(visibility);
    }

}

