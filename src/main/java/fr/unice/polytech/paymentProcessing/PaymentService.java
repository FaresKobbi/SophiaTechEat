package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;

public class PaymentService implements IPaymentService{
    @Override
    public boolean processPayment(Order order) {
        return new MockedExternalPaymentSystem(order).mockedCheckingInformation();
    }
}
