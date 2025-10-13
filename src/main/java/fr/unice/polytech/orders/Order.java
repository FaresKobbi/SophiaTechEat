package fr.unice.polytech.orders;

import fr.unice.polytech.users.*;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.restaurants.TimeSlot;

import java.util.List;

public class Order {
    UserAccount client;
    double amount;
    OrderStatus orderStatus;
    Restaurant restaurant;
    List<Dish> dishes;
    TimeSlot timeSlot;

    public Order(UserAccount client, double amount, Restaurant restaurant, List<Dish> dishes, TimeSlot timeSlot) {
        this.client = client;
        this.amount = amount;
        this.restaurant = restaurant;
        this.dishes = dishes;
        this.timeSlot = timeSlot;
        this.orderStatus = OrderStatus.PENDING;
    }

    public Order(UserAccount client, double amount) {
        this.client = client;
        this.amount = amount;
    }

    public UserAccount getClient() { return client; }
    public double getAmount() { return amount; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public Restaurant getRestaurant() { return restaurant; }
    public List<Dish> getDishes() { return dishes; }
    public TimeSlot getTimeSlot() { return timeSlot; }
}
