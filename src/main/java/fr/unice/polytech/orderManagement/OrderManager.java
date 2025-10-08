package fr.unice.polytech.orderManagement;

import fr.unice.polytech.DeliveryLocation;
import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.users.StudentAccount;

import java.util.List;

public class OrderManager {

    List<Order> registeredOrders;
    List<Order> pendingOrders;

    public OrderManager(){
        registeredOrders = new java.util.ArrayList<>();
        pendingOrders = new java.util.ArrayList<>();
    }

    public void createOrder(List<Dish> dishes, StudentAccount studentAccount, DeliveryLocation deliveryLocation) {
        Order order = new Order.Builder(studentAccount)
                .dishes(dishes)
                .amount(calculateTotalAmount(dishes))
                .deliveryLocation(deliveryLocation)
                .build();

        pendingOrders.add(order);
    }

    private double calculateTotalAmount(List<Dish> dishes) {
        return dishes.stream().mapToDouble(Dish::getPrice).sum();
    }

}
