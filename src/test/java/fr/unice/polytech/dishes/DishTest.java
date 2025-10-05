package fr.unice.polytech.dishes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DishTest {

    // Test 1: Verify the constructor and basic getters
    @Test
    void testDishCreationAndGetters() {
        Dish pizza = new Dish("Margherita", "Simple cheese pizza", 12.50);
        
        assertEquals("Margherita", pizza.getName(), "The name should be set correctly.");
        assertEquals("Simple cheese pizza", pizza.getDescription(), "The description should be set correctly.");
        assertEquals(12.50, pizza.getPrice(), "The price should be set correctly.");
        assertTrue(pizza.getToppings().isEmpty(), "The toppings list should be initialized empty.");
    }
    


    // Test 2: Test adding a topping
    @Test
    void testAddTopping() {
        Dish fries = new Dish("Fries", "Classic fries", 3.00);
        Topping cheese = new Topping("Cheese Sauce", 1.50);

        fries.addTopping(cheese);
        
        assertEquals(1, fries.getToppings().size(), "One topping should have been added.");
        assertEquals(cheese, fries.getToppings().get(0), "The correct topping object should be in the list.");
    }
    
    // Test 3: Test setting the category
    @Test
    void testSetCategory() {
        Dish coke = new Dish("Coke", "Carbonated drink", 2.00);
        coke.setCategory(DishCategory.DRINK);
        
        assertEquals(DishCategory.DRINK, coke.getCategory(), "The category should be set to DRINK.");
    }
}