package fr.unice.polytech.services.handlers.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;
import fr.unice.polytech.restaurants.TimeSlot;
import fr.unice.polytech.suggestion.HybridSuggestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RestaurantHandlersTest {

    @Mock
    private RestaurantManager restaurantManager;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HybridSuggestionService suggestionService;
    @Mock
    private HttpExchange exchange;

    private StaticRestaurantHandler staticHandler;
    private DynamicRestaurantHandler dynamicHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        staticHandler = new StaticRestaurantHandler(restaurantManager, objectMapper);
        dynamicHandler = new DynamicRestaurantHandler(restaurantManager, objectMapper, suggestionService);
    }

    @Test
    void testStaticGetRestaurants() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/restaurants"));
        when(restaurantManager.search(any(), any())).thenReturn(Collections.emptyList());
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        Headers headers = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        staticHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(restaurantManager).search(null, Collections.emptyList());
    }

    @Test
    void testDynamicGetDishes() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/restaurants/rest123/dishes"));

        Restaurant mockRestaurant = mock(Restaurant.class);
        when(mockRestaurant.getDishes()).thenReturn(Collections.emptyList());
        
        
        
        when(restaurantManager.getAllRestaurants()).thenReturn(List.of(mockRestaurant));
        when(mockRestaurant.getRestaurantId()).thenReturn("rest123");

        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        Headers headers = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        dynamicHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void testDynamicReserveSlot() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestURI()).thenReturn(URI.create("/restaurants/rest123/slots/reserve"));

        String jsonRequest = "{\"day\":\"MONDAY\", \"startTime\":\"12:00\", \"endTime\":\"13:00\"}";
        when(exchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(jsonRequest.getBytes(StandardCharsets.UTF_8)));

        
        ObjectMapper realMapper = new ObjectMapper();
        realMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        dynamicHandler = new DynamicRestaurantHandler(restaurantManager, realMapper, suggestionService);

        Restaurant mockRestaurant = mock(Restaurant.class);
        when(restaurantManager.getAllRestaurants()).thenReturn(List.of(mockRestaurant));
        when(mockRestaurant.getRestaurantId()).thenReturn("rest123");
        when(mockRestaurant.getCapacity(any(TimeSlot.class))).thenReturn(10);

        Headers headers = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        dynamicHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(mockRestaurant).decreaseCapacity(any(TimeSlot.class));
    }
}
