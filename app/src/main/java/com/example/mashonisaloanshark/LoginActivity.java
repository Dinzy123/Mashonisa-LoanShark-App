package com.example.mashonisaloanshark;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // UI components
    private EditText emailField, passwordField;
    private Button btnLogin, btnRegister;
    private ImageView btnGoogleSignIn;
    private TextView forgotPasswordLink;

    // Firebase components
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private DatabaseReference databaseReference;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI components
        emailField = findViewById(R.id.log_username);
        passwordField = findViewById(R.id.log_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnGoogleSignIn = findViewById(R.id.btn_google_signup);
        forgotPasswordLink = findViewById(R.id.sn_loginlink);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set listeners
        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        forgotPasswordLink.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserInterestRate(user.getEmail());
                        }
                    } else {
                        Log.e(TAG, "Google authentication failed", task.getException());
                        Toast.makeText(this, "Google authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInterestRate(String email) {
        if (email == null) {
            Log.e(TAG, "Email is null for the current user.");
            Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            return;
        }

        String encodedEmail = email.replace(".", ",");
        Query query = databaseReference.child(encodedEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Direct the user to the MainActivity directly if they are already registered.
                    Intent nextActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(nextActivity);
                    finish(); // Optional: Close the LoginActivity
                } else {
                    Log.w(TAG, "Google account not registered in the database.");
                    Toast.makeText(LoginActivity.this, "Google account not registered. Please sign up.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(LoginActivity.this, "Database error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Please enter your email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Please enter your password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkUserInterestRate(email);
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Log.e(TAG, "Login failed: " + errorMessage);
                Toast.makeText(this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        String email = emailField.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Please enter your email to reset password");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Do you want to reset your password?")
                .setPositiveButton("Yes", (dialog, which) -> sendResetPasswordEmail(email))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void sendResetPasswordEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset password email sent. Check your email.", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                        Log.e(TAG, "Error sending reset email: " + errorMessage);
                        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
