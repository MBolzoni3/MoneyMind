package it.unimib.devtrinity.moneymind.ui.auth.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.ui.auth.viewmodel.RegisterViewModel;
import it.unimib.devtrinity.moneymind.utils.GenericState;

public class RegisterFragment extends Fragment {
    private RegisterViewModel registerViewModel;

    EditText emailInput;
    EditText passwordInput;
    EditText confirmPasswordInput;
    EditText nameInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        nameInput = view.findViewById(R.id.register_name_input);
        emailInput = view.findViewById(R.id.register_email_input);
        passwordInput = view.findViewById(R.id.register_password_input);
        confirmPasswordInput = view.findViewById(R.id.register_confirm_password_input);

        Button registerButton = view.findViewById(R.id.register_button);
        TextView loginLink = view.findViewById(R.id.login_link);

        registerButton.setOnClickListener(v -> registerAction());
        loginLink.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Inizializza il ViewModel
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Osserva lo stato della registrazione
        registerViewModel.getRegisterState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof GenericState.Loading) {
                // Mostra una progress bar
            } else if (state instanceof GenericState.Success) {
                Toast.makeText(getContext(), "Registrazione completata! Effettua il login.", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else if (state instanceof GenericState.Failure) {
                String error = ((GenericState.Failure<Void>) state).getErrorMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void registerAction() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getActivity(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Le password non coincidono", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getActivity(), "La password deve avere almeno 6 caratteri", Toast.LENGTH_SHORT).show();
            return;
        }

        registerViewModel.register(name, email, password);
    }
}

