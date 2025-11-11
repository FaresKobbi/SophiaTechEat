package fr.unice.polytech.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;

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

        int port = 8081;
        SimpleServer server = new SimpleServer(port);

        ApiRegistry registry = new ApiRegistry();

        registry.register("GET", "/restaurants", new RestaurantsHandler());

        server.start(registry);
        System.out.println("RestaurantService started on port " + port);
        System.out.println("Now serving exact route: GET /restaurants");
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
