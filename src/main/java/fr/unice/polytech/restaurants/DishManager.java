package fr.unice.polytech.restaurants;

import fr.unice.polytech.dishes.Dish;

public class DishManager {
    private Restaurant restaurant;

    public DishManager(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Dish createDish(String name, String description, double price) {
        return new Dish(name, description, price);

    }

    public void updatePrice(Dish dish, double newPrice) {
        dish.setPrice(newPrice);
    }

    public void updateDescription(Dish dish, String newDescription) {
        dish.setDescription(newDescription);
    }
}
