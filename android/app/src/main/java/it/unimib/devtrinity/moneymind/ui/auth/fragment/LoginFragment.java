package it.unimib.devtrinity.moneymind.ui.auth.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.ui.activity.MainActivity;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.LoginViewModel;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.LoginViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericState;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.TextInputHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private ProgressBar loadingIndicator;
    private View loadingOverlay;

    private TextInputEditText emailInput, passwordInput;
    private TextInputLayout emailLayout, passwordLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingIndicator = view.findViewById(R.id.loading_indicator);
        loadingOverlay = view.findViewById(R.id.loading_overlay);

        UserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
        LoginViewModelFactory factory = new LoginViewModelFactory(userRepository);
        loginViewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        emailLayout = view.findViewById(R.id.email_layout);
        passwordLayout = view.findViewById(R.id.password_layout);

        MaterialButton loginButton = view.findViewById(R.id.login_button);
        MaterialButton registerButton = view.findViewById(R.id.register_button);

        loginViewModel.getLoginState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof GenericState.Loading) {
                toggleLoadingView(true);
            } else if (state instanceof GenericState.Success) {
                NavigationHelper.navigateToMain(getContext());
            } else if (state instanceof GenericState.Failure) {
                toggleLoadingView(false);
                String error = ((GenericState.Failure<String>) state).getErrorMessage();
                Utils.makeSnackBar(view, error);
            }
        });

        emailInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.performClick();
                return true;
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            Utils.closeKeyboard(requireContext(), view);

            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (validateInput()) {
                loginViewModel.login(email, password, getContext());
            }
        });

        registerButton.setOnClickListener(v -> {
            NavigationHelper.loadFragment((MainActivity) getActivity(), new RegisterFragment());
        });
    }

    private boolean validateInput() {
        if (!TextInputHelper.validateField(emailLayout, emailInput, getString(R.string.error_field_required), getString(R.string.error_email_invalid), TextInputHelper.LOGIN_EMAIL_REGEX)) {
            return false;
        }

        if (!TextInputHelper.validateField(passwordLayout, passwordInput, getString(R.string.error_field_required), null, null)) {
            return false;
        }

        return true;
    }

    private void toggleLoadingView(boolean isLoading) {
        int visibility = isLoading ? View.VISIBLE : View.GONE;
        loadingIndicator.setVisibility(visibility);
        loadingOverlay.setVisibility(visibility);
    }

}
