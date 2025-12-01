package fr.unice.polytech.services.handlers.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderManager;
import fr.unice.polytech.orderManagement.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderHandlerTest {

    @Mock
    private OrderManager orderManager;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpExchange exchange;
    @Mock
    private HttpResponse<String> mockResponse;

    private OrderHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new OrderHandler(orderManager, objectMapper, httpClient);
    }

    @Test
    void testHandleGetOrdersForRestaurant() throws IOException, InterruptedException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/orders/restaurant/rest123"));

        Order mockOrder = mock(Order.class);
        when(mockOrder.getStudentId()).thenReturn("student123");
        when(mockOrder.getOrderStatus()).thenReturn(OrderStatus.VALIDATED);
        when(orderManager.getValidatedOrdersForRestaurant("rest123")).thenReturn(List.of(mockOrder));

        // Mock HttpClient for fetchStudentName
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("\"John Doe\"");
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(mockResponse);

        when(objectMapper.writeValueAsString(any())).thenReturn("[{...}]");
        when(objectMapper.readValue(anyString(), eq(String.class))).thenReturn("John Doe");

        Headers headers = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(orderManager).getValidatedOrdersForRestaurant("rest123");
    }

    @Test
    void testHandleCreateOrderSuccess() throws IOException, InterruptedException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestURI()).thenReturn(URI.create("/orders"));

        String jsonRequest = "{"
                + "\"restaurantId\":\"rest123\", "
                + "\"studentId\":\"student123\", "
                + "\"dishes\":[], "
                + "\"paymentMethod\":\"EXTERNAL\", "
                + "\"timeSlot\":{\"day\":\"MONDAY\",\"startTime\":\"12:00\",\"endTime\":\"13:00\"}, "
                + "\"deliveryLocation\":{\"name\":\"Home\",\"address\":\"123 St\",\"city\":\"Nice\",\"zipCode\":\"06000\"}"
                + "}";
        when(exchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(jsonRequest.getBytes(StandardCharsets.UTF_8)));

        // Use real ObjectMapper
        ObjectMapper realMapper = new ObjectMapper();
        realMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        handler = new OrderHandler(orderManager, realMapper, httpClient);

        // Mock HttpClient for reserveSlot AND fetchStudentName
        when(mockResponse.statusCode()).thenReturn(200);
        // fetchStudentName expects a JSON string of the name
        when(mockResponse.body()).thenReturn("\"John Doe\"");
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(mockResponse);

        // Mock OrderManager
        Order mockOrder = mock(Order.class);
        when(mockOrder.getOrderStatus()).thenReturn(OrderStatus.VALIDATED);
        when(mockOrder.getStudentId()).thenReturn("student123");
        when(orderManager.createOrder(any(), anyString(), any(), anyString())).thenReturn(mockOrder);

        Headers headers = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(201), anyLong());
        verify(orderManager).createOrder(any(), eq("student123"), any(), eq("rest123"));
        verify(orderManager).initiatePayment(eq(mockOrder), any());
    }
}
