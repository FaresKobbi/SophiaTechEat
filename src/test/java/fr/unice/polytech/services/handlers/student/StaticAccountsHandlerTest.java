package fr.unice.polytech.services.handlers.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.users.StudentAccount;
import fr.unice.polytech.users.StudentAccountManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class StaticAccountsHandlerTest {

    @Mock
    private StudentAccountManager accountManager;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HttpExchange exchange;

    private StaticAccountsHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new StaticAccountsHandler(accountManager, objectMapper);
    }

    @Test
    void testHandleGetAccounts() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/accounts"));
        when(accountManager.getAllAccounts()).thenReturn(Collections.emptyList());
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        Headers headers = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(accountManager).getAllAccounts();
    }

    @Test
    void testHandlePostAccount() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        
        
        
        
    }
}
