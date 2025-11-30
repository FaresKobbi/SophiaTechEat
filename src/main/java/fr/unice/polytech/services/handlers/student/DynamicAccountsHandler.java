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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicAccountsHandler implements HttpHandler {
    private  StudentAccountManager accountManager;
    private  ObjectMapper objectMapper;

    public DynamicAccountsHandler(StudentAccountManager accountManager,ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.accountManager = accountManager;
    }

    private static final Pattern ACCOUNT_ID_PATTERN = Pattern.compile("/accounts/([^/]+)/?$");
    private static final Pattern ACCOUNT_NAME_ID_PATTERN = Pattern.compile("/accounts/name/([^/]+)/?$");

    private static final Pattern BANK_INFO_PATTERN = Pattern.compile("/accounts/([^/]+)/bankinfo/?$");
    private static final Pattern DEBIT_PATTERN = Pattern.compile("/accounts/([^/]+)/debit/?$");
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            Matcher matcher = ACCOUNT_NAME_ID_PATTERN.matcher(path);
            Matcher bankInfoMatcher = BANK_INFO_PATTERN.matcher(path);
            Matcher debitMatcher = DEBIT_PATTERN.matcher(path);

            if (matcher.matches() && "GET".equals(method)) {
                String studentId = matcher.group(1);
                handleGetAccountNameById(exchange, studentId);
            }// 1. Handle Get Bank Info (For External Payment)
            else if (bankInfoMatcher.matches() && "GET".equals(method)) {
                String studentId = bankInfoMatcher.group(1);
                handleGetBankInfo(exchange, studentId);
            }
            // 2. Handle Debit (For Internal Payment)
            else if (debitMatcher.matches() && "POST".equals(method)) {
                String studentId = debitMatcher.group(1);
                handleDebit(exchange, studentId);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Route Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetAccountNameById(HttpExchange exchange, String studentId) throws IOException {
        Optional<StudentAccount> account = accountManager.findAccountById(studentId);

        if (account.isPresent()) {
            String jsonResponse = objectMapper.writeValueAsString(account.get().getName()+" "+account.get().getSurname());
            sendResponse(exchange, 200, jsonResponse);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Student Not Found\"}");
        }
    }
    private void handleGetBankInfo(HttpExchange exchange, String studentId) throws IOException {
        Optional<StudentAccount> account = accountManager.findAccountById(studentId);

        if (account.isPresent() && account.get().getBankInfo() != null) {
            String jsonResponse = objectMapper.writeValueAsString(account.get().getBankInfo());
            sendResponse(exchange, 200, jsonResponse);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Bank Info Not Found\"}");
        }
    }

    private void handleDebit(HttpExchange exchange, String studentId) throws IOException {
        Optional<StudentAccount> accountOpt = accountManager.findAccountById(studentId);

        if (accountOpt.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Student Not Found\"}");
            return;
        }

        StudentAccount account = accountOpt.get();

        try {
            InputStream requestBody = exchange.getRequestBody();
            JsonNode body = objectMapper.readTree(requestBody);
            double amount = body.get("amount").asDouble();

            if (account.debit(amount)) {
                System.out.println("Debit successful for " + studentId + ". New Balance: " + account.getBalance());
                sendResponse(exchange, 200, "{\"status\":\"Success\", \"newBalance\":" + account.getBalance() + "}");
            } else {
                System.out.println("Debit failed for " + studentId + ". Insufficient funds.");
                sendResponse(exchange, 402, "{\"error\":\"Insufficient Funds\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid Debit Request\"}");
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
}