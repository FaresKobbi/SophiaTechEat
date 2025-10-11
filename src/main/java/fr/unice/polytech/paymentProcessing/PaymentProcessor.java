package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.Order;
import fr.unice.polytech.OrderStatus;

public class PaymentProcessor {

    private final Order order;
    private final IPaymentService paymentService;

    public PaymentProcessor(Order order) {
        this(order, new PaymentService());
    }

    public PaymentProcessor(Order order, IPaymentService paymentService) {
        this.order = order;
        this.paymentService = paymentService;
    }



    public OrderStatus processPayment() {
        return processPayment(order);
    }

    public OrderStatus processPayment(Order order){
        boolean paymentSuccessful = paymentService.processExternalPayment(order);
        return paymentSuccessful ? OrderStatus.VALIDATED : OrderStatus.CANCELED;
    }

}
