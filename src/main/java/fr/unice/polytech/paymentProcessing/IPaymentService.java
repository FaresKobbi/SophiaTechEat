package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.Order;

public interface IPaymentService {
    public boolean processExternalPayment(Order order);
}
