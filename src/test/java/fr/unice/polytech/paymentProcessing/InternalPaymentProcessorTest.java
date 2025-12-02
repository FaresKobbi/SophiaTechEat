
package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderStatus;
import fr.unice.polytech.users.StudentAccount;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.unice.polytech.paymentProcessing.clients.StudentAccountClient;
import static org.mockito.Mockito.*;

public class InternalPaymentProcessorTest {
    InternalPaymentProcessor processor;
    StudentAccountClient mockClient;

    @Test
    void testProcessPayment_Success() {
        StudentAccount client = new StudentAccount.Builder("Alice", "Smith").balance(100.0).studentId("student1")
                .build();
        Order order = new Order.Builder(client.getStudentID()).amount(50.0).build();

        mockClient = mock(StudentAccountClient.class);
        when(mockClient.debit("student1", 50.0)).thenReturn(true);

        processor = new InternalPaymentProcessor(mockClient);
        OrderStatus status = processor.processPayment(order);

        assertEquals(OrderStatus.VALIDATED, status);
        verify(mockClient).debit("student1", 50.0);
    }

    @Test
    void testProcessPayment_Failure_InsufficientFunds() {
        StudentAccount client = new StudentAccount.Builder("Bob", "Brown").balance(30.0).studentId("student2").build();
        Order order = new Order.Builder(client.getStudentID()).amount(50.0).build();

        mockClient = mock(StudentAccountClient.class);
        when(mockClient.debit("student2", 50.0)).thenReturn(false);

        processor = new InternalPaymentProcessor(mockClient);
        OrderStatus status = processor.processPayment(order);

        assertEquals(OrderStatus.CANCELED, status);
        verify(mockClient).debit("student2", 50.0);
    }

}