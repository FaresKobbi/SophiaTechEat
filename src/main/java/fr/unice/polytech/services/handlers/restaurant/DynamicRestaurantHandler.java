package fr.unice.polytech.services.handlers.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class DynamicRestaurantHandler implements HttpHandler {
    private final RestaurantManager restaurantManager;
    private final ObjectMapper objectMapper;
    private static final String RESTAURANTS_PATH = "/restaurants/";


    public DynamicRestaurantHandler(RestaurantManager restaurantManager, ObjectMapper objectMapper) {
        this.restaurantManager = restaurantManager;
        this.objectMapper = objectMapper;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (!"GET".equals(method)) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            if (path.endsWith("/dishes")) {
                handleGetDishes(exchange, path);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Dynamic Route Not Found for: " + path + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }


    private void handleGetDishes(HttpExchange exchange, String path) throws IOException {
        try {
            int start = path.indexOf(RESTAURANTS_PATH) + RESTAURANTS_PATH.length();
            int end = path.indexOf("/dishes", start);

            if (start == -1 || end == -1 || start >= end) {
                sendResponse(exchange, 400, "{\"error\":\"Bad Request: Missing restaurant ID in path\"}");
                return;
            }

            String restaurantId = path.substring(start, end);

            Optional<Restaurant> restaurantOptional = restaurantManager.getAllRestaurants().stream()
                    .filter(r -> r.getRestaurantId().equals(restaurantId))
                    .findFirst();

            if (restaurantOptional.isEmpty()) {
                sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found: " + restaurantId + "\"}");
                return;
            }

            Restaurant restaurant = restaurantOptional.get();

            String jsonResponse = objectMapper.writeValueAsString(restaurant.getDishes());
            sendResponse(exchange, 200, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Failed to process dish request\"}");
        }
    }
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
