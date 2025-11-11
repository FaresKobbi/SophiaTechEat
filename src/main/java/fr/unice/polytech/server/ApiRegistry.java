package fr.unice.polytech.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ApiRegistry {
    private final Map<String, HttpHandler> routes = new HashMap<>();
    private HttpHandler fallbackHandler = new NotFoundHandler();

    public void register(String method, String path, HttpHandler handler) {
        routes.put(method.toUpperCase() + " " + path, handler);
    }

    public void registerFallback(HttpHandler handler) {
        this.fallbackHandler = handler;
    }

    public void handle(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();

        HttpHandler handler = routes.get(method + " " + path);

        if (handler != null) {
            handler.handle(exchange);
        } else {
            fallbackHandler.handle(exchange);
        }
    }

    /**
     * Default handler for routes that are not found and not handled by fallback.
     */
    private static class NotFoundHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"error\":\"Not Found\", \"method\":\"" + exchange.getRequestMethod() + "\", \"path\":\"" + exchange.getRequestURI().getPath() + "\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = response.getBytes("UTF-8");
            exchange.sendResponseHeaders(404, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

}