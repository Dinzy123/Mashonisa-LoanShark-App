package com.example.mashonisaloanshark;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMemberActivity extends AppCompatActivity {

    private EditText memberNameEditText, loanAmountEditText;
    private Button submitMemberButton;
    private DatabaseReference membersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_member_activity);

        // Initialize views
        memberNameEditText = findViewById(R.id.memberNameEditText);
        loanAmountEditText = findViewById(R.id.loanAmountEditText);
        submitMemberButton = findViewById(R.id.submitMemberButton);

        // Initialize Firebase reference for members
        membersReference = FirebaseDatabase.getInstance().getReference("Members");

        // Submit button functionality to add new member
        submitMemberButton.setOnClickListener(v -> {
            String memberName = memberNameEditText.getText().toString().trim();
            String loanAmount = loanAmountEditText.getText().toString().trim();

            if (!memberName.isEmpty() && !loanAmount.isEmpty()) {
                String memberId = membersReference.push().getKey();
                Member member = new Member(memberId, memberName, loanAmount);
                membersReference.child(memberId).setValue(member);
                Toast.makeText(AddMemberActivity.this, "Member added successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close activity after submission
            } else {
                Toast.makeText(AddMemberActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

// Member class to store member details
class Member {
    public String memberId;
    public String name;
    public String loanAmount;

    public Member(String memberId, String name, String loanAmount) {
        this.memberId = memberId;
        this.name = name;
        this.loanAmount = loanAmount;
    }
}
