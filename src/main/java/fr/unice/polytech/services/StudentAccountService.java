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

import java.io.IOException;

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
                        .email("bob.martin@etu.unice.fr")
                        .addDeliveryLocation(new DeliveryLocation("Dorm", "50 Avenue Jean Medecin", "Nice", "06000"))
                        .build()
        );
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
