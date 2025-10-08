package fr.unice.polytech.dishes;
import java.util.ArrayList;
import java.util.List;

public class Dish {
    private String name;
    private String description;
    private double price;
    private DishCategory category; 
    private List<Topping> toppings;


    
    public Dish(String name, double price) {
        this.name = name;
        this.price = price;
    }


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

    // Individual Setters (used internally or for single-field updates)
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        // Good place for validation if needed:
        // if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }

    // Single Update Method (for administrative bulk updates)
    public void updateDetails(String newName, String newDescription, double newPrice) {
        this.setName(newName);
        this.setDescription(newDescription);
        this.setPrice(newPrice);
    }

    public void addTopping(Topping topping) {
        this.toppings.add(topping);
    }
    

    @Override
    public String toString() {
        return "Dish [name=" + name + ", price=" + price + ", category=" + (category != null ? category.name() : "N/A") + "]";
    }
}