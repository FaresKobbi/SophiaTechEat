package fr.unice.polytech.paymentProcessing.clients;

public interface StudentAccountClient {
    boolean debit(String studentId, double amount);

}
