package fr.unice.polytech.services.handlers.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;
import fr.unice.polytech.users.StudentAccountManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DynamicAccountsHandlerTest {

    @Mock
    private StudentAccountManager accountManager;
    @Mock
    private HttpExchange exchange;
    @Mock
    private StudentAccount studentAccount;

    private DynamicAccountsHandler handler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        handler = new DynamicAccountsHandler(accountManager, objectMapper);
    }

    @Test
    void testGetDeliveryLocations() throws IOException {
        String studentId = "student123";
        DeliveryLocation loc1 = new DeliveryLocation("Home", "123 Main St", "City", "12345");
        DeliveryLocation loc2 = new DeliveryLocation("Campus", "456 College Rd", "City", "12345");
        List<DeliveryLocation> locations = Arrays.asList(loc1, loc2);

        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/accounts/" + studentId + "/locations"));
        when(accountManager.findAccountById(studentId)).thenReturn(Optional.of(studentAccount));
        when(studentAccount.getDeliveryLocations()).thenReturn(locations);

        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        String response = outputStream.toString();
        assertTrue(response.contains("Home"));
        assertTrue(response.contains("Campus"));
    }

    @Test
    void testGetDeliveryLocationsNotFound() throws IOException {
        String studentId = "unknown";

        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/accounts/" + studentId + "/locations"));
        when(accountManager.findAccountById(studentId)).thenReturn(Optional.empty());

        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(404), anyLong());
    }
}
