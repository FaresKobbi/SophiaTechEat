package fr.unice.polytech.orderManagement;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.paymentProcessing.*;
import fr.unice.polytech.users.DeliveryLocation;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderManager {



    private Map<String, Order> allOrders;
    private final PaymentProcessorFactory paymentProcessorFactory;


    // CHANGED: Constructor now takes HttpClient
    public OrderManager(HttpClient httpClient){
        this.paymentProcessorFactory = new PaymentProcessorFactory(httpClient);
        this.allOrders = new HashMap<>();
    }

    public void createOrder(List<Dish> dishes, String studentId, DeliveryLocation deliveryLocation, String restaurantId) {
        if (dishes == null || dishes.isEmpty()) {
            throw new IllegalArgumentException("Empty cart");
        }
        if (deliveryLocation == null) {
            throw new IllegalArgumentException("Missing delivery address");
        }
        Order order = new Order.Builder(studentId)
                .dishes(dishes)
                .amount(calculateTotalAmount(dishes))
                .deliveryLocation(deliveryLocation)
                .restaurant(restaurantId)
                .orderStatus(OrderStatus.PENDING)
                .build();


        allOrders.put(order.getOrderId(), order);

    }


    public void initiatePayment(Order order, PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method must be provided");
        }
        /*
        * */
        //Creattion du processeur de paiement via la factory
        IPaymentProcessor processor = paymentProcessorFactory.createProcessor(order, paymentMethod);

        order.setPaymentMethod(paymentMethod);
        OrderStatus status = processor.processPayment(order);
        order.setOrderStatus(status);

    }

    //TODO : register and drop order to be moved to service layer
    /*
    private void dropOrder(Order order) {
        pendingOrders.remove(order);
    }



    public boolean registerOrder(Order order, Restaurant restaurant) {
        if (order.getOrderStatus() == OrderStatus.VALIDATED) {
            registeredOrders.add(order);
            pendingOrders.remove(order);
            restaurant.addOrder(order);
            return true;
        } else if (order.getOrderStatus() == OrderStatus.CANCELED) {
            dropOrder(order);
        }
        return false;
    }

    */

    private double calculateTotalAmount(List<Dish> dishes) {
        return dishes.stream().mapToDouble(Dish::getPrice).sum();
    }


    public Order getOrder(String orderId) {
        return allOrders.get(orderId);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(allOrders.values());
    }

    public List<Order> getOrdersForStudent(String studentId) {
        return allOrders.values().stream()
                .filter(order -> studentId.equals(order.getStudentId()))
                .collect(Collectors.toList());
    }

    public List<Order> getPendingOrders() {
        return allOrders.values().stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.PENDING)
                .collect(Collectors.toList());
    }
    public List<Order> getRegisteredOrders() {
        return allOrders.values().stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.VALIDATED)
                .collect(Collectors.toList());
    }




}
