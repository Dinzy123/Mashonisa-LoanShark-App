package com.example.mashonisaloanshark;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // UI elements
    private EditText etBorrowerName, etGender, etLoanStartDate, etPhoneNumber, etLoanAmount,
            etLoanPurpose, etLoanDuration, etLoanPlan;
    private Spinner spinnerLoanCategory;
    private RadioGroup rgBorrowerType;
    private CheckBox cbAgreeTerms;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        etBorrowerName = findViewById(R.id.etBorrowerName);
        etGender = findViewById(R.id.etGender);
        etLoanStartDate = findViewById(R.id.etLoanStartDate);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etLoanAmount = findViewById(R.id.etLoanAmount);
        etLoanPurpose = findViewById(R.id.etLoanPurpose);
        etLoanDuration = findViewById(R.id.etLoanDuration);
        etLoanPlan = findViewById(R.id.etLoanPlan);
        spinnerLoanCategory = findViewById(R.id.spinnerLoanCategory);
       // rgBorrowerType = findViewById(R.id.rgBorrowerType);
        cbAgreeTerms = findViewById(R.id.cbAgreeTerms);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Set submit button onClick listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLoanApplication();
            }
        });
    }

    private void saveLoanApplication() {
        // Get the current logged-in user ID
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Get all form input values
        String borrowerName = etBorrowerName.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String loanStartDate = etLoanStartDate.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String loanAmount = etLoanAmount.getText().toString().trim();
        String loanPurpose = etLoanPurpose.getText().toString().trim();
        String loanDuration = etLoanDuration.getText().toString().trim();
        String loanPlan = etLoanPlan.getText().toString().trim();
        String loanCategory = spinnerLoanCategory.getSelectedItem().toString();

        int selectedBorrowerTypeId = rgBorrowerType.getCheckedRadioButtonId();
        RadioButton selectedBorrowerType = findViewById(selectedBorrowerTypeId);
        String borrowerType = selectedBorrowerType.getText().toString();

        boolean agreeToTerms = cbAgreeTerms.isChecked();

        // Validate required fields
        if (borrowerName.isEmpty() || loanAmount.isEmpty() || loanPurpose.isEmpty() || !agreeToTerms) {
            Toast.makeText(this, "Please fill all required fields and agree to terms", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a LoanApplication object
        LoanApplication loanApplication = new LoanApplication(
                borrowerName, gender, loanStartDate, phoneNumber, loanAmount, loanPurpose,
                loanDuration, loanPlan, loanCategory, borrowerType, agreeToTerms
        );

        // Save data to Firebase Database under the logged-in user
        mDatabase.child("users").child(userId).child("loanApplications").push().setValue(loanApplication)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Loan application submitted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to submit application. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
