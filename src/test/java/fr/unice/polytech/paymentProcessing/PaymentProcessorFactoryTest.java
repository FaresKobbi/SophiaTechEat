
package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderStatus;
import fr.unice.polytech.users.StudentAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.http.HttpClient;

class PaymentProcessorFactoryTest {
        private StudentAccount student;
        private Order order;

        @BeforeEach
        void setUp() {
                student = new StudentAccount.Builder("Jordan", "Smith")
                                .email("jordan.smith@etu.unice.fr")
                                .studentId("ID-42")
                                .bankInfo("1234 5678 9012 3456", 123, 12, 30)
                                .build();
                order = new Order.Builder(student.getStudentID())
                                .amount(18.5)
                                .build();
        }

        @Test
        void createProcessorForExternalPaymentUsesProvidedPaymentService() {
                IPaymentService paymentService = mock(IPaymentService.class);
                when(paymentService.processExternalPayment(order)).thenReturn(true);
                PaymentProcessorFactory factory = new PaymentProcessorFactory(paymentService, mock(HttpClient.class));

                IPaymentProcessor processor = factory.createProcessor(order, PaymentMethod.EXTERNAL);

                assertAll(
                                () -> assertTrue(processor instanceof PaymentProcessor,
                                                "Expected external payments to use PaymentProcessor"),
                                () -> assertEquals(OrderStatus.VALIDATED, processor.processPayment(order),
                                                "The payment status should reflect the service response"));
                verify(paymentService).processExternalPayment(order);
        }

        @Test
        void createProcessorForInternalPaymentReturnsInternalProcessor() {
                PaymentProcessorFactory factory = new PaymentProcessorFactory(mock(IPaymentService.class),
                                mock(HttpClient.class));

                IPaymentProcessor processor = factory.createProcessor(order, PaymentMethod.INTERNAL);

                assertTrue(processor instanceof InternalPaymentProcessor,
                                "Internal payments must use the dedicated processor");
                // Note: We cannot easily test the debit logic here without mocking the
                // HttpClient or StudentAccountClient
                // inside the factory/processor, but we verified the type.
        }

        @Test
        void createProcessorWithUnsupportedMethodThrowsException() {
                PaymentProcessorFactory factory = new PaymentProcessorFactory(mock(IPaymentService.class),
                                mock(HttpClient.class));

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                () -> factory.createProcessor(order, null));

                assertTrue(exception.getMessage().contains("Unsupported payment method"));
        }

}