package fr.unice.polytech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;
import fr.unice.polytech.services.handlers.DynamicAccountsHandler;
import fr.unice.polytech.services.handlers.StaticAccountsHandler;
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

        registry.register("GET", "/accounts", new StaticAccountsHandler(accountManager, objectMapper));
        registry.register("POST", "/accounts", new StaticAccountsHandler(accountManager, objectMapper));
        registry.registerFallback(new DynamicAccountsHandler());

        server.start(registry);
        System.out.println("StudentAccountService started on port " + port);
        System.out.println("Serving STATIC routes:  GET /accounts, POST /accounts");

    }
}
