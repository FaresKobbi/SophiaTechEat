package fr.unice.polytech.services.handlers.suggestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.suggestion.DishInfo;
import fr.unice.polytech.suggestion.HybridSuggestionService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SuggestionHandler implements HttpHandler {

    private final HybridSuggestionService suggestionService;
    private final ObjectMapper objectMapper;

    public SuggestionHandler(HybridSuggestionService suggestionService, ObjectMapper objectMapper) {
        this.suggestionService = suggestionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            String keyword = "";
            if (query != null && query.contains("keyword=")) {
                keyword = query.split("keyword=")[1].split("&")[0];
            }

            List<DishInfo> suggestions = suggestionService.getSuggestions(keyword);
            String response = objectMapper.writeValueAsString(suggestions);
            sendResponse(exchange, 200, response);
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        // Add CORS headers for frontend access
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
