package fr.unice.polytech.APIGateWay;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * A generic handler that forwards requests to the appropriate backend service
 * based on the request path.
 */
public class PassthroughHandler implements HttpHandler {

    private final HttpClient httpClient;

    public PassthroughHandler() {
        this(HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build());
    }

    public PassthroughHandler(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private static final String RESTAURANT_SERVICE_URL = "http://localhost:8081";
    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8082";
    private static final String ORDER_SERVICE_URL = "http://localhost:8083";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Allows Angular frontend
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getRawQuery();
        // String query = exchange.getRequestURI().getQuery();
        String method = exchange.getRequestMethod();
        String targetBaseUrl = null;

        if (path.startsWith("/api/restaurants")) {
            targetBaseUrl = RESTAURANT_SERVICE_URL;
            path = path.substring(4);
        } else if (path.startsWith("/api/accounts")) {
            targetBaseUrl = ACCOUNT_SERVICE_URL;
            path = path.substring(4);
        } else if (path.startsWith("/api/orders")) {
            targetBaseUrl = ORDER_SERVICE_URL;
            path = path.substring(4);
        } else if (path.startsWith("/api/suggestions")) {
            targetBaseUrl = RESTAURANT_SERVICE_URL;
            path = path.substring(4);
        } else {

            sendResponse(exchange, 404, "{\"error\":\"Gateway Route Not Found\"}".getBytes("UTF-8"));
            return;
        }

        String targetUrl = targetBaseUrl + path + (query == null ? "" : "?" + query);

        try {
            byte[] requestBody = exchange.getRequestBody().readAllBytes();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl));

            switch (method.toUpperCase()) {
                case "POST":
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(requestBody));
                    break;
                case "PUT":
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofByteArray(requestBody));
                    break;
                case "DELETE":
                    requestBuilder.DELETE();
                    break;
                case "GET":
                default:
                    requestBuilder.GET();
                    break;
            }

            exchange.getRequestHeaders().forEach((key, values) -> {
                if (!key.equalsIgnoreCase("Access-Control-Allow-Origin") &&
                        !key.equalsIgnoreCase("Content-Length")) {
                    values.forEach(value -> exchange.getResponseHeaders().add(key, value));
                }
            });

            HttpRequest request = requestBuilder.build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            response.headers().map().forEach((key, values) -> {
                if (!key.equalsIgnoreCase("Content-Length") && !key.toLowerCase().startsWith("access-control-allow-")) {
                    values.forEach(value -> exchange.getResponseHeaders().add(key, value));
                }
            });

            sendResponse(exchange, response.statusCode(), response.body());

        } catch (Exception e) {
            System.err.println("Passthrough failed: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 502, ("{\"error\":\"Bad Gateway: " + e.getMessage() + "\"}").getBytes("UTF-8"));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] responseBody) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBody.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBody);
        }
    }
}