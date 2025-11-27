package fr.unice.polytech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderManager;
import fr.unice.polytech.orderManagement.OrderStatus;
import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;
import fr.unice.polytech.services.handlers.order.OrderHandler;
import fr.unice.polytech.users.DeliveryLocation;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

public class OrderService {
    public static void main(String[] args) throws IOException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        OrderManager orderManager = new OrderManager(httpClient);
        ObjectMapper objectMapper = new ObjectMapper();

        Dish pizza = new Dish("Margherita", "Cheese and Tomato", 12.50);

        DeliveryLocation loc = new DeliveryLocation("Home", "123 Main St", "Nice", "06000");

        String demoRestaurantId = "bf568b52-b860-4808-a78b-a4de6d507885";

        orderManager.createOrder(List.of(pizza), "9af8b7c0-a7fd-4297-94d3-f7e96746ca93", loc, demoRestaurantId);

        List<Order> pending = orderManager.getPendingOrders();
        if(!pending.isEmpty()) {
            Order o = pending.get(0);
            o.setOrderStatus(OrderStatus.VALIDATED);
            System.out.println("OrderService: Created mock VALIDATED order " + o.getOrderId() + " for restaurant " + demoRestaurantId);
        }

        int port = 8083;
        SimpleServer server = new SimpleServer(port);

        ApiRegistry registry = new ApiRegistry();

        registry.registerFallback(new OrderHandler(orderManager,objectMapper, httpClient));
        server.start(registry);
        System.out.println("OrderService started on port " + port);

    }
}
