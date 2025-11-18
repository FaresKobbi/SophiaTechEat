package fr.unice.polytech.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;
import fr.unice.polytech.services.handlers.DynamicAccountsHandler;
import fr.unice.polytech.services.handlers.restaurant.DynamicRestaurantHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

public class RestaurantService {
    private static final RestaurantManager restaurantManager = new RestaurantManager();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        // MOCK
        restaurantManager.addRestaurant(new Restaurant("Pizza Palace"));
        restaurantManager.addRestaurant(new Restaurant("Sushi Shop"));
        restaurantManager.getRestaurant("Sushi Shop").addDish("California Roll", "Fresh sushi roll with crab, avocado, and cucumber", 8.99);
        restaurantManager.getRestaurant("Pizza Palace").addDish("Margherita Pizza", "Classic pizza with tomato sauce, mozzarella, and basil", 12.50);

        int port = 8081;
        SimpleServer server = new SimpleServer(port);

        ApiRegistry registry = new ApiRegistry();

        registry.register("GET", "/restaurants", new RestaurantsHandler());

        registry.registerFallback(new DynamicRestaurantHandler(restaurantManager,objectMapper));
        server.start(registry);
        System.out.println("RestaurantService started on port " + port);
        System.out.println("Serving STATIC routes:  GET /restaurants");
        System.out.println("Serving DYNAMIC routes: GET /restaurants/{restaurantId}/dishes");
        System.out.println("Sushi ID:"+restaurantManager.getRestaurant("Sushi Shop").getRestaurantId());
        System.out.println("Pizza ID:"+restaurantManager.getRestaurant("Pizza Palace").getRestaurantId());
    }

    /**
     * Handles GET /restaurants
     */
    static class RestaurantsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {

                if ("GET".equals(exchange.getRequestMethod())) {
                    List<Restaurant> restaurants = restaurantManager.getAllRestaurants();
                    String jsonResponse = objectMapper.writeValueAsString(restaurants);
                    sendResponse(exchange, 200, jsonResponse);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "{\"error\":\"Internal Server Error\"}");
            }
        }
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
