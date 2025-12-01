package fr.unice.polytech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;
import fr.unice.polytech.services.handlers.student.DynamicAccountsHandler;
import fr.unice.polytech.services.handlers.student.StaticAccountsHandler;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;
import fr.unice.polytech.users.StudentAccountManager;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;

public class StudentAccountService {
    private static final StudentAccountManager accountManager = new StudentAccountManager();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        //MOCK
        accountManager.addAccount(
                new StudentAccount.Builder("Alice", "Smith")
                        .email("alice.smith@etu.unice.fr")
                        .addDeliveryLocation(new DeliveryLocation("Home", "10 Rue de France", "Nice", "06000"))
                        .addDeliveryLocation(new DeliveryLocation("University", "930 Route des Colles", "Biot", "06410"))
                        .build()
        );
        accountManager.addAccount(
                new StudentAccount.Builder("Bob", "Martin")
                        .deliveryLocations(List.of(
                                new DeliveryLocation("Home", "123 Main St", "Nice", "06000"),
                                new DeliveryLocation("Work", "456 Elm St", "Nice", "06000")))
                        .bankInfo("1234567890123456", 123, 12, 2026)
                        .email("bob.martin@etu.unice.fr")
                        .addDeliveryLocation(new DeliveryLocation("Dorm", "50 Avenue Jean Medecin", "Nice", "06000"))
                        .bankInfo("1234567890123456", 123, 12, 2025)
                        .build()
        );
        objectMapper.registerModule(new JavaTimeModule());
        int port = 8082;
        SimpleServer server = new SimpleServer(port);

        ApiRegistry registry = new ApiRegistry();

        registry.register("GET", "/accounts", new StaticAccountsHandler(accountManager, objectMapper));
        registry.register("POST", "/accounts", new StaticAccountsHandler(accountManager, objectMapper));
        registry.registerFallback(new DynamicAccountsHandler(accountManager, objectMapper));

        server.start(registry);
        System.out.println("StudentAccountService started on port " + port);
        System.out.println("Serving STATIC routes:  GET /accounts, POST /accounts");

    }
}
