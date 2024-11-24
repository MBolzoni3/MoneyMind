package it.unimib.devtrinity.moneymind.ui.fragment.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import it.unimib.devtrinity.moneymind.ui.MainNavigationActivity;
import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.utils.FirebaseHelper;

public class RegisterFragment extends Fragment {

    EditText emailInput;
    EditText passwordInput;
    EditText confirmPasswordInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        emailInput = view.findViewById(R.id.register_email_input);
        passwordInput = view.findViewById(R.id.register_password_input);
        confirmPasswordInput = view.findViewById(R.id.register_confirm_password_input);

        Button registerButton = view.findViewById(R.id.register_button);
        TextView loginLink = view.findViewById(R.id.login_link);

        registerButton.setOnClickListener(v -> registerAction());
        loginLink.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    public void registerAction(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
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

        FirebaseHelper.getInstance().registerUser(email, password, getActivity());
    }

}
