package fr.unice.polytech.restaurants;


import fr.unice.polytech.restaurants.Restaurant;


import fr.unice.polytech.dishes.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RestaurantManager class.
 */
class RestaurantManagerTest {

    private RestaurantManager manager;
    private Restaurant restaurant;
    private TimeSlot slot1;
    private TimeSlot slot2;
    private TimeSlot slot3;

    @BeforeEach
    void setUp() {
        // Initialize manager
        manager = new RestaurantManager();

        // Create time slots
        slot1 = new TimeSlot(LocalTime.of(12, 0), LocalTime.of(12, 30));
        slot2 = new TimeSlot(LocalTime.of(12, 30), LocalTime.of(13, 0));
        slot3 = new TimeSlot(LocalTime.of(13, 0), LocalTime.of(13, 30));

        // Create a restaurant with dishes and time slots
        restaurant = new Restaurant.Builder("Chez Luigi")
                .withTimeSlot(slot1)
                .withTimeSlot(slot2)
                .withTimeSlot(slot3)
                .withDish(new Dish("Pizza Margherita", "Tomato, mozzarella", 12.50))
                .withDish(new Dish("Pasta Carbonara", "Pasta with bacon", 11.00))
                .build();

        // Add restaurant to manager
        manager.addRestaurant(restaurant);
    }

    // ========== TESTS FOR getRestaurant() ==========

    @Test
    @DisplayName("Should return restaurant when it exists")
    void testGetRestaurant_Success() {
        Restaurant result = manager.getRestaurant("Chez Luigi");

        assertNotNull(result);
        assertEquals("Chez Luigi", result.getRestaurantName());
        assertEquals(2, result.getDishes().size());
    }

    @Test
    @DisplayName("Should return null when restaurant does not exist")
    void testGetRestaurant_NotFound() {
        Restaurant result = manager.getRestaurant("Non Existent Restaurant");

        assertNull(result);
    }

    @Test
    @DisplayName("Should throw exception when restaurant name is null")
    void testGetRestaurant_NullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.getRestaurant(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when restaurant name is empty")
    void testGetRestaurant_EmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.getRestaurant("");
        });
    }

    // ========== TESTS FOR blockTimeSlot() ==========

    @Test
    @DisplayName("Should block a time slot successfully")
    void testBlockTimeSlot_Success() {
        manager.blockTimeSlot(slot1, restaurant);

        List<TimeSlot> blockedSlots = manager.getBlockedTimeSlots(restaurant);

        assertEquals(1, blockedSlots.size());
        assertTrue(blockedSlots.contains(slot1));
    }

    @Test
    @DisplayName("Should block multiple time slots")
    void testBlockTimeSlot_Multiple() {
        manager.blockTimeSlot(slot1, restaurant);
        manager.blockTimeSlot(slot2, restaurant);

        List<TimeSlot> blockedSlots = manager.getBlockedTimeSlots(restaurant);

        assertEquals(2, blockedSlots.size());
        assertTrue(blockedSlots.contains(slot1));
        assertTrue(blockedSlots.contains(slot2));
    }

    @Test
    @DisplayName("Should not duplicate blocked time slots")
    void testBlockTimeSlot_NoDuplicates() {
        manager.blockTimeSlot(slot1, restaurant);
        manager.blockTimeSlot(slot1, restaurant); // Block same slot again

        List<TimeSlot> blockedSlots = manager.getBlockedTimeSlots(restaurant);

        assertEquals(1, blockedSlots.size());
    }

    @Test
    @DisplayName("Should throw exception when time slot is null")
    void testBlockTimeSlot_NullSlot() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.blockTimeSlot(null, restaurant);
        });
    }

    @Test
    @DisplayName("Should throw exception when restaurant is null")
    void testBlockTimeSlot_NullRestaurant() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.blockTimeSlot(slot1, null);
        });
    }

    // ========== TESTS FOR getAvailableTimeSlots() ==========

    @Test
    @DisplayName("Should return all time slots when none are blocked")
    void testGetAvailableTimeSlots_NoneBlocked() {
        List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);

        assertEquals(3, availableSlots.size());
        assertTrue(availableSlots.contains(slot1));
        assertTrue(availableSlots.contains(slot2));
        assertTrue(availableSlots.contains(slot3));
    }

    @Test
    @DisplayName("Should exclude blocked time slots from available slots")
    void testGetAvailableTimeSlots_WithBlockedSlots() {
        manager.blockTimeSlot(slot1, restaurant);
        manager.blockTimeSlot(slot2, restaurant);

        List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);

        assertEquals(1, availableSlots.size());
        assertFalse(availableSlots.contains(slot1));
        assertFalse(availableSlots.contains(slot2));
        assertTrue(availableSlots.contains(slot3));
    }

    @Test
    @DisplayName("Should return empty list when all slots are blocked")
    void testGetAvailableTimeSlots_AllBlocked() {
        manager.blockTimeSlot(slot1, restaurant);
        manager.blockTimeSlot(slot2, restaurant);
        manager.blockTimeSlot(slot3, restaurant);

        List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);

        assertTrue(availableSlots.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when restaurant is null")
    void testGetAvailableTimeSlots_NullRestaurant() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.getAvailableTimeSlots(null);
        });
    }

    @Test
    @DisplayName("Should return empty list for restaurant with no time slots")
    void testGetAvailableTimeSlots_NoTimeSlots() {
        Restaurant emptyRestaurant = new Restaurant("Empty Restaurant");
        manager.addRestaurant(emptyRestaurant);

        List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(emptyRestaurant);

        assertTrue(availableSlots.isEmpty());
    }

    // ========== TESTS FOR unblockTimeSlot() ==========

    @Test
    @DisplayName("Should unblock a previously blocked time slot")
    void testUnblockTimeSlot_Success() {
        manager.blockTimeSlot(slot1, restaurant);
        manager.blockTimeSlot(slot2, restaurant);

        manager.unblockTimeSlot(slot1, restaurant);

        List<TimeSlot> blockedSlots = manager.getBlockedTimeSlots(restaurant);
        List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);

        assertEquals(1, blockedSlots.size());
        assertFalse(blockedSlots.contains(slot1));
        assertTrue(blockedSlots.contains(slot2));

        assertEquals(2, availableSlots.size());
        assertTrue(availableSlots.contains(slot1));
    }

    @Test
    @DisplayName("Should handle unblocking non-blocked slot gracefully")
    void testUnblockTimeSlot_NotBlocked() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            manager.unblockTimeSlot(slot1, restaurant);
        });
    }

    @Test
    @DisplayName("Should throw exception when time slot is null")
    void testUnblockTimeSlot_NullSlot() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.unblockTimeSlot(null, restaurant);
        });
    }

    // ========== TESTS FOR UTILITY METHODS ==========

    @Test
    @DisplayName("Should add restaurant successfully")
    void testAddRestaurant_Success() {
        Restaurant newRestaurant = new Restaurant("Bella Italia");
        manager.addRestaurant(newRestaurant);

        Restaurant result = manager.getRestaurant("Bella Italia");
        assertNotNull(result);
        assertEquals("Bella Italia", result.getRestaurantName());
    }

    @Test
    @DisplayName("Should throw exception when adding null restaurant")
    void testAddRestaurant_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.addRestaurant(null);
        });
    }

    @Test
    @DisplayName("Should return all restaurants")
    void testGetAllRestaurants() {
        Restaurant restaurant2 = new Restaurant("Bella Italia");
        manager.addRestaurant(restaurant2);

        List<Restaurant> allRestaurants = manager.getAllRestaurants();

        assertEquals(2, allRestaurants.size());
    }

    @Test
    @DisplayName("Should check if restaurant exists")
    void testHasRestaurant() {
        assertTrue(manager.hasRestaurant("Chez Luigi"));
        assertFalse(manager.hasRestaurant("Non Existent"));
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Integration test: Complete workflow")
    void testCompleteWorkflow() {
        // 1. Get restaurant
        Restaurant resto = manager.getRestaurant("Chez Luigi");
        assertNotNull(resto);

        // 2. Check initial available slots
        List<TimeSlot> initialSlots = manager.getAvailableTimeSlots(resto);
        assertEquals(3, initialSlots.size());

        // 3. Block a slot
        manager.blockTimeSlot(slot2, resto);
        List<TimeSlot> afterBlock = manager.getAvailableTimeSlots(resto);
        assertEquals(2, afterBlock.size());
        assertFalse(afterBlock.contains(slot2));

        // 4. Unblock the slot
        manager.unblockTimeSlot(slot2, resto);
        List<TimeSlot> afterUnblock = manager.getAvailableTimeSlots(resto);
        assertEquals(3, afterUnblock.size());
        assertTrue(afterUnblock.contains(slot2));
    }

    @Test
    @DisplayName("Integration test: Multiple restaurants with blocked slots")
    void testMultipleRestaurants() {
        // Create second restaurant
        TimeSlot slot4 = new TimeSlot(LocalTime.of(18, 0), LocalTime.of(18, 30));
        Restaurant restaurant2 = new Restaurant.Builder("Bella Italia")
                .withTimeSlot(slot4)
                .build();
        manager.addRestaurant(restaurant2);

        // Block slots for different restaurants
        manager.blockTimeSlot(slot1, restaurant);
        manager.blockTimeSlot(slot4, restaurant2);

        // Check that blocking is independent per restaurant
        List<TimeSlot> availableRestaurant1 = manager.getAvailableTimeSlots(restaurant);
        List<TimeSlot> availableRestaurant2 = manager.getAvailableTimeSlots(restaurant2);

        assertEquals(2, availableRestaurant1.size());
        assertEquals(0, availableRestaurant2.size());

        assertFalse(availableRestaurant1.contains(slot1));
        assertTrue(availableRestaurant1.contains(slot2));
    }

    @Test
    @DisplayName("Edge case: Restaurant with same time slot multiple times")
    void testDuplicateTimeSlots() {
        Restaurant resto = new Restaurant.Builder("Test Restaurant")
                .withTimeSlot(slot1)
                .withTimeSlot(slot1) // Duplicate
                .build();
        manager.addRestaurant(resto);

        manager.blockTimeSlot(slot1, resto);
        List<TimeSlot> available = manager.getAvailableTimeSlots(resto);

        // Should block all instances of the same time slot
        assertEquals(0, available.size());
    }
}