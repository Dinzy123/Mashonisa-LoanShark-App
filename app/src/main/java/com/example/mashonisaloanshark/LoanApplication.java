package com.example.mashonisaloanshark;

public class LoanApplication {
    private String borrowerName;
    private String gender;
    private String loanStartDate;
    private String phoneNumber;
    private String loanAmount;
    private String loanPurpose;
    private String loanDuration;
    private String loanCategory;
    private String borrowerType;
    private boolean agreeToTerms;

    // Constructor
    public LoanApplication(String borrowerName, String gender, String loanStartDate, String phoneNumber,
                           String loanAmount, String loanPurpose, String loanDuration, String loanCategory,
                           String borrowerType, boolean agreeToTerms) {
        this.borrowerName = borrowerName;
        this.gender = gender;
        this.loanStartDate = loanStartDate;
        this.phoneNumber = phoneNumber;
        this.loanAmount = loanAmount;
        this.loanPurpose = loanPurpose;
        this.loanDuration = loanDuration;
        this.loanCategory = loanCategory;
        this.borrowerType = borrowerType;
        this.agreeToTerms = agreeToTerms;
    }

    // Getters and setters (optional, if needed)
}
