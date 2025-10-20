package fr.unice.polytech.orderManagement;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.paymentProcessing.*;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {


    private List<Order> registeredOrders;
    private List<Order> pendingOrders;
    private Map<Order, Long> orderCreationTimes;
    private static final long TIMEOUT_MILLIS = 3 * 60 * 1000; // 3 minutes
    private final PaymentProcessorFactory paymentProcessorFactory;

    public OrderManager(){
        this(new PaymentProcessorFactory());

    }
    public OrderManager(PaymentProcessorFactory paymentProcessorFactory) {
        this.paymentProcessorFactory = paymentProcessorFactory;
        registeredOrders = new java.util.ArrayList<>();
        pendingOrders = new java.util.ArrayList<>();
        orderCreationTimes = new HashMap<>();
    }

    public void createOrder(List<Dish> dishes, StudentAccount studentAccount, DeliveryLocation deliveryLocation, Restaurant restaurant) {
        if (!studentAccount.hasDeliveryLocation(deliveryLocation)) {
            throw new IllegalArgumentException("Order creation failed: Delivery location is not among the student's saved locations.");
        }
        Order order = new Order.Builder(studentAccount)
                .dishes(dishes)
                .amount(calculateTotalAmount(dishes))
                .deliveryLocation(deliveryLocation)
                .restaurant(restaurant)
                .build();

        pendingOrders.add(order);
        orderCreationTimes.put(order, System.currentTimeMillis());

    }


    public void initiatePayment(Order order, PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method must be provided");
        }
        if (isOrderTimedOut(order)) {
            dropOrder(order);
            return;
        }
        //Creattion du processeur de paiement via la factory
        IPaymentProcessor processor = paymentProcessorFactory.createProcessor(order, paymentMethod);

        order.setPaymentMethod(paymentMethod);
        OrderStatus status = processor.processPayment(order);
        order.setOrderStatus(status);

    }

    private void dropOrder(Order order) {
        order.setOrderStatus(OrderStatus.CANCELED);
        pendingOrders.remove(order);
        orderCreationTimes.remove(order); // Important: remove the timer
    }

    private boolean isOrderTimedOut(Order order) {
        Long creationTime = orderCreationTimes.get(order);
        return (System.currentTimeMillis() - creationTime) > TIMEOUT_MILLIS;
    }


    public boolean registerOrder(Order order, Restaurant restaurant) {
        if (order.getOrderStatus() == OrderStatus.VALIDATED) {
            registeredOrders.add(order);
            pendingOrders.remove(order);
            orderCreationTimes.remove(order);
            restaurant.addOrder(order);
            return true;
        } else {
            return false;
        }
    }


    private double calculateTotalAmount(List<Dish> dishes) {
        return dishes.stream().mapToDouble(Dish::getPrice).sum();
    }

    public List<Order> getRegisteredOrders() {
        return registeredOrders;
    }
    public List<Order> getPendingOrders() {
        return pendingOrders;
    }




}
