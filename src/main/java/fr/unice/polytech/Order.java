package fr.unice.polytech;

import fr.unice.polytech.users.StudentAccount;

public class Order {
    StudentAccount studentAccount;
    double amount;
    OrderStatus orderStatus;

    public Order(StudentAccount studentAccount, double amount, OrderStatus orderStatus) {
        this.studentAccount = studentAccount;
        this.amount = amount;
        this.orderStatus = orderStatus;
    }

    public StudentAccount getStudentAccount() {
        return studentAccount;
    }

    public double getAmount() {
        return amount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
