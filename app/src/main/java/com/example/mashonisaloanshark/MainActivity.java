package com.example.mashonisaloanshark;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // UI elements
    private EditText etBorrowerName, etGender, etLoanStartDate, etPhoneNumber, etLoanAmount,
            etLoanPurpose, etLoanDuration;
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
        spinnerLoanCategory = findViewById(R.id.spinnerLoanCategory);
        rgBorrowerType = findViewById(R.id.rgBorrowerType);
        cbAgreeTerms = findViewById(R.id.cbAgreeTerms);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Populate spinner with loan categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.loan_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoanCategory.setAdapter(adapter);

        // Set up date picker for loan start date
        etLoanStartDate.setOnClickListener(v -> showDatePickerDialog());

        // Submit button listener
        btnSubmit.setOnClickListener(v -> saveLoanApplication());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = (month1 + 1) + "/" + dayOfMonth + "/" + year1;
                    etLoanStartDate.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveLoanApplication() {
        // Check if the user is authenticated
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Retrieve form input values
        String borrowerName = etBorrowerName.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String loanStartDate = etLoanStartDate.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String loanAmount = etLoanAmount.getText().toString().trim();
        String loanPurpose = etLoanPurpose.getText().toString().trim();
        String loanDuration = etLoanDuration.getText().toString().trim();
        String loanCategory = spinnerLoanCategory.getSelectedItem().toString();

        int selectedBorrowerTypeId = rgBorrowerType.getCheckedRadioButtonId();
        RadioButton selectedBorrowerType = findViewById(selectedBorrowerTypeId);
        String borrowerType = selectedBorrowerType.getText().toString();

        boolean agreeToTerms = cbAgreeTerms.isChecked();

        // Validate required fields
        if (borrowerName.isEmpty() || loanAmount.isEmpty() || loanPurpose.isEmpty() || !agreeToTerms) {
            Toast.makeText(this, "Fill all required fields and agree to terms", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create LoanApplication object
        LoanApplication loanApplication = new LoanApplication(
                borrowerName, gender, loanStartDate, phoneNumber, loanAmount, loanPurpose,
                loanDuration, loanCategory, borrowerType, agreeToTerms
        );

        // Save data to Firebase
        mDatabase.child("users").child(userId).child("loanApplications").push()
                .setValue(loanApplication)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Loan application submitted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to submit application. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
