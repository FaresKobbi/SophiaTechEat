package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.paymentProcessing.clients.HttpStudentAccountClient;
import fr.unice.polytech.paymentProcessing.clients.StudentAccountClient;

import java.net.http.HttpClient;

public class PaymentProcessorFactory {

    private final IPaymentService externalPaymentService;
    private final HttpClient httpClient;

    public PaymentProcessorFactory(HttpClient httpClient) {
        this(new PaymentService(httpClient), httpClient);
    }

    public PaymentProcessorFactory(IPaymentService externalPaymentService, HttpClient httpClient) {
        this.externalPaymentService = externalPaymentService;
        this.httpClient = httpClient;
    }

    public IPaymentProcessor createProcessor(Order order, PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Unsupported payment method: null");
        }
        return switch (paymentMethod) {
            case EXTERNAL -> new PaymentProcessor(order, externalPaymentService);
            case INTERNAL -> {
                StudentAccountClient accountClient = new HttpStudentAccountClient(httpClient);
                yield new InternalPaymentProcessor(accountClient);
            }
        };
    }
}