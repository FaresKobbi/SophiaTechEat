package fr.unice.polytech.APIGateWay;

import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;

import java.io.IOException;

public class ApiGateWayServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        SimpleServer server = new SimpleServer(port);

        ApiRegistry registry = new ApiRegistry();

        registry.registerFallback(new PassthroughHandler());

        server.start(registry);
        System.out.println("API Gateway started on port " + port);
        System.out.println("Gateway is now routing:");
        System.out.println(" - http://localhost:8080/api/restaurants -> RestaurantService (Port 8081)");
        System.out.println(" - http://localhost:8080/api/accounts   -> StudentAccountService (Port 8082)");
        System.out.println(" - http://localhost:8083/api/orders   -> OrderService (Port 8083)");
    }
}
