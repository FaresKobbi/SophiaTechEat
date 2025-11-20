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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicRestaurantHandler implements HttpHandler {
    private final RestaurantManager restaurantManager;
    private final ObjectMapper objectMapper;
    private static final String RESTAURANTS_PATH = "/restaurants/";

    // Matches: /restaurants/{restaurantId}/dishes
    private static final Pattern DISHES_COLLECTION_PATTERN = Pattern.compile("/restaurants/([^/]+)/dishes/?$");
    // Matches: /restaurants/{restaurantId}/dishes/{dishId}
    private static final Pattern DISH_ITEM_PATTERN = Pattern.compile("/restaurants/([^/]+)/dishes/([^/]+)/?$");

    public DynamicRestaurantHandler(RestaurantManager restaurantManager, ObjectMapper objectMapper) {
        this.restaurantManager = restaurantManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            Matcher collectionMatcher = DISHES_COLLECTION_PATTERN.matcher(path);
            Matcher itemMatcher = DISH_ITEM_PATTERN.matcher(path);

            if (collectionMatcher.matches()) {
                String restaurantId = collectionMatcher.group(1);

                if ("GET".equals(method)) {
                    handleGetDishes(exchange, restaurantId);
                } else if ("POST".equals(method)) {
                    handlePostDish(exchange, restaurantId);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            }
            else if (itemMatcher.matches()) {
                String restaurantId = itemMatcher.group(1);
                String dishId = itemMatcher.group(2);

                if ("PUT".equals(method)) {
                    handlePutDish(exchange, restaurantId, dishId);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            }
            else {
                sendResponse(exchange, 404, "{\"error\":\"Dynamic Route Not Found for: " + path + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }

    private void handlePutDish(HttpExchange exchange, String restaurantId, String dishId) throws IOException {
        Optional<Restaurant> restaurantOptional = getRestaurantById(restaurantId);

        if (restaurantOptional.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found: " + restaurantId + "\"}");
            return;
        }

        Restaurant restaurant = restaurantOptional.get();

        Dish dish = restaurant.getDishes().stream()
                .filter(d -> d.getId().equals(dishId))
                .findFirst()
                .orElse(null);

        if (dish == null) {
            sendResponse(exchange, 404, "{\"error\":\"Dish Not Found: " + dishId + "\"}");
            return;
        }

        try {
            DishRequest dishRequest = objectMapper.readValue(exchange.getRequestBody(), DishRequest.class);

            if (dishRequest.name != null && !dishRequest.name.isBlank()) {
                dish.setName(dishRequest.name);
            }
            if (dishRequest.description != null && !dishRequest.description.isBlank()) {
                dish.setDescription(dishRequest.description);
            }
            if (dishRequest.price != null) {
                dish.setPrice(dishRequest.price);
            }

            updateDishCommonFields(dish,dishRequest);

            String jsonResponse = objectMapper.writeValueAsString(dish);
            sendResponse(exchange, 200, jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Invalid JSON format\"}");
        }
    }
    private void handlePostDish(HttpExchange exchange, String restaurantId) throws IOException {
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

            updateDishCommonFields(createdDish,dishRequest);


            String jsonResponse = objectMapper.writeValueAsString(createdDish);
            sendResponse(exchange,201,jsonResponse);
        }catch (Exception e){
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Invalid JSON format\"}");
        }



    }

    private Optional<Restaurant> getRestaurantById(String restaurantId) {
        return restaurantManager.getAllRestaurants().stream()
                .filter(r -> r.getRestaurantId().equals(restaurantId))
                .findFirst();
    }
    private void handleGetDishes(HttpExchange exchange, String restaurantId) throws IOException {
        try {
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
    private void updateDishCommonFields(Dish dish, DishRequest request) {
        if (request.category != null && !request.category.isBlank()) {
            try {
                dish.setCategory(DishCategory.valueOf(request.category.toUpperCase()));
            } catch (IllegalArgumentException e) { /* Ignore invalid categories */ }
        }

        if (request.dishType != null && !request.dishType.isBlank()) {
            try {
                dish.setDishType(DishType.valueOf(request.dishType.toUpperCase()));
            } catch (IllegalArgumentException e) { /* Ignore invalid types */ }
        }

        if (request.dietaryLabels != null) {
            List<DietaryLabel> labels = new ArrayList<>();
            for (String label : request.dietaryLabels) {
                try {
                    labels.add(DietaryLabel.valueOf(label.toUpperCase()));
                } catch (IllegalArgumentException e) { /* Ignore invalid labels */ }
            }
            dish.setDietaryLabels(labels);
        }

        if (request.toppings != null) {
            dish.getToppings().clear();
            for (ToppingRequest t : request.toppings) {
                if (t.name != null && t.price != null) {
                    dish.addTopping(new Topping(t.name, t.price));
                }
            }
        }
    }


    private static class DishRequest {
        public String name;
        public String description;
        public Double price;
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
