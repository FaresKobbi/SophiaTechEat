package fr.unice.polytech.dishes;

public class PriceableItem {
    private String name;
    private String id;
    private double price;


    public PriceableItem(String name) {
        id = java.util.UUID.randomUUID().toString();
        this.name = name;
        price = 4;
    }

    public PriceableItem(String name, double price) {
        id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PriceableItem [name=" + name + ", price=" + price + "]";
    }

}