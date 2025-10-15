package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;

public interface IPaymentService {
    public boolean processPayment(Order order);
}
