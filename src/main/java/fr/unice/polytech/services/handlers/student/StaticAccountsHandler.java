package fr.unice.polytech.services.handlers.student;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.users.StudentAccount;
import fr.unice.polytech.users.StudentAccountManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class StaticAccountsHandler implements HttpHandler {

    private final StudentAccountManager accountManager;
    private final ObjectMapper objectMapper;

    public StaticAccountsHandler(StudentAccountManager manager, ObjectMapper mapper) {
        this.accountManager = manager;
        this.objectMapper = mapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if ("GET".equals(method)) {
                handleGetAllAccounts(exchange);
            } else if ("POST".equals(method)) {
                handleCreateAccount(exchange);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Handles GET /accounts
     */
    private void handleGetAllAccounts(HttpExchange exchange) throws IOException {
        List<StudentAccount> accounts = accountManager.getAllAccounts();
        String jsonResponse = objectMapper.writeValueAsString(accounts);
        sendResponse(exchange, 200, jsonResponse);
    }

    /**
     * Handles POST /accounts
     * Expects JSON body: {"name": "...", "surname": "...", "email": "..."}
     */
    private void handleCreateAccount(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        JsonNode body = objectMapper.readTree(requestBody);

        String name = body.get("name").asText();
        String surname = body.get("surname").asText();
        String email = body.get("email").asText();

        if (name == null || surname == null || email == null) {
            sendResponse(exchange, 400, "{\"error\":\"Missing required fields: name, surname, email\"}");
            return;
        }

        StudentAccount newAccount = new StudentAccount.Builder(name, surname)
                .email(email)
                .build();

        accountManager.addAccount(newAccount);
        System.out.println("StudentAccountService: Created new student " + newAccount.getStudentID());

        String jsonResponse = objectMapper.writeValueAsString(newAccount);
        sendResponse(exchange, 201, jsonResponse);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}