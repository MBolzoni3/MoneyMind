package it.unimib.devtrinity.moneymind.ui.fragment.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import it.unimib.devtrinity.moneymind.ui.MainNavigationActivity;
import it.unimib.devtrinity.moneymind.ui.MainActivity;
import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.utils.FirebaseHelper;
import it.unimib.devtrinity.moneymind.utils.NavigationHelper;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    TextInputEditText emailInput;
    TextInputEditText passwordInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);

        MaterialButton loginButton = view.findViewById(R.id.login_button);
        MaterialButton registerButton = view.findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> loginAction());
        registerButton.setOnClickListener(v -> NavigationHelper.loadFragment((MainActivity) getActivity(), new RegisterFragment()));

        return view;
    }

    private void loginAction(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Inserisci email e password", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseHelper.getInstance().loginUser(email, password, getActivity());
    }
}
