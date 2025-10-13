package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orders.*;

public class PaymentProcessor {

    Order order;

    public PaymentProcessor(Order order) {
        this.order = order;
    }

    public OrderStatus processPayment(Order order){
        return new PaymentService().processExternalPayment(order) ? OrderStatus.VALIDATED : OrderStatus.CANCELED;
    }

}
