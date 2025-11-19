package fr.unice.polytech.services.handlers.restaurant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class StaticRestaurantHandler implements HttpHandler {
    private final RestaurantManager restaurantManager;
    private final ObjectMapper objectMapper ;

    public StaticRestaurantHandler(RestaurantManager restaurantManager, ObjectMapper objectMapper) {
        this.restaurantManager = restaurantManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {

            if ("GET".equals(exchange.getRequestMethod())) {
                List<Restaurant> restaurants = restaurantManager.getAllRestaurants();
                String jsonResponse = objectMapper.writeValueAsString(restaurants);
                sendResponse(exchange, 200, jsonResponse);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                createRestaurant(exchange);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error\"}");
        }
    }


    private void createRestaurant(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        JsonNode body = objectMapper.readTree(requestBody);

        String restaurantName = body.get("restaurantName").asText();

        if (restaurantName == null || restaurantName.trim().isEmpty()){
            sendResponse(exchange,400,"{\"error\":\"Missing required fields: name\"}");
            return;
        }
        Restaurant newRestaurant = new Restaurant(restaurantName.trim());
        restaurantManager.addRestaurant(newRestaurant);
        System.out.println("StudentAccountService: Created new student " + newRestaurant.getRestaurantId());

        String jsonResponse = objectMapper.writeValueAsString(newRestaurant);

        sendResponse(exchange, 201, jsonResponse);
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}

