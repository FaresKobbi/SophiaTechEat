package fr.unice.polytech.dishes;
import java.util.ArrayList;
import java.util.List;

public class Dish {
    private String name;
    private String description;
    private double price;
    private DishCategory category; 
    private List<Topping> toppings;


    public Dish(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.toppings = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public DishCategory getCategory() {
        return category;
    }

    public List<Topping> getToppings() {
        return toppings;
    }
    
    // Setter to allow category modification
    public void setCategory(DishCategory category) {
        this.category = category;
    }

    public void addTopping(Topping topping) {
        this.toppings.add(topping);
    }
    

    @Override
    public String toString() {
        return "Dish [name=" + name + ", price=" + price + ", category=" + (category != null ? category.name() : "N/A") + "]";
    }
}