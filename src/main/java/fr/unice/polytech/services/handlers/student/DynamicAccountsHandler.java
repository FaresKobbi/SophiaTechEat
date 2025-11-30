package fr.unice.polytech.services.handlers.student;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import fr.unice.polytech.paymentProcessing.BankInfo;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;
import fr.unice.polytech.users.StudentAccountManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
    private static final Pattern LOCATIONS_PATTERN = Pattern.compile("/accounts/([^/]+)/locations/?$");
    private static final Pattern LOCATION_ITEM_PATTERN = Pattern.compile("/accounts/([^/]+)/locations/([^/]+)/?$");
    private static final Pattern BANK_INFO_PATTERN = Pattern.compile("/accounts/([^/]+)/bankinfo/?$");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            Matcher nameMatcher = ACCOUNT_NAME_ID_PATTERN.matcher(path);
            Matcher locationsMatcher = LOCATIONS_PATTERN.matcher(path);
            Matcher locationItemMatcher = LOCATION_ITEM_PATTERN.matcher(path);
            Matcher bankInfoMatcher = BANK_INFO_PATTERN.matcher(path);

            if (nameMatcher.matches() && "GET".equals(method)) {
                String studentId = nameMatcher.group(1);
                handleGetAccountNameById(exchange, studentId);
            } 
            else if (locationsMatcher.matches()) {
                String studentId = locationsMatcher.group(1);
                if ("GET".equals(method)) {
                    handleGetLocations(exchange, studentId);
                } else if ("POST".equals(method)) {
                    handleAddLocation(exchange, studentId);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            }
            else if (locationItemMatcher.matches() && "DELETE".equals(method)) {
                String studentId = locationItemMatcher.group(1);
                String locationId = locationItemMatcher.group(2); 
                handleRemoveLocation(exchange, studentId, locationId);
            }
            // --- GESTION DES BANK INFO ---
            else if (bankInfoMatcher.matches()) {
                String studentId = bankInfoMatcher.group(1);
                if ("GET".equals(method)) {
                    handleGetBankInfo(exchange, studentId);
                } else if ("PUT".equals(method)) {
                    handleUpdateBankInfo(exchange, studentId);
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                }
            }
            else {
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

    private void handleGetLocations(HttpExchange exchange, String studentId) throws IOException {
        Optional<StudentAccount> account = accountManager.findAccountById(studentId);
        if (account.isPresent()) {
            String json = objectMapper.writeValueAsString(account.get().getDeliveryLocations());
            sendResponse(exchange, 200, json);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Student Not Found\"}");
        }
    }

    private void handleAddLocation(HttpExchange exchange, String studentId) throws IOException {
        Optional<StudentAccount> account = accountManager.findAccountById(studentId);
        if (account.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Student Not Found\"}");
            return;
        }
        
        InputStream requestBody = exchange.getRequestBody();
        JsonNode body = objectMapper.readTree(requestBody);
        DeliveryLocation newLoc = new DeliveryLocation();
        newLoc.setName(body.get("name").asText());
        newLoc.setAddress(body.get("address").asText());
        newLoc.setCity(body.get("city").asText());
        newLoc.setZipCode(body.get("zipCode").asText());
        
        account.get().addDeliveryLocation(newLoc);
        
        sendResponse(exchange, 201, objectMapper.writeValueAsString(newLoc));
    }

    private void handleRemoveLocation(HttpExchange exchange, String studentId, String locationId) throws IOException {
        Optional<StudentAccount> account = accountManager.findAccountById(studentId);
        if (account.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Student Not Found\"}");
            return;
        }
        account.get().removeDeliveryLocation(locationId);
        sendResponse(exchange, 204, ""); // No Content
    }

    private void handleGetBankInfo(HttpExchange exchange, String studentId) throws IOException {
        Optional<StudentAccount> account = accountManager.findAccountById(studentId);
        
        if (account.isPresent()) {
            BankInfo info = account.get().getBankInfo();
            if (info != null) {
                // On crée une map pour faciliter la sérialisation JSON propre (séparation mois/année)
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("cardNumber", info.getCardNumber());
                responseMap.put("cvv", info.getCVV());
                // Extraction mois/année depuis YearMonth
                responseMap.put("month", info.getExpirationDate().getMonthValue());
                responseMap.put("year", info.getExpirationDate().getYear());
                
                String json = objectMapper.writeValueAsString(responseMap);
                sendResponse(exchange, 200, json);
            } else {
                // Pas d'info bancaire, on renvoie un objet vide ou null
                sendResponse(exchange, 200, "null");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Student Not Found\"}");
        }
    }

    private void handleUpdateBankInfo(HttpExchange exchange, String studentId) throws IOException {
        Optional<StudentAccount> account = accountManager.findAccountById(studentId);
        if (account.isEmpty()) {
            sendResponse(exchange, 404, "{\"error\":\"Student Not Found\"}");
            return;
        }

        InputStream requestBody = exchange.getRequestBody();
        JsonNode body = objectMapper.readTree(requestBody);

        String cardNumber = body.get("cardNumber").asText();
        int cvv = body.get("cvv").asInt();
        int month = body.get("month").asInt();
        int year = body.get("year").asInt();

        BankInfo newBankInfo = new BankInfo(cardNumber, cvv, month, year);
        account.get().setBankInfo(newBankInfo);

        sendResponse(exchange, 200, objectMapper.writeValueAsString(body));
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