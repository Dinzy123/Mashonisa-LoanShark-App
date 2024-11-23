package com.example.mashonisaloanshark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InterestRateActivity extends AppCompatActivity {

    private CardView cardInterest10, cardInterest15, cardInterest20, cardInterest25;
    private EditText editTextCustomRate;
    private Button btnAddInterest;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_rate);

        // Initialize Firebase user and database reference
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI components
        cardInterest10 = findViewById(R.id.card_interest_10);
        cardInterest15 = findViewById(R.id.card_interest_15);
        cardInterest20 = findViewById(R.id.card_interest_20);
        cardInterest25 = findViewById(R.id.card_interest_25);
        editTextCustomRate = findViewById(R.id.editTextCustomRate);
        btnAddInterest = findViewById(R.id.btnAddInterest);

        // Set onClick listeners for interest rate selection
        cardInterest10.setOnClickListener(v -> selectInterestRate(10));
        cardInterest15.setOnClickListener(v -> selectInterestRate(20));
        cardInterest20.setOnClickListener(v -> selectInterestRate(30));
        cardInterest25.setOnClickListener(v -> selectInterestRate(40));

        // Set onClick listener for adding custom interest rate
        btnAddInterest.setOnClickListener(v -> {
            String customRateText = editTextCustomRate.getText().toString();
            if (!customRateText.isEmpty()) {
                try {
                    int customRate = Integer.parseInt(customRateText);
                    selectInterestRate(customRate);
                } catch (NumberFormatException e) {
                    Toast.makeText(InterestRateActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(InterestRateActivity.this, "Please enter an interest rate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectInterestRate(int interestRate) {
        // Check if user is logged in
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            // Encode the email to make it safe for Firebase keys
            String encodedEmail = userEmail.replace(".", ",");

            // Save the interest rate under the user's email
            databaseReference.child(encodedEmail).child("interestRate")
                    .setValue(interestRate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(InterestRateActivity.this, "Interest rate of " + interestRate + "% selected", Toast.LENGTH_SHORT).show();
                            // Navigate to BusinessRegProfileActivity
                            Intent intent = new Intent(InterestRateActivity.this, BusinessRegProfileActivity.class);
                            startActivity(intent);
                            finish(); // Close the current activity
                        } else {
                            Toast.makeText(InterestRateActivity.this, "Failed to update interest rate", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
