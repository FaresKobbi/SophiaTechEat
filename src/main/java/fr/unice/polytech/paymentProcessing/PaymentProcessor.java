package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.Order;
import fr.unice.polytech.OrderStatus;

public class PaymentProcessor {

    Order order;

    public PaymentProcessor(Order order) {
        this.order = order;
    }

    public OrderStatus processPayment(Order order){
        return new PaymentService().processExternalPayment(order) ? OrderStatus.VALIDATED : OrderStatus.CANCELED;
    }

}
