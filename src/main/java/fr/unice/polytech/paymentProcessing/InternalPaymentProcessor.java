package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.users.StudentAccount;

public class InternalPaymentProcessor implements IPaymentService {

    @Override
    public boolean processPayment(Order order) {
        StudentAccount client = order.getStudentAccount();
        double orderTotal = order.getAmount();
            return client.debit(orderTotal);
        }


}
