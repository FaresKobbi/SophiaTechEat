package fr.unice.polytech.services.handlers.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.dishes.*;
import fr.unice.polytech.restaurants.OpeningHours;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;
import fr.unice.polytech.suggestion.HybridSuggestionService;import fr.unice.polytech.restaurants.TimeSlot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicRestaurantHandler implements HttpHandler {
    private final RestaurantManager restaurantManager;
    private final ObjectMapper objectMapper;

    private static final Pattern RESTAURANT_ID_PATTERN = Pattern.compile("/restaurants/([^/]+)/?$");
    private static final Pattern DISHES_COLLECTION_PATTERN = Pattern.compile("/restaurants/([^/]+)/dishes/?$");
    private static final Pattern DISH_ITEM_PATTERN = Pattern.compile("/restaurants/([^/]+)/dishes/([^/]+)/?$");
    private static final Pattern OPENING_HOURS_PATTERN = Pattern.compile("/restaurants/([^/]+)/opening-hours/?$");
    private static final Pattern CAPACITIES_PATTERN = Pattern.compile("/restaurants/([^/]+)/capacities/?$");
    private static final Pattern OPENING_HOURS_ITEM_PATTERN = Pattern
            
            .compile("/restaurants/([^/]+)/opening-hours/([^/]+)/?$");

    private final HybridSuggestionService suggestionService;

    public DynamicRestaurantHandler(RestaurantManager restaurantManager, ObjectMapper objectMapper,
            HybridSuggestionService suggestionService) {
        this.restaurantManager = restaurantManager;
        this.objectMapper = objectMapper;
        this.suggestionService = suggestionService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            Matcher collectionMatcher = DISHES_COLLECTION_PATTERN.matcher(path);
            Matcher restaurantMatcher = RESTAURANT_ID_PATTERN.matcher(path);
            Matcher itemMatcher = DISH_ITEM_PATTERN.matcher(path);
            Matcher openingHoursMatcher = OPENING_HOURS_PATTERN.matcher(path);
            Matcher capacitiesMatcher = CAPACITIES_PATTERN.matcher(path);
            Matcher openingHoursItemMatcher = OPENING_HOURS_ITEM_PATTERN.matcher(path);

            if (collectionMatcher.matches()) {
                String restaurantId = collectionMatcher.group(1);

                if ("GET".equals(method)) {
                    handleGetDishes(exchange, restaurantId);
                } else if ("POST".equals(method)) {
                    handlePostDish(exchange, restaurantId);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            } else if (itemMatcher.matches()) {
                String restaurantId = itemMatcher.group(1);
                String dishId = itemMatcher.group(2);

                if ("PUT".equals(method)) {
                    handlePutDish(exchange, restaurantId, dishId);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            } else if (openingHoursMatcher.matches()) {
                handleOpeningHours(exchange, method, openingHoursMatcher.group(1));
            } else if (openingHoursItemMatcher.matches()) {
                String restaurantId = openingHoursItemMatcher.group(1);
                String day = openingHoursItemMatcher.group(2); 

                if ("DELETE".equals(method)) {
                    handleDeleteOpeningHour(exchange, restaurantId, day);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            } else if (capacitiesMatcher.matches()) {
                handleCapacities(exchange, method, capacitiesMatcher.group(1));
            } else if (path.matches("/restaurants/[^/]+/slots/reserve/?")) {
                String restaurantId = path.split("/")[2];
                if ("POST".equals(method)) {
                    handleReserveSlot(exchange, restaurantId);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            } else if (path.matches("/restaurants/[^/]+/slots/release/?")) {
                String restaurantId = path.split("/")[2];
                if ("POST".equals(method)) {
                    handleReleaseSlot(exchange, restaurantId);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            }
            else if (restaurantMatcher.matches()) {
                String restaurantId = restaurantMatcher.group(1);
                if ("GET".equals(method)) {
                    handleGetRestaurantById(exchange, restaurantId);
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

    private void handleGetRestaurantById(HttpExchange exchange, String restaurantId) throws IOException {
        Optional<Restaurant> rOpt = getRestaurantById(restaurantId);
        if (rOpt.isPresent()) {
            String json = objectMapper.writeValueAsString(rOpt.get());
            sendResponse(exchange, 200, json);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found\"}");
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

            updateDishCommonFields(dish, dishRequest);
            
            dishRequest.dietaryLabels.forEach(label -> {
                restaurant.addDietaryLabel(DietaryLabel.valueOf(label.toUpperCase()));
            });

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

        try {
            DishRequest dishRequest = objectMapper.readValue(exchange.getRequestBody(), DishRequest.class);
            if (!dishRequest.isValid()) {
                sendResponse(exchange, 400, "{\"error\":\"Missing required fields: name, description, price\"}");
                return;
            }
            restaurant.addDish(dishRequest.name, dishRequest.description, dishRequest.price);

            Dish createdDish = restaurant.findDishByName(dishRequest.name);

            updateDishCommonFields(createdDish, dishRequest);
            if (dishRequest.dietaryLabels != null) {
                dishRequest.dietaryLabels.forEach(label -> {
                    restaurant.addDietaryLabel(DietaryLabel.valueOf(label.toUpperCase()));
                });
            }

            String jsonResponse = objectMapper.writeValueAsString(createdDish);
            suggestionService.learnFrom(createdDish);
            sendResponse(exchange, 201, jsonResponse);
        } catch (Exception e) {
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

    private void handleOpeningHours(HttpExchange exchange, String method, String restaurantId) throws IOException {
        Optional<Restaurant> rOpt = getRestaurantById(restaurantId);
        if (rOpt.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found\"}");
            return;
        }
        Restaurant restaurant = rOpt.get();

        if ("GET".equals(method)) {
            sendResponse(exchange, 200, objectMapper.writeValueAsString(restaurant.getOpeningHours()));
        } else if ("POST".equals(method)) {
            InputStream requestBody = exchange.getRequestBody();
            JsonNode body = objectMapper.readTree(requestBody);
            try {
                DayOfWeek day = DayOfWeek.valueOf(body.get("day").asText());
                LocalTime start = LocalTime.parse(body.get("openingTime").asText());
                LocalTime end = LocalTime.parse(body.get("closingTime").asText());

                OpeningHours oh = new OpeningHours(day, start, end);
                restaurant.addOpeningHours(oh);

                sendResponse(exchange, 201, objectMapper.writeValueAsString(oh));
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"Invalid Data: " + e.getMessage() + "\"}");
            }
        }

    }

    private void handleDeleteOpeningHour(HttpExchange exchange, String restaurantId, String openingHourId)
            throws IOException {
        Optional<Restaurant> rOpt = getRestaurantById(restaurantId);

        if (rOpt.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found\"}");
            return;
        }

        Restaurant restaurant = rOpt.get();

        boolean removed = restaurant.getOpeningHours().removeIf(oh -> oh.getId().equals(openingHourId));

        if (removed) {
            sendResponse(exchange, 204, ""); 
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Opening hour not found with ID: " + openingHourId + "\"}");
        }
    }

    private void handleCapacities(HttpExchange exchange, String method, String restaurantId) throws IOException {
        Optional<Restaurant> rOpt = getRestaurantById(restaurantId);
        if (rOpt.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found\"}");
            return;
        }
        Restaurant restaurant = rOpt.get();

        if ("PUT".equals(method)) {
            InputStream requestBody = exchange.getRequestBody();
            JsonNode body = objectMapper.readTree(requestBody);
            try {
                DayOfWeek day = DayOfWeek.valueOf(body.get("day").asText());
                LocalTime start = LocalTime.parse(body.get("start").asText());
                LocalTime end = LocalTime.parse(body.get("end").asText());
                int newCapacity = body.get("capacity").asInt();

                restaurant.updateSlotCapacity(day, start, end, newCapacity);

                sendResponse(exchange, 200, "{\"status\":\"Updated\", \"capacity\":" + newCapacity + "}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"Update failed: " + e.getMessage() + "\"}");
            }
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
            } catch (IllegalArgumentException e) {
                 }
        }

        if (request.dishType != null && !request.dishType.isBlank()) {
            try {
                dish.setDishType(DishType.valueOf(request.dishType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                 }
        }

        if (request.dietaryLabels != null) {
            List<DietaryLabel> labels = new ArrayList<>();
            for (String label : request.dietaryLabels) {
                try {
                    labels.add(DietaryLabel.valueOf(label.toUpperCase()));
                } catch (IllegalArgumentException e) {
                     }
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

    @JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ToppingRequest {
        public String name;
        public Double price;
    }

    private void handleReserveSlot(HttpExchange exchange, String restaurantId) throws IOException {
        Optional<Restaurant> rOpt = getRestaurantById(restaurantId);
        if (rOpt.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found\"}");
            return;
        }
        Restaurant restaurant = rOpt.get();

        try {
            InputStream requestBody = exchange.getRequestBody();
            JsonNode body = objectMapper.readTree(requestBody);

            DayOfWeek day = DayOfWeek.valueOf(body.get("day").asText());
            LocalTime start = LocalTime.parse(body.get("startTime").asText());
            LocalTime end = LocalTime.parse(body.get("endTime").asText());

            TimeSlot slot = new TimeSlot(day, start, end);

            
            System.out.println("--- RESERVE SLOT DEBUG ---");
            System.out.println(
                    "Target Slot: " + slot.getDayOfWeek() + " " + slot.getStartTime() + " - " + slot.getEndTime());
            System.out.println("Target HashCode: " + slot.hashCode());

            int currentCapacity = restaurant.getCapacity(slot);
            System.out.println("Capacity Found: " + currentCapacity);

            if (currentCapacity > 0) {
                restaurant.decreaseCapacity(slot);
                sendResponse(exchange, 200,
                        "{\"status\":\"Reserved\", \"remainingCapacity\":" + (currentCapacity - 1) + "}");
            } else {
                sendResponse(exchange, 409, "{\"error\":\"Slot Full\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Reservation failed: " + e.getMessage() + "\"}");
        }
    }

    private void handleReleaseSlot(HttpExchange exchange, String restaurantId) throws IOException {
        Optional<Restaurant> rOpt = getRestaurantById(restaurantId);
        if (rOpt.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Restaurant Not Found\"}");
            return;
        }
        Restaurant restaurant = rOpt.get();

        try {
            InputStream requestBody = exchange.getRequestBody();
            JsonNode body = objectMapper.readTree(requestBody);

            DayOfWeek day = DayOfWeek.valueOf(body.get("day").asText());
            LocalTime start = LocalTime.parse(body.get("startTime").asText());
            LocalTime end = LocalTime.parse(body.get("endTime").asText());

            TimeSlot slot = new TimeSlot(day, start, end);

            restaurant.increaseCapacity(slot);
            sendResponse(exchange, 200, "{\"status\":\"Released\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Release failed: " + e.getMessage() + "\"}");
        }
    }
}
