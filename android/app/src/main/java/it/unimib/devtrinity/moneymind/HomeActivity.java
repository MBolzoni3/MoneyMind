package it.unimib.devtrinity.moneymind;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inizializza FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Verifica l'utente loggato
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        // Mostra un messaggio di benvenuto
        Toast.makeText(this, "Benvenuto, " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();

        // Collega il pulsante di logout
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(this, "Logout effettuato", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish(); // Chiude la HomeActivity
    }
}
