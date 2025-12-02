package fr.unice.polytech.orderManagement;


import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.paymentProcessing.PaymentMethod;
import fr.unice.polytech.users.DeliveryLocation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private String orderId;
    private String studentId;
    private String restaurantId;
    private double amount;
    private OrderStatus orderStatus;
    private List<Dish> dishes;
    private DeliveryLocation deliveryLocation;
    private PaymentMethod paymentMethod;


    private Order(Builder builder) {
        this.orderId = UUID.randomUUID().toString();
        this.amount = builder.amount;
        this.dishes = builder.dishes;
        this.studentId = builder.studentID;
        this.deliveryLocation = builder.deliveryLocation;
        this.orderStatus = builder.orderStatus != null ? builder.orderStatus : OrderStatus.PENDING;
        this.restaurantId = builder.restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getStudentAccountId() {
        return studentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStudentId() {
        return studentId;
    }


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public DeliveryLocation getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(DeliveryLocation deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static class Builder {
        private String studentID;
        private double amount;
        private List<Dish> dishes;
        private String restaurantId;
        private DeliveryLocation deliveryLocation;
        private OrderStatus orderStatus;

        public Builder(String studentID) {
            this.studentID = studentID;
        }

        public Builder deliveryLocation(DeliveryLocation deliveryLocation) {
            this.deliveryLocation = deliveryLocation;
            return this;
        }

        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }
        public Builder dishes(List<Dish> dishes) {
            this.dishes = dishes;
            return this;
        }
        public Builder restaurant(String restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }


        public Order build() {
            return new Order(this);
        }


        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Double.compare(amount, builder.amount) == 0 && Objects.equals(studentID, builder.studentID) && Objects.equals(restaurantId, builder.restaurantId) && Objects.equals(dishes, builder.dishes) && Objects.equals(deliveryLocation, builder.deliveryLocation) && orderStatus == builder.orderStatus;
        }

        @Override
        public int hashCode() {
            return Objects.hash(studentID, amount, restaurantId, dishes, deliveryLocation, orderStatus);
        }
    }
}
