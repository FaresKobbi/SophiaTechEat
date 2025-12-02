package fr.unice.polytech.restaurants;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.dishes.DishCategory;
import fr.unice.polytech.dishes.DishType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DishManagerTest {

    private DishManager dishManager;

    @BeforeEach
    void setUp() {
        dishManager = new DishManager();
    }

    @Test
    void testCreateDish() {
        Dish dish = dishManager.createDish("Pizza", "Delicious cheese pizza", 12.5);
        assertNotNull(dish);
        assertEquals("Pizza", dish.getName());
        assertEquals("Delicious cheese pizza", dish.getDescription());
        assertEquals(12.5, dish.getPrice());
    }

    @Test
    void testUpdatePrice() {
        Dish dish = dishManager.createDish("Pizza", "Delicious cheese pizza", 12.5);
        dishManager.updatePrice(dish, 15.0);
        assertEquals(15.0, dish.getPrice());
    }

    @Test
    void testUpdateDescription() {
        Dish dish = dishManager.createDish("Pizza", "Delicious cheese pizza", 12.5);
        dishManager.updateDescription(dish, "Extra cheese pizza");
        assertEquals("Extra cheese pizza", dish.getDescription());
    }

    @Test
    void testUpdateDishCategory() {
        Dish dish = dishManager.createDish("Pizza", "Delicious cheese pizza", 12.5);
        dishManager.updateDishCategory(dish, DishCategory.MAIN_COURSE);
        assertEquals(DishCategory.MAIN_COURSE, dish.getCategory());
    }

    @Test
    void testUpdateDishType() {
        Dish dish = dishManager.createDish("Pizza", "Delicious cheese pizza", 12.5);
        dishManager.updateDishType(dish, DishType.PIZZA);
        assertEquals(DishType.PIZZA, dish.getDishType());
    }
}
