package com.example.mashonisaloanshark;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusinessRegProfileActivity extends AppCompatActivity {

    private EditText editTextRegisteredEmail, editTextBusinessName, editTextBusinessAddress, editTextBusinessContact, editTextBusinessDescription;
    private Button buttonRegisterBusiness;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_business_profile);

        // Initialize views
        editTextRegisteredEmail = findViewById(R.id.editTextRegisteredEmail);
        editTextBusinessName = findViewById(R.id.editTextBusinessName);
        editTextBusinessAddress = findViewById(R.id.editTextBusinessAddress);
        editTextBusinessContact = findViewById(R.id.editTextBusinessContact);
        editTextBusinessDescription = findViewById(R.id.editTextBusinessDescription);
        buttonRegisterBusiness = findViewById(R.id.buttonRegisterBusiness);

        // Get the currently logged-in user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // Redirect to login if no user is logged in
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(BusinessRegProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("businessProfiles");

        // Set up button click listener
        buttonRegisterBusiness.setOnClickListener(v -> registerBusiness());

        // Set filters to limit the contact input to numeric characters and 10 digits
        editTextBusinessContact.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(10),  // Limit to 10 digits
                (source, start, end, dest, dstart, dend) -> {
                    // Only allow numeric characters
                    if (source.length() > 0 && !Character.isDigit(source.charAt(0))) {
                        return "";
                    }
                    return null;
                }
        });
    }

    private void registerBusiness() {
        String registeredEmail = editTextRegisteredEmail.getText().toString().trim();
        String businessName = editTextBusinessName.getText().toString().trim();
        String businessAddress = editTextBusinessAddress.getText().toString().trim();
        String businessContact = editTextBusinessContact.getText().toString().trim();
        String businessDescription = editTextBusinessDescription.getText().toString().trim();

        // Validate inputs
        if (registeredEmail.isEmpty() || businessName.isEmpty() || businessAddress.isEmpty() || businessContact.isEmpty() || businessDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the entered email matches the authenticated user's email
        if (!registeredEmail.equals(currentUser.getEmail())) {
            Toast.makeText(this, "Email does not match the logged-in user's email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sanitize the email to make it a valid Firebase key
        String sanitizedEmail = registeredEmail.replace(".", ",");  // Replace periods with commas to avoid issues with Firebase keys

        // Create a new Business object
        Business business = new Business(businessName, businessAddress, businessContact, businessDescription);

        // Save the business profile information under the sanitized email in Firebase
        databaseReference.child(sanitizedEmail).setValue(business)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(BusinessRegProfileActivity.this, "Business profile registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BusinessRegProfileActivity.this, MainActivity.class));
                        finish();  // Close the current activity
                    } else {
                        Toast.makeText(BusinessRegProfileActivity.this, "Failed to register business profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
