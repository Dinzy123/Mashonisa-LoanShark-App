package com.example.mashonisaloanshark;

public class LoanApplication {
    private String borrowerName;
    private String gender;
    private String loanStartDate;
    private String phoneNumber;
    private String loanAmount;
    private String loanPurpose;
    private String loanDuration;
    private String loanPlan;
    private String loanCategory;
    private String borrowerType;
    private boolean agreeToTerms;

    // Constructor
    public LoanApplication(String borrowerName, String gender, String loanStartDate,
                           String phoneNumber, String loanAmount, String loanPurpose,
                           String loanDuration, String loanPlan, String loanCategory,
                           String borrowerType, boolean agreeToTerms) {
        this.borrowerName = borrowerName;
        this.gender = gender;
        this.loanStartDate = loanStartDate;
        this.phoneNumber = phoneNumber;
        this.loanAmount = loanAmount;
        this.loanPurpose = loanPurpose;
        this.loanDuration = loanDuration;
        this.loanPlan = loanPlan;
        this.loanCategory = loanCategory;
        this.borrowerType = borrowerType;
        this.agreeToTerms = agreeToTerms;
    }

    // Getters and Setters
    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    // Similar getters and setters for the other fields...
}
