package it.unimib.devtrinity.moneymind.ui.auth.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.activity.MainActivity;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.LoginViewModel;
import it.unimib.devtrinity.moneymind.utils.GenericState;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;
import it.unimib.devtrinity.moneymind.utils.SyncHelper;

public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getLoginState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof GenericState.Loading) {
                // Mostra una progress bar
            } else if (state instanceof GenericState.Success) {
                NavigationHelper.navigateToMain(getContext());
                SyncHelper.triggerManualSync(getContext());
            } else if (state instanceof GenericState.Failure) {
                String error = ((GenericState.Failure<String>) state).getErrorMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.login_button).setOnClickListener(v -> {
            String email = ((EditText) view.findViewById(R.id.email_input)).getText().toString();
            String password = ((EditText) view.findViewById(R.id.password_input)).getText().toString();

            if (validateInput(email, password)) {
                loginViewModel.login(email, password);
            } else {
                Toast.makeText(getContext(), "Please enter valid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.register_button).setOnClickListener(v -> {
            NavigationHelper.loadFragment((MainActivity) getActivity(), new RegisterFragment());
        });
    }

    private boolean validateInput(String email, String password) {
        return email != null && !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && password != null && !password.isEmpty();
    }
}
