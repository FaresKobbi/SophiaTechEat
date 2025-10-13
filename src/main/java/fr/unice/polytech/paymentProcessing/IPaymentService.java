package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orders.*;

public interface IPaymentService {
    public boolean processExternalPayment(Order order);
}
