package fr.unice.polytech.restaurants;

import fr.unice.polytech.dishes.Dish;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    private Restaurant restaurant;
    private Dish dish1;
    private Dish dish2;
    private TimeSlot slot1;
    private OpeningHours mondayHours;
    private OpeningHours mondayUpdatedHours;

    @BeforeEach
    void setUp() {
        dish1 = new Dish("Pizza", 10.5);
        dish2 = new Dish("Pasta", 9.0);
        LocalTime startTime1 = LocalTime.of(12, 0); 
        LocalTime endTime1 = LocalTime.of(14, 0); 
        slot1 = new TimeSlot(startTime1, endTime1);
        restaurant = new Restaurant("La Bella Vita");
        mondayHours = new OpeningHours(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(14, 30));
        mondayUpdatedHours = new OpeningHours(DayOfWeek.MONDAY, LocalTime.of(19, 0), LocalTime.of(22, 0));
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
    void builder_ShouldBuildRestaurantWithDishesAndTimeSlots() {
        Restaurant built = new Restaurant.Builder("Chez Luigi")
                .withDish(dish1)
                .withTimeSlot(slot1)
                .build();

        assertEquals("Chez Luigi", built.getRestaurantName());
        assertEquals(1, built.getDishes().size());
        assertEquals(1, built.getAvailableTimeSlots().size());
        assertTrue(built.getDishes().contains(dish1));
    }

    @Test
    void builder_ShouldIgnoreNullDishesOrSlots() {
        Restaurant built = new Restaurant.Builder("Test")
                .withDish(null)
                .withTimeSlot(null)
                .build();

        assertTrue(built.getDishes().isEmpty());
        assertTrue(built.getAvailableTimeSlots().isEmpty());
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
        restaurant.addDish(dish1);
        assertTrue(restaurant.getDishes().contains(dish1));
    }

    @Test
    void addDish_ShouldThrowException_WhenDishIsNull() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.addDish(null));
    }

    @Test
    void addDish_ShouldThrowException_WhenDishAlreadyExists() {
        restaurant.addDish(dish1);
        assertThrows(IllegalArgumentException.class, () -> restaurant.addDish(dish1));
    }

    @Test
    void addDishes_ShouldAddMultipleDishes() {
        List<Dish> list = List.of(dish1, dish2);
        restaurant.addDishes(list);
        assertEquals(2, restaurant.getDishes().size());
    }

    @Test
    void addDishes_ShouldThrowException_WhenListIsNull() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.addDishes(null));
    }

    @Test
    void updateDish_ShouldReplaceOldDishWithNew() {
        restaurant.addDish(dish1);
        restaurant.updateDish(dish1, dish2);
        assertFalse(restaurant.getDishes().contains(dish1));
        assertTrue(restaurant.getDishes().contains(dish2));
    }

    @Test
    void updateDish_ShouldThrowException_WhenOldDishNotFound() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.updateDish(dish1, dish2));
    }

    @Test
    void updateDish_ShouldThrowException_WhenNewDishIsNull() {
        restaurant.addDish(dish1);
        assertThrows(IllegalArgumentException.class, () -> restaurant.updateDish(dish1, null));
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
        restaurant.addDish(dish1);
        String output = restaurant.toString();
        assertTrue(output.contains("restaurantName='La Bella Vita'"));
        assertTrue(output.contains("dishCount=1"));
    }


    @Test
    void constructor_ShouldInitializeEmptyOpeningHoursList() {
        assertNotNull(restaurant.getOpeningHours());
        assertTrue(restaurant.getOpeningHours().isEmpty());
    }


    @Test
    void addOpeningHours_ShouldAddHours_WhenDayDoesNotExist() {
        restaurant.addOpeningHours(mondayHours);
        assertEquals(1, restaurant.getOpeningHours().size());
        assertTrue(restaurant.getOpeningHours().contains(mondayHours));
    }

    @Test
    void addOpeningHours_ShouldThrowException_WhenHoursAreNull() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.addOpeningHours(null));
    }

    @Test
    void addOpeningHours_ShouldThrowException_WhenDayAlreadyExists() {
        restaurant.addOpeningHours(mondayHours);
        assertThrows(IllegalArgumentException.class, () -> restaurant.addOpeningHours(mondayUpdatedHours));
    }


    @Test
    void updateOpeningHours_ShouldUpdateHours_WhenDayExists() {
        restaurant.addOpeningHours(mondayHours);

        restaurant.updateOpeningHours(mondayUpdatedHours);

        assertEquals(1, restaurant.getOpeningHours().size());
        assertFalse(restaurant.getOpeningHours().contains(mondayHours));
        assertTrue(restaurant.getOpeningHours().contains(mondayUpdatedHours));
    }

    @Test
    void updateOpeningHours_ShouldThrowException_WhenHoursAreNull() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.updateOpeningHours(null));
    }

    @Test
    void updateOpeningHours_ShouldThrowException_WhenDayDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.updateOpeningHours(mondayHours));
    }


    @Test
    void setOpeningHours_ShouldThrowException_WhenListIsNull() {
        assertThrows(IllegalArgumentException.class, () -> restaurant.setOpeningHours(null));
    }


    @Test
    void setOpeningHours_ShouldReplaceExistingListWithNewList() {
        OpeningHours monday = new OpeningHours(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(14, 0));
        OpeningHours tuesday = new OpeningHours(DayOfWeek.TUESDAY, LocalTime.of(19, 0), LocalTime.of(22, 0));
        List<OpeningHours> newSchedule = List.of(monday, tuesday);

        restaurant.setOpeningHours(newSchedule);

        assertEquals(2, restaurant.getOpeningHours().size());
        assertEquals(newSchedule, restaurant.getOpeningHours());
    }





}
