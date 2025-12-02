package fr.unice.polytech.paymentProcessing.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpStudentAccountClient implements StudentAccountClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_GATEWAY = "http://localhost:8080/api/accounts/";

    public HttpStudentAccountClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    
    @Override
    public boolean debit(String studentId, double amount) {
        try {
            String targetUrl = API_GATEWAY + studentId + "/debit";
            ObjectNode body = objectMapper.createObjectNode();
            body.put("amount", amount);
            String requestBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            System.out.println("HttpStudentAccountClient: Calling StudentAccountService at " + targetUrl);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("HttpStudentAccountClient: Debit successful.");
                return true;
            } else {
                System.out.println("HttpStudentAccountClient: Debit failed. Status: " + response.statusCode());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
