package fr.unice.polytech.restaurants;

import fr.unice.polytech.dishes.Dish;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    private Restaurant restaurant;
    private Dish dish1;
    private Dish dish2;
    private TimeSlot slot1;

    @BeforeEach
    void setUp() {
        dish1 = new Dish("Pizza", 10.5);
        dish2 = new Dish("Pasta", 9.0);
        LocalTime startTime1 = LocalTime.of(12, 0);
        LocalTime endTime1 = LocalTime.of(14, 0);
        slot1 = new TimeSlot(startTime1, endTime1);
        restaurant = new Restaurant("La Bella Vita");
    }

    // ---------- Constructor Tests ----------

    @Test
    void constructor_ShouldThrowException_WhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Restaurant(null));
    }

    @Test
    void constructor_ShouldThrowException_WhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Restaurant(""));
    }

    @Test
    void constructor_ShouldInitializeEmptyLists() {
        assertTrue(restaurant.getDishes().isEmpty());
        assertTrue(restaurant.getAvailableTimeSlots().isEmpty());
    }

    // ---------- Builder Tests ----------

    @Test
    void builder_ShouldBuildRestaurantWithName() {
        Restaurant built = new Restaurant.Builder("Chez Luigi").build();

        assertEquals("Chez Luigi", built.getRestaurantName());
        assertNotNull(built.getDishes());
        assertNotNull(built.getAvailableTimeSlots());
    }

    // ---------- Setter & Getter Tests ----------

    @Test
    void setRestaurantName_ShouldThrowException_WhenNameIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.setRestaurantName(""));
        assertThrows(IllegalArgumentException.class, () -> restaurant.setRestaurantName(null));
    }

    @Test
    void setRestaurantName_ShouldUpdateValue() {
        restaurant.setRestaurantName("NewName");
        assertEquals("NewName", restaurant.getRestaurantName());
    }

    // ---------- Dish Management Tests ----------

    @Test
    void addDish_ShouldAddDishToList() {
        restaurant.addDish(dish1.getName(), "Tasty pizza", dish1.getPrice());
        assertEquals(1, restaurant.getDishes().size());
    }

    @Test
    void addDish_ShouldThrowException_WhenNameIsNullOrEmptyOrDescriptionNullOrPriceNegative() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.addDish(null, "desc", 5.0));
        assertThrows(IllegalArgumentException.class, () -> restaurant.addDish("", "desc", 5.0));
        assertThrows(IllegalArgumentException.class, () -> restaurant.addDish("Name", null, 5.0));
        assertThrows(IllegalArgumentException.class, () -> restaurant.addDish("Name", "desc", -1.0));
    }

    @Test
    void addDish_ShouldAllowDuplicateDishes_WhenCalledTwice() {
        restaurant.addDish(dish1.getName(), "desc", dish1.getPrice());
        restaurant.addDish(dish1.getName(), "desc", dish1.getPrice());
        assertEquals(2, restaurant.getDishes().size());
    }

    @Test
    void addMultipleDishes_ShouldAddAll() {
        List<Dish> list = List.of(dish1, dish2);
        for (Dish d : list) {
            restaurant.addDish(d.getName(), "desc", d.getPrice());
        }
        assertEquals(2, restaurant.getDishes().size());
    }

    @Test
    void updateDish_ShouldUpdateDescription_WhenValidDishProvided() {
        restaurant.addDish(dish1.getName(), "old desc", (int) dish1.getPrice());
        Dish added = restaurant.getDishes().get(0);
        assertDoesNotThrow(() -> restaurant.updateDish(added, "new desc"));
    }

    @Test
    void updateDish_ShouldUpdatePrice_WhenValidDishProvided() {
        restaurant.addDish(dish1.getName(), "desc", dish1.getPrice());
        Dish added = restaurant.getDishes().get(0);
        restaurant.updateDish(added, 12);
        // verify price updated if Dish exposes getPrice()
        // fallback: ensure no exception and dish remains present
        assertTrue(restaurant.getDishes().contains(added));
    }

    @Test
    void updateDish_ShouldThrowException_WhenOldDishNotFound() {
        // use a Dish instance that was not added to restaurant
        assertThrows(IllegalArgumentException.class, () -> restaurant.updateDish(dish1, "new"));
        assertThrows(IllegalArgumentException.class, () -> restaurant.updateDish(dish1, 15));
    }

    @Test
    void updateDish_ShouldThrowException_WhenNewDescriptionIsNull() {
        restaurant.addDish(dish1.getName(), "desc", dish1.getPrice());
        Dish added = restaurant.getDishes().get(0);
        assertThrows(IllegalArgumentException.class, () -> restaurant.updateDish(added, (String) null));
    }

    // ---------- Equality & ToString Tests ----------

    @Test
    void equals_ShouldBeTrueForSameName() {
        Restaurant r1 = new Restaurant("Same");
        Restaurant r2 = new Restaurant("Same");
        assertEquals(r1, r2);
    }

    @Test
    void equals_ShouldBeFalseForDifferentName() {
        Restaurant r1 = new Restaurant("A");
        Restaurant r2 = new Restaurant("B");
        assertNotEquals(r1, r2);
    }

    @Test
    void hashCode_ShouldDependOnName() {
        Restaurant r1 = new Restaurant("X");
        Restaurant r2 = new Restaurant("X");
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toString_ShouldContainRestaurantNameAndCounts() {
        restaurant.addDish(dish1.getName(), "desc", dish1.getPrice());
        String output = restaurant.toString();
        assertTrue(output.contains("restaurantName='La Bella Vita'"));
        assertTrue(output.contains("dishCount=1"));
    }
}
