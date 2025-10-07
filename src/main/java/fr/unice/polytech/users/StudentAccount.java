package fr.unice.polytech.users; // Assuming this package

// Note: Requires UserAccount class from above.
public class StudentAccount extends UserAccount {
    
    // Attributes from Class Diagram:
    private String studentID;
    private double balance; // Represents the student credit balance

    /**
     * Constructor for StudentAccount.
     */
    public StudentAccount(String name, String surname, String email, double initialBalance) {
        super(name, surname, email); // Initialize attributes from UserAccount
        this.balance = initialBalance;
    }
    
    public String getStudentID() {
        return studentID;
    }

    public double getBalance() {
        return balance;
    }


    
   
}