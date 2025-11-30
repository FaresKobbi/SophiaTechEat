package fr.unice.polytech.dishes;

public class Topping extends PriceableItem {

    // NEW: Default constructor
    public Topping() {
        super();
    }

    public Topping(String name) {
        super(name);
    }
    public Topping(String name, double price) {
        super(name, price);
    }

    
}