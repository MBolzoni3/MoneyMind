package it.unimib.devtrinity.moneymind.ui.auth.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.RegisterViewModel;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.RegisterViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericState;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class RegisterFragment extends Fragment {
    private RegisterViewModel registerViewModel;

    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText nameInput;
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

        loadingIndicator = view.findViewById(R.id.loading_indicator);
        loadingOverlay = view.findViewById(R.id.loading_overlay);

        Button registerButton = view.findViewById(R.id.register_button);
        TextView loginLink = view.findViewById(R.id.login_link);

        registerButton.setOnClickListener(v -> registerAction());
        loginLink.setOnClickListener(v -> getParentFragmentManager().popBackStack());

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
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerAction() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Utils.makeSnackBar(thisView, getString(R.string.field_error));
            return;
        }

        if (!password.equals(confirmPassword)) {
            Utils.makeSnackBar(thisView, getString(R.string.password_error));
            return;
        }

        if (password.length() < 6) {
            Utils.makeSnackBar(thisView, getString(R.string.password_length));
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

