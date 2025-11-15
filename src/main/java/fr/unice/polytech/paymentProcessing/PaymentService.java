package fr.unice.polytech.paymentProcessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.orderManagement.Order;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PaymentService implements IPaymentService{
    private final MockedExternalPaymentSystem externalPaymentSystem;
    private final HttpClient httpClient; // NEW
    private final ObjectMapper objectMapper = new ObjectMapper(); // NEW
    private static final String API_GATEWAY = "http://localhost:8080/api/accounts/"; // NEW

    public PaymentService(HttpClient httpClient){
        this.externalPaymentSystem = new MockedExternalPaymentSystem();
        this.httpClient = httpClient;
    }


    @Override
    public boolean processExternalPayment(Order order) {
        try {
            String studentId = order.getStudentId();
            BankInfo bankInfo = fetchBankInfo(studentId);

            return externalPaymentSystem.mockedCheckingInformation(bankInfo);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //TODO NOT TESTED
    private BankInfo fetchBankInfo(String studentId) throws IOException, InterruptedException {
        String targetUrl = API_GATEWAY + studentId + "/bankinfo";
        System.out.println("PaymentService: Calling StudentAccountService at " + targetUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch BankInfo. Status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), BankInfo.class);
    }

}
