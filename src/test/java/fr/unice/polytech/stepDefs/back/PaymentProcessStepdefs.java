package fr.unice.polytech.stepDefs.back;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderStatus;
import fr.unice.polytech.paymentProcessing.IPaymentService;
import fr.unice.polytech.paymentProcessing.PaymentProcessor;
import fr.unice.polytech.users.StudentAccount;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;


import static org.mockito.Mockito.*;


public class PaymentProcessStepdefs {

    private Order pendingOrder;
    private PaymentProcessor paymentProcessor;
    private IPaymentService paymentService;
    private OrderStatus resultingStatus;
    private int expectedPaymentAttempts;

    @Given("a pending order")
    public Order aPendingOrder() {
        StudentAccount studentAccount = new StudentAccount.Builder("Alice", "Smith")
                .email("alice.smith@etu.unice.fr")
                .studentId("22400632")
                .bankInfo("3151 2136 8946 4151", 401, 5, 28)
                .build();

        pendingOrder = new Order.Builder(studentAccount.getStudentID())
                .orderStatus(OrderStatus.PENDING)
                .build();
        return pendingOrder;
    }

    @Given("a payment processor")
    public PaymentProcessor aPaymentProcessor() {
        paymentService = mock(IPaymentService.class);
        paymentProcessor = new PaymentProcessor(pendingOrder, paymentService);
        return paymentProcessor;
    }

    
    @When("the external provider approves first payment request")
    public void approveFirstPaymentRequest() {
        when(paymentService.processExternalPayment(pendingOrder)).thenReturn(true);
        expectedPaymentAttempts = 1;
        resultingStatus = paymentProcessor.processPayment(pendingOrder);
    }

    
    @When("the external provider approves the second payment attempt after rejecting the first one")
    public void approveSecondPaymentAttemptAfterFirstRejection() {
        when(paymentService.processExternalPayment(pendingOrder)).thenReturn(false, true);
        expectedPaymentAttempts = 2;
        resultingStatus = paymentProcessor.processPayment(pendingOrder);
    }

    
    @When("the external provider approves the third payment attempt after rejecting the firsts ones")
    public void approveThirdPaymentAttemptAfterFirstOnesRejection() {
        when(paymentService.processExternalPayment(pendingOrder)).thenReturn(false, false, true);
        expectedPaymentAttempts = 3;
        resultingStatus = paymentProcessor.processPayment(pendingOrder);
    }

    
    @When("the external provider reject the third payment attempt after rejecting the firsts ones")
    public void rejectThirdPaymentAttemptAfterFirstOnesRejection() {
        when(paymentService.processExternalPayment(pendingOrder)).thenReturn(false, false, false);
        expectedPaymentAttempts = 3;
        resultingStatus = paymentProcessor.processPayment(pendingOrder);
    }

    
    @Then("the payment processor validate the payment")
    public void thePaymentProcessorValidatesThePayment() {
        assertPaymentValidated();
    }

    
    @Then("the payment processor cancels the payment")
    public void thePaymentProcessorCancelsThePayment() {
        Assertions.assertEquals(OrderStatus.CANCELED, resultingStatus);
        verify(paymentService, times(expectedPaymentAttempts)).processExternalPayment(pendingOrder);
    }

    private void assertPaymentValidated() {
        Assertions.assertEquals(OrderStatus.VALIDATED, resultingStatus);
        verify(paymentService, times(expectedPaymentAttempts)).processExternalPayment(pendingOrder);
    }
}