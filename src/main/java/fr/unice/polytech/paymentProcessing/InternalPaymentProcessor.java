package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderStatus;
import fr.unice.polytech.paymentProcessing.clients.StudentAccountClient;
import fr.unice.polytech.users.StudentAccount;

public class InternalPaymentProcessor implements IPaymentProcessor {
    private final StudentAccountClient accountClient;

    public InternalPaymentProcessor(StudentAccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @Override
    public OrderStatus processPayment(Order order) {
        String studentId = order.getStudentId();
        double amount = order.getAmount();

        System.out.println("InternalPaymentProcessor: Requesting debit for student " + studentId);

        boolean success = accountClient.debit(studentId, amount);

        if (success) {
            return OrderStatus.VALIDATED;
        } else {
            return OrderStatus.CANCELED;
        }
    }




}
