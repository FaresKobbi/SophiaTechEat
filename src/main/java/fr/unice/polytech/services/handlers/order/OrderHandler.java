package fr.unice.polytech.services.handlers.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderManager;
import fr.unice.polytech.users.DeliveryLocation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OrderHandler implements HttpHandler {
    private final OrderManager orderManager;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private static final String API_GATEWAY_ACCOUNT_URL = "http://localhost:8080/api/accounts/";

    private static final Pattern ORDERS_BY_RESTAURANT_PATTERN = Pattern.compile("/orders/restaurant/([^/]+)/?$");

    public OrderHandler(OrderManager orderManager, ObjectMapper objectMapper, HttpClient httpClient) {
        this.orderManager = orderManager;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            Matcher matcher = ORDERS_BY_RESTAURANT_PATTERN.matcher(path);

            if (matcher.matches() && "GET".equals(method)) {
                String restaurantId = matcher.group(1);
                handleGetOrdersForRestaurant(exchange, restaurantId);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Route Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetOrdersForRestaurant(HttpExchange exchange, String restaurantId) throws IOException {
        List<Order> orders = orderManager.getValidatedOrdersForRestaurant(restaurantId);

        List<RestaurantOrderResponse> responseList = orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        String jsonResponse = objectMapper.writeValueAsString(responseList);
        sendResponse(exchange, 200, jsonResponse);
    }

    private RestaurantOrderResponse mapToResponse(Order order) {
        String studentName = fetchStudentName(order.getStudentId());
        return new RestaurantOrderResponse(order, studentName);
    }

    private String fetchStudentName(String studentId) {
        String url = API_GATEWAY_ACCOUNT_URL+"name/" + studentId;
        System.out.println("DEBUG: OrderHandler fetching student name from: " + url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("DEBUG: Response Status: " + response.statusCode());
            System.out.println("DEBUG: Response Body: " + response.body());

            if (response.statusCode() == 200) {
                String studentName = objectMapper.readValue(response.body(), String.class);
                return studentName;
            } else {
                System.err.println("DEBUG: Failed to get 200 OK. Status was: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Exception while fetching student name:");
            e.printStackTrace();
        }
        return "Unknown Student (" + studentId + ")";
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    // --- DTOs ---

    // Response DTO with Student Name
    private static class RestaurantOrderResponse {
        public String orderId;
        public String studentName; // Changed from studentId
        public double amount;
        public String orderStatus;
        public List<Dish> dishes;
        public DeliveryLocation deliveryLocation;

        public RestaurantOrderResponse(Order order, String studentName) {
            this.orderId = order.getOrderId();
            this.studentName = studentName;
            this.amount = order.getAmount();
            this.orderStatus = order.getOrderStatus().toString();
            this.dishes = order.getDishes();
            this.deliveryLocation = order.getDeliveryLocation();
        }
    }

    // Helper DTO to parse Student API response
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class StudentNameDto {
        public String name;
        public String surname;
    }
}