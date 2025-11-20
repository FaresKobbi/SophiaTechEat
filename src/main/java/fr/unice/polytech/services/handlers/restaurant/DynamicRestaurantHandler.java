package fr.unice.polytech.services.handlers.restaurant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.dishes.*;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
            if (path.endsWith("/dishes") && "GET".equals(method)) {
                handleGetDishes(exchange, path);
            } else if (path.endsWith("/dishes")&& "POST".equals(method)) {
                handlePostDish(exchange, path);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Dynamic Route Not Found for: " + path + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }

    private void handlePostDish(HttpExchange exchange, String path) throws IOException {
        int start = path.indexOf(RESTAURANTS_PATH) + RESTAURANTS_PATH.length();
        int end = path.indexOf("/dishes", start);

        if (start == -1 || end == -1 || start >= end) {
            sendResponse(exchange, 400, "{\"error\":\"Bad Request: Missing restaurant ID in path\"}");
            return;
        }

        String restaurantId = path.substring(start, end);

        Optional<Restaurant> restaurantOptional = getRestaurantById(restaurantId);

        if (restaurantOptional.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found: " + restaurantId + "\"}");
            return;
        }

        Restaurant restaurant = restaurantOptional.get();


        try{
            DishRequest dishRequest = objectMapper.readValue(exchange.getRequestBody(), DishRequest.class);
            if (!dishRequest.isValid()) {
                sendResponse(exchange, 400, "{\"error\":\"Missing required fields: name, description, price\"}");
                return;
            }
            restaurant.addDish(dishRequest.name, dishRequest.description, dishRequest.price);

            Dish createdDish =restaurant.findDishByName(dishRequest.name);

            if (dishRequest.category != null && !dishRequest.category.isBlank()) {
                try {
                    createdDish.setCategory(DishCategory.valueOf(dishRequest.category.toUpperCase()));
                } catch (IllegalArgumentException e) { /* Ignore invalid categories */ }
            }

            if (dishRequest.dishType != null && !dishRequest.dishType.isBlank()) {
                try {
                    createdDish.setDishType(DishType.valueOf(dishRequest.dishType.toUpperCase()));
                } catch (IllegalArgumentException e) { /* Ignore invalid types */ }
            }

            if (dishRequest.dietaryLabels != null) {
                List<DietaryLabel> labels = new ArrayList<>();
                for (String label : dishRequest.dietaryLabels) {
                    try {
                        labels.add(DietaryLabel.valueOf(label.toUpperCase()));
                    } catch (IllegalArgumentException e) { /* Ignore invalid labels */ }
                }
                createdDish.setDietaryLabels(labels);
            }

            if (dishRequest.toppings != null) {
                for (ToppingRequest t : dishRequest.toppings) {
                    if (t.name != null && t.price != null) {
                        createdDish.addTopping(new Topping(t.name, t.price));
                    }
                }
            }


            String jsonResponse = objectMapper.writeValueAsString(createdDish);
            sendResponse(exchange,201,jsonResponse);
        }catch (Exception e){
            e.printStackTrace(); // Log the error (e.g. JSON syntax error)
            sendResponse(exchange, 400, "{\"error\":\"Invalid JSON format\"}");
        }



    }

    private Optional<Restaurant> getRestaurantById(String restaurantId) {
        return restaurantManager.getAllRestaurants().stream()
                .filter(r -> r.getRestaurantId().equals(restaurantId))
                .findFirst();
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

            Optional<Restaurant> restaurantOptional = getRestaurantById(restaurantId);

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


    // Inner class to model the JSON request body
    private static class DishRequest {
        public String name;
        public String description;
        public Double price; // Use Wrapper to detect nulls easily
        public String category;
        public String dishType;
        public List<String> dietaryLabels;
        public List<ToppingRequest> toppings;

        public boolean isValid() {
            return name != null && !name.isBlank()
                    && description != null && !description.isBlank()
                    && price != null;
        }
    }

    // Inner class for Toppings inside the request
    private static class ToppingRequest {
        public String name;
        public Double price;
    }
}
