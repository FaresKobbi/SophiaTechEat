package fr.unice.polytech.APIGateWay;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
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
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PassthroughHandlerTest {

    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpExchange exchange;
    @Mock
    private HttpResponse<byte[]> mockResponse;

    private PassthroughHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new PassthroughHandler(httpClient);
    }

    @Test
    void testHandleForwardRequest() throws IOException, InterruptedException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/api/restaurants/123"));
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(new byte[0]));

        Headers requestHeaders = new Headers();
        when(exchange.getRequestHeaders()).thenReturn(requestHeaders);

        Headers responseHeaders = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(responseHeaders);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        // Mock HttpClient response
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("Success".getBytes(StandardCharsets.UTF_8));

        // Mock HttpHeaders for response
        HttpHeaders mockHttpHeaders = HttpHeaders.of(Collections.emptyMap(), (k, v) -> true);
        when(mockResponse.headers()).thenReturn(mockHttpHeaders);

        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<byte[]>>any()))
                .thenReturn(mockResponse);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void testHandleNotFound() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/unknown/path"));

        Headers responseHeaders = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(responseHeaders);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(404), anyLong());
        verifyNoInteractions(httpClient);
    }
}
