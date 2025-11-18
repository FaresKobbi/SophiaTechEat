package fr.unice.polytech.dishes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Dish extends PriceableItem {
    private String description;
    private DishType dishType;
    private DishCategory category;
    private List<Topping> toppings;
    private List<DietaryLabel> dietaryLabels;

    public Dish(String name, double price) {
        super(name, price);
    }

    public Dish(String name, double price, String description) {
        super(name, price);
        this.description = description;
    }

    public DishType getDishType() {
        return dishType;
    }

    public Dish(String name, String description, double price) {
        super(name, price);
        this.description = description;
        this.toppings = new ArrayList<>();
    }

    public Dish(String name, String description, double price, DishType dishType ) {
        super(name, price);
        this.description = description;
        this.toppings = new ArrayList<>();
        this.dishType = dishType;
    }


    public void setDishType(DishType dishType) {
        this.dishType = dishType;
    }


    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description;
    }

    // Single Update Method (for administrative bulk updates)
    public void updateDetails(String newName, String newDescription, double newPrice) {
        this.setName(newName);
        this.setDescription(newDescription);
        this.setPrice(newPrice);
    }

    public void setDietaryLabels(List<DietaryLabel> dietaryLabels) {
        this.dietaryLabels = dietaryLabels;
    }

    public List<DietaryLabel> getDietaryLabels() {
        return dietaryLabels;
    }

    public void addTopping(Topping topping) {
        this.toppings.add(topping);
    }


    @Override
    public String toString() {
        String categoryStr = (category == null) ? "N/A" : category.toString();
        return "Dish [name=" + getName() + ", price=" + getPrice() + ", category=" + categoryStr + "]";
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(description, dish.description) && dishType == dish.dishType && category == dish.category && Objects.equals(toppings, dish.toppings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, dishType, category, toppings);
    }
}