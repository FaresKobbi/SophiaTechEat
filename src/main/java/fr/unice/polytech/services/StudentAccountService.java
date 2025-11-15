package fr.unice.polytech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;
import fr.unice.polytech.users.StudentAccount;
import fr.unice.polytech.users.StudentAccountManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

public class StudentAccountService {
    private static final StudentAccountManager accountManager = new StudentAccountManager();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        //MOCK
        accountManager.addAccount(
                new StudentAccount.Builder("Alice", "Smith")
                        .email("alice.smith@etu.unice.fr")
                        .build()
        );
        accountManager.addAccount(
                new StudentAccount.Builder("Bob", "Martin")
                        .email("bob.martin@etu.unice.fr")
                        .build()
        );

        int port = 8082;
        SimpleServer server = new SimpleServer(port);

        ApiRegistry registry = new ApiRegistry();

        registry.register("GET", "/accounts", new AccountsHandler());

        server.start(registry);
        System.out.println("StudentAccountService started on port " + port);
        System.out.println("Now serving exact route: GET /accounts");    }

    /**
     * Handles GET /accounts
     */
    static class AccountsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    List<StudentAccount> accounts = accountManager.getAllAccounts();
                    String jsonResponse = objectMapper.writeValueAsString(accounts);
                    sendResponse(exchange, 200, jsonResponse);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
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
