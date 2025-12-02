package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private PaymentService paymentService;
    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        paymentService = new PaymentService(mockHttpClient);
    }

    @Test
    void testProcessExternalPaymentSuccess() throws IOException, InterruptedException {
        
        Order mockOrder = mock(Order.class);
        when(mockOrder.getStudentId()).thenReturn("12345");

        
        String jsonBankInfo = "{\"cardNumber\":\"1234567890123456\",\"cvv\":123,\"month\":12,\"year\":2026}";
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonBankInfo);
        when(mockHttpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(mockResponse);

        
        boolean result = paymentService.processExternalPayment(mockOrder);

        
        assertTrue(result);
    }

    @Test
    void testProcessExternalPaymentFailureApiError() throws IOException, InterruptedException {
        
        Order mockOrder = mock(Order.class);
        when(mockOrder.getStudentId()).thenReturn("12345");

        
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(mockResponse);

        
        boolean result = paymentService.processExternalPayment(mockOrder);

        
        assertFalse(result);
    }

    @Test
    void testProcessExternalPaymentFailureException() throws IOException, InterruptedException {
        
        Order mockOrder = mock(Order.class);
        when(mockOrder.getStudentId()).thenReturn("12345");

        
        when(mockHttpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(new IOException("Network error"));

        
        boolean result = paymentService.processExternalPayment(mockOrder);

        
        assertFalse(result);
    }
}
