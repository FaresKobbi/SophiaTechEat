package fr.unice.polytech.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;
import fr.unice.polytech.services.handlers.restaurant.StaticRestaurantHandler;

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

        registry.register("GET", "/restaurants", new StaticRestaurantHandler(restaurantManager, objectMapper));
        registry.register("POST", "/restaurants", new StaticRestaurantHandler(restaurantManager, objectMapper));

        server.start(registry);
        System.out.println("RestaurantService started on port " + port);
        System.out.println("Now serving exact route: GET /restaurants");
        System.out.println("Now serving exact route: POST /restaurants");
    }


}
