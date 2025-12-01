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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fr.unice.polytech.paymentProcessing.PaymentMethod;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class OrderHandler implements HttpHandler {
    private final OrderManager orderManager;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private static final String API_GATEWAY_ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    private static final String API_GATEWAY_RESTAURANT_URL = "http://localhost:8080/api/restaurants/";

    private static final Pattern ORDERS_BY_RESTAURANT_PATTERN = Pattern.compile("/orders/restaurant/([^/]+)/?$");
    private static final Pattern ORDERS_BY_STUDENT_PATTERN = Pattern.compile("/orders/student/([^/]+)/?$");

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
            Matcher restMatcher = ORDERS_BY_RESTAURANT_PATTERN.matcher(path);
            Matcher studentMatcher = ORDERS_BY_STUDENT_PATTERN.matcher(path);

            if (restMatcher.matches() && "GET".equals(method)) {
                String restaurantId = restMatcher.group(1);
                handleGetOrdersForRestaurant(exchange, restaurantId);
            }
            else if (studentMatcher.matches() && "GET".equals(method)) {
                String studentId = studentMatcher.group(1);
                handleGetOrdersForStudent(exchange, studentId);
            } else if (path.equals("/orders") && "POST".equals(method)) {
                handleCreateOrder(exchange);
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

    private void handleGetOrdersForStudent(HttpExchange exchange, String studentId) throws IOException {
        List<Order> orders = orderManager.getOrdersForStudent(studentId);
        
        List<StudentOrderResponse> responseList = orders.stream()
                .map(this::mapToStudentResponse) // NEW MAPPING
                .collect(Collectors.toList());
        
        String jsonResponse = objectMapper.writeValueAsString(responseList);
        sendResponse(exchange, 200, jsonResponse);
    }
    private StudentOrderResponse mapToStudentResponse(Order order) {
        String studentName = fetchStudentName(order.getStudentId());
        String restaurantName = fetchRestaurantName(order.getRestaurantId());
        return new StudentOrderResponse(order, studentName, restaurantName);
    }

    private RestaurantOrderResponse mapToResponse(Order order) {
        String studentName = fetchStudentName(order.getStudentId());
        String restaurantName = fetchRestaurantName(order.getRestaurantId());
        return new RestaurantOrderResponse(order, studentName, restaurantName);
    }
    

    private String fetchStudentName(String studentId) {
        String url = API_GATEWAY_ACCOUNT_URL + studentId + "/name";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Student (" + studentId + ")";
    }

    private String fetchRestaurantName(String restaurantId) {
        String url = API_GATEWAY_RESTAURANT_URL + restaurantId;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                RestaurantNameDto rest = objectMapper.readValue(response.body(), RestaurantNameDto.class);
                return rest.restaurantName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Restaurant (" + restaurantId + ")";
    }

    private void handleCreateOrder(HttpExchange exchange) throws IOException {
        try {
            InputStream requestBody = exchange.getRequestBody();
            OrderRequest orderRequest = objectMapper.readValue(requestBody, OrderRequest.class);

            boolean reserved = reserveSlot(orderRequest.restaurantId, orderRequest.timeSlot);
            if (!reserved) {
                sendResponse(exchange, 409, "{\"error\":\"Time slot full\"}");
                return;
            }

            try {
                Order order = orderManager.createOrder(orderRequest.dishes, orderRequest.studentId,
                        orderRequest.deliveryLocation, orderRequest.restaurantId);

                orderManager.initiatePayment(order, orderRequest.paymentMethod);

                if (order.getOrderStatus() == fr.unice.polytech.orderManagement.OrderStatus.CANCELED) {
                    throw new RuntimeException("Payment failed");
                }

                String jsonResponse = objectMapper.writeValueAsString(mapToResponse(order));
                sendResponse(exchange, 201, jsonResponse);

            } catch (Exception e) {
                releaseSlot(orderRequest.restaurantId, orderRequest.timeSlot);
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Order creation failed: " + e.getMessage() + "\"}");
        }
    }

    private boolean reserveSlot(String restaurantId, TimeSlotDto slot) {
        try {
            String url = API_GATEWAY_RESTAURANT_URL + restaurantId + "/slots/reserve";
            String jsonBody = objectMapper.writeValueAsString(slot);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void releaseSlot(String restaurantId, TimeSlotDto slot) {
        try {
            String url = API_GATEWAY_RESTAURANT_URL + restaurantId + "/slots/release";
            String jsonBody = objectMapper.writeValueAsString(slot);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OrderRequest {
        public List<Dish> dishes;
        public String studentId;
        public DeliveryLocation deliveryLocation;
        public String restaurantId;
        public TimeSlotDto timeSlot;
        public PaymentMethod paymentMethod;
    }

    private static class TimeSlotDto {
        public DayOfWeek day;
        public LocalTime startTime;
        public LocalTime endTime;
    }

    private static class RestaurantOrderResponse {
        public String orderId;
        public String studentName;
        public String restaurantName;
        public double amount;
        public String orderStatus;
        public List<Dish> dishes;
        public DeliveryLocation deliveryLocation;
        public String restaurantId; // Added to help frontend logic if needed

        public RestaurantOrderResponse(Order order, String studentName, String restaurantName) {
            this.orderId = order.getOrderId();
            this.studentName = studentName;
            this.amount = order.getAmount();
            this.orderStatus = order.getOrderStatus().toString();
            this.dishes = order.getDishes();
            this.deliveryLocation = order.getDeliveryLocation();
            this.restaurantId = order.getRestaurantId();
            this.restaurantName = restaurantName;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RestaurantNameDto {
        public String restaurantName;
    }

    private static class StudentOrderResponse {
        public String orderId;
        public String studentName;
        public String restaurantName;
        public double amount;
        public String orderStatus;
        public String paymentMethod; // <--- ADDED
        public List<Dish> dishes;
        public DeliveryLocation deliveryLocation;
        public String restaurantId;

        public StudentOrderResponse(Order order, String studentName, String restaurantName) {
            this.orderId = order.getOrderId();
            this.studentName = studentName;
            this.restaurantName = restaurantName;
            this.amount = order.getAmount();
            this.orderStatus = order.getOrderStatus().toString();
            this.paymentMethod = (order.getPaymentMethod() != null) ? order.getPaymentMethod().toString() : "UNKNOWN"; // <--- Map it
            this.dishes = order.getDishes();
            this.deliveryLocation = order.getDeliveryLocation();
            this.restaurantId = order.getRestaurantId();
        }
    }
}