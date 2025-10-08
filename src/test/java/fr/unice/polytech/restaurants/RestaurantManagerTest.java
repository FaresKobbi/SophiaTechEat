package fr.unice.polytech.restaurants;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.dishes.DishCategory;
import fr.unice.polytech.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RestaurantManager Tests")
class RestaurantManagerTest {

    private RestaurantManager manager;
    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private TimeSlot slot1;
    private TimeSlot slot2;
    private TimeSlot slot3;

    @BeforeEach
    void setUp() {
        manager = new RestaurantManager();

        // Create sample restaurants
        restaurant1 = new Restaurant("Pizza Palace");
        restaurant2 = new Restaurant("Pasta House");

        // Create sample time slots
        slot1 = new TimeSlot(LocalTime.of(11, 0), LocalTime.of(11, 30));
        slot2 = new TimeSlot(LocalTime.of(11, 30), LocalTime.of(12, 0));
        slot3 = new TimeSlot(LocalTime.of(12, 0), LocalTime.of(12, 30));

        // Setup some capacities for restaurant1
        restaurant1.setCapacityByTimeSlot(slot1, 10);
        restaurant1.setCapacityByTimeSlot(slot2, 15);
        restaurant1.setCapacityByTimeSlot(slot3, 20);
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty RestaurantManager")
        void shouldCreateEmptyRestaurantManager() {
            RestaurantManager newManager = new RestaurantManager();
            assertNotNull(newManager);
            assertTrue(newManager.getAllRestaurants().isEmpty());
        }
    }

    // ==================== ADD RESTAURANT TESTS ====================

    @Nested
    @DisplayName("Add Restaurant Tests")
    class AddRestaurantTests {

        @Test
        @DisplayName("Should add restaurant successfully")
        void shouldAddRestaurantSuccessfully() {
            manager.addRestaurant(restaurant1);

            assertTrue(manager.hasRestaurant("Pizza Palace"));
            assertEquals(1, manager.getAllRestaurants().size());
        }

        @Test
        @DisplayName("Should add multiple restaurants")
        void shouldAddMultipleRestaurants() {
            manager.addRestaurant(restaurant1);
            manager.addRestaurant(restaurant2);

            assertEquals(2, manager.getAllRestaurants().size());
            assertTrue(manager.hasRestaurant("Pizza Palace"));
            assertTrue(manager.hasRestaurant("Pasta House"));
        }

        @Test
        @DisplayName("Should throw exception when adding null restaurant")
        void shouldThrowExceptionWhenAddingNullRestaurant() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.addRestaurant(null));
            assertEquals("Restaurant cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should replace restaurant with same name")
        void shouldReplaceRestaurantWithSameName() {
            manager.addRestaurant(restaurant1);

            Restaurant newPizzaPalace = new Restaurant("Pizza Palace");
            Dish pizza = new Dish("Margherita", "Classic Italian pizza", 12.0);
            pizza.setCategory(DishCategory.MAIN_COURSE);
            newPizzaPalace.addDish(pizza);

            manager.addRestaurant(newPizzaPalace);

            assertEquals(1, manager.getAllRestaurants().size());
            Restaurant retrieved = manager.getRestaurant("Pizza Palace");
            assertEquals(1, retrieved.getDishes().size());
        }
    }

    // ==================== GET RESTAURANT TESTS ====================

    @Nested
    @DisplayName("Get Restaurant Tests")
    class GetRestaurantTests {

        @Test
        @DisplayName("Should get restaurant by name")
        void shouldGetRestaurantByName() {
            manager.addRestaurant(restaurant1);

            Restaurant retrieved = manager.getRestaurant("Pizza Palace");

            assertNotNull(retrieved);
            assertEquals("Pizza Palace", retrieved.getRestaurantName());
        }

        @Test
        @DisplayName("Should return null for non-existent restaurant")
        void shouldReturnNullForNonExistentRestaurant() {
            Restaurant retrieved = manager.getRestaurant("Unknown Restaurant");
            assertNull(retrieved);
        }

        @Test
        @DisplayName("Should throw exception when getting restaurant with null name")
        void shouldThrowExceptionWhenGettingRestaurantWithNullName() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.getRestaurant(null));
            assertEquals("Restaurant name cannot be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when getting restaurant with empty name")
        void shouldThrowExceptionWhenGettingRestaurantWithEmptyName() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.getRestaurant(""));
            assertEquals("Restaurant name cannot be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should get all restaurants")
        void shouldGetAllRestaurants() {
            manager.addRestaurant(restaurant1);
            manager.addRestaurant(restaurant2);

            List<Restaurant> allRestaurants = manager.getAllRestaurants();

            assertEquals(2, allRestaurants.size());
            assertTrue(allRestaurants.contains(restaurant1));
            assertTrue(allRestaurants.contains(restaurant2));
        }

        @Test
        @DisplayName("Should return empty list when no restaurants")
        void shouldReturnEmptyListWhenNoRestaurants() {
            List<Restaurant> allRestaurants = manager.getAllRestaurants();
            assertNotNull(allRestaurants);
            assertTrue(allRestaurants.isEmpty());
        }
    }

    // ==================== HAS RESTAURANT TESTS ====================

    @Nested
    @DisplayName("Has Restaurant Tests")
    class HasRestaurantTests {

        @Test
        @DisplayName("Should return true when restaurant exists")
        void shouldReturnTrueWhenRestaurantExists() {
            manager.addRestaurant(restaurant1);
            assertTrue(manager.hasRestaurant("Pizza Palace"));
        }

        @Test
        @DisplayName("Should return false when restaurant does not exist")
        void shouldReturnFalseWhenRestaurantDoesNotExist() {
            assertFalse(manager.hasRestaurant("Unknown Restaurant"));
        }

        @Test
        @DisplayName("Should return false for null restaurant name")
        void shouldReturnFalseForNullRestaurantName() {
            assertFalse(manager.hasRestaurant(null));
        }
    }

    // ==================== BLOCK TIME SLOT TESTS ====================

    @Nested
    @DisplayName("Block Time Slot Tests")
    class BlockTimeSlotTests {

        @Test
        @DisplayName("Should block time slot for restaurant")
        void shouldBlockTimeSlotForRestaurant() {
            manager.addRestaurant(restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);

            assertTrue(restaurant1.isTimeSlotBlocked(slot1));
        }

        @Test
        @DisplayName("Should not block already blocked time slot")
        void shouldNotBlockAlreadyBlockedTimeSlot() {
            manager.addRestaurant(restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);

            assertEquals(1, restaurant1.getBlockedTimeSlots().size());
        }

        @Test
        @DisplayName("Should throw exception when blocking with null restaurant")
        void shouldThrowExceptionWhenBlockingWithNullRestaurant() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.blockTimeSlot(slot1, null));
            assertEquals("Restaurant cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should block multiple time slots")
        void shouldBlockMultipleTimeSlots() {
            manager.addRestaurant(restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);
            manager.blockTimeSlot(slot2, restaurant1);

            assertTrue(restaurant1.isTimeSlotBlocked(slot1));
            assertTrue(restaurant1.isTimeSlotBlocked(slot2));
            assertFalse(restaurant1.isTimeSlotBlocked(slot3));
        }
    }

    // ==================== UNBLOCK TIME SLOT TESTS ====================

    @Nested
    @DisplayName("Unblock Time Slot Tests")
    class UnblockTimeSlotTests {

        @Test
        @DisplayName("Should unblock time slot for restaurant")
        void shouldUnblockTimeSlotForRestaurant() {
            manager.addRestaurant(restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);
            manager.unblockTimeSlot(slot1, restaurant1);

            assertFalse(restaurant1.isTimeSlotBlocked(slot1));
        }

        @Test
        @DisplayName("Should not throw error when unblocking non-blocked slot")
        void shouldNotThrowErrorWhenUnblockingNonBlockedSlot() {
            manager.addRestaurant(restaurant1);
            assertDoesNotThrow(() -> manager.unblockTimeSlot(slot1, restaurant1));
        }

        @Test
        @DisplayName("Should throw exception when unblocking with null restaurant")
        void shouldThrowExceptionWhenUnblockingWithNullRestaurant() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.unblockTimeSlot(slot1, null));
            assertEquals("Restaurant cannot be null", exception.getMessage());
        }
    }

    // ==================== GET AVAILABLE TIME SLOTS TESTS ====================

    @Nested
    @DisplayName("Get Available Time Slots Tests")
    class GetAvailableTimeSlotsTests {

        @Test
        @DisplayName("Should get all available time slots")
        void shouldGetAllAvailableTimeSlots() {
            manager.addRestaurant(restaurant1);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant1);

            assertEquals(3, availableSlots.size());
            assertTrue(availableSlots.contains(slot1));
            assertTrue(availableSlots.contains(slot2));
            assertTrue(availableSlots.contains(slot3));
        }

        @Test
        @DisplayName("Should exclude blocked time slots from available slots")
        void shouldExcludeBlockedTimeSlotsFromAvailableSlots() {
            manager.addRestaurant(restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);
            manager.blockTimeSlot(slot2, restaurant1);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant1);

            assertEquals(1, availableSlots.size());
            assertFalse(availableSlots.contains(slot1));
            assertFalse(availableSlots.contains(slot2));
            assertTrue(availableSlots.contains(slot3));
        }

        @Test
        @DisplayName("Should return empty list when all slots are blocked")
        void shouldReturnEmptyListWhenAllSlotsAreBlocked() {
            manager.addRestaurant(restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);
            manager.blockTimeSlot(slot2, restaurant1);
            manager.blockTimeSlot(slot3, restaurant1);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant1);

            assertTrue(availableSlots.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list when restaurant has no time slots")
        void shouldReturnEmptyListWhenRestaurantHasNoTimeSlots() {
            manager.addRestaurant(restaurant2);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant2);

            assertTrue(availableSlots.isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when getting available slots with null restaurant")
        void shouldThrowExceptionWhenGettingAvailableSlotsWithNullRestaurant() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.getAvailableTimeSlots(null));
            assertEquals("Restaurant cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should not include slots with zero capacity")
        void shouldNotIncludeSlotsWithZeroCapacity() {
            Restaurant restaurant = new Restaurant("Test Restaurant");
            restaurant.setCapacityByTimeSlot(slot1, 10);
            restaurant.setCapacityByTimeSlot(slot2, 0);
            manager.addRestaurant(restaurant);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);

            assertEquals(1, availableSlots.size());
            assertTrue(availableSlots.contains(slot1));
            assertFalse(availableSlots.contains(slot2));
        }
    }

    // ==================== GET BLOCKED TIME SLOTS TESTS ====================

    @Nested
    @DisplayName("Get Blocked Time Slots Tests")
    class GetBlockedTimeSlotsTests {

        @Test
        @DisplayName("Should get all blocked time slots for restaurant")
        void shouldGetAllBlockedTimeSlotsForRestaurant() {
            manager.addRestaurant(restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);
            manager.blockTimeSlot(slot2, restaurant1);

            List<TimeSlot> blockedSlots = manager.getBlockedTimeSlots(restaurant1);

            assertEquals(2, blockedSlots.size());
            assertTrue(blockedSlots.contains(slot1));
            assertTrue(blockedSlots.contains(slot2));
        }

        @Test
        @DisplayName("Should return empty list when no slots are blocked")
        void shouldReturnEmptyListWhenNoSlotsAreBlocked() {
            manager.addRestaurant(restaurant1);

            List<TimeSlot> blockedSlots = manager.getBlockedTimeSlots(restaurant1);

            assertNotNull(blockedSlots);
            assertTrue(blockedSlots.isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when getting blocked slots with null restaurant")
        void shouldThrowExceptionWhenGettingBlockedSlotsWithNullRestaurant() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.getBlockedTimeSlots(null));
            assertEquals("Restaurant cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should return immutable copy of blocked time slots")
        void shouldReturnImmutableCopyOfBlockedTimeSlots() {
            manager.addRestaurant(restaurant1);
            manager.blockTimeSlot(slot1, restaurant1);

            List<TimeSlot> blockedSlots = manager.getBlockedTimeSlots(restaurant1);
            blockedSlots.add(slot2);

            assertEquals(1, manager.getBlockedTimeSlots(restaurant1).size());
        }
    }

    // ==================== INTEGRATION TESTS ====================

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should manage multiple restaurants with different time slots")
        void shouldManageMultipleRestaurantsWithDifferentTimeSlots() {
            // Setup restaurant1 with 3 time slots
            restaurant1.setCapacityByTimeSlot(slot1, 10);
            restaurant1.setCapacityByTimeSlot(slot2, 15);
            restaurant1.setCapacityByTimeSlot(slot3, 20);

            // Setup restaurant2 with 2 time slots
            restaurant2.setCapacityByTimeSlot(slot1, 5);
            restaurant2.setCapacityByTimeSlot(slot2, 8);

            manager.addRestaurant(restaurant1);
            manager.addRestaurant(restaurant2);

            // Block some slots
            manager.blockTimeSlot(slot1, restaurant1);
            manager.blockTimeSlot(slot2, restaurant2);

            // Verify restaurant1
            List<TimeSlot> available1 = manager.getAvailableTimeSlots(restaurant1);
            assertEquals(2, available1.size());
            assertFalse(available1.contains(slot1));

            // Verify restaurant2
            List<TimeSlot> available2 = manager.getAvailableTimeSlots(restaurant2);
            assertEquals(1, available2.size());
            assertFalse(available2.contains(slot2));
        }

        @Test
        @DisplayName("Should handle blocking and unblocking cycles")
        void shouldHandleBlockingAndUnblockingCycles() {
            manager.addRestaurant(restaurant1);

            // Block slot1
            manager.blockTimeSlot(slot1, restaurant1);
            assertTrue(restaurant1.isTimeSlotBlocked(slot1));
            assertEquals(2, manager.getAvailableTimeSlots(restaurant1).size());

            // Unblock slot1
            manager.unblockTimeSlot(slot1, restaurant1);
            assertFalse(restaurant1.isTimeSlotBlocked(slot1));
            assertEquals(3, manager.getAvailableTimeSlots(restaurant1).size());

            // Block again
            manager.blockTimeSlot(slot1, restaurant1);
            assertTrue(restaurant1.isTimeSlotBlocked(slot1));
        }

        @Test
        @DisplayName("Should correctly filter available slots based on capacity and blocked status")
        void shouldCorrectlyFilterAvailableSlotsBasedOnCapacityAndBlockedStatus() {
            Restaurant restaurant = new Restaurant("Complex Restaurant");

            // slot1: capacity 10, not blocked → available
            restaurant.setCapacityByTimeSlot(slot1, 10);

            // slot2: capacity 0, not blocked → not available
            restaurant.setCapacityByTimeSlot(slot2, 0);

            // slot3: capacity 5, blocked → not available
            restaurant.setCapacityByTimeSlot(slot3, 5);

            manager.addRestaurant(restaurant);
            manager.blockTimeSlot(slot3, restaurant);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);

            assertEquals(1, availableSlots.size());
            assertTrue(availableSlots.contains(slot1));
            assertFalse(availableSlots.contains(slot2));
            assertFalse(availableSlots.contains(slot3));
        }

        @Test
        @DisplayName("Should maintain independent state for multiple restaurants")
        void shouldMaintainIndependentStateForMultipleRestaurants() {
            manager.addRestaurant(restaurant1);
            manager.addRestaurant(restaurant2);

            // Block slot1 only for restaurant1
            manager.blockTimeSlot(slot1, restaurant1);

            // Verify restaurant1 has slot1 blocked
            assertTrue(restaurant1.isTimeSlotBlocked(slot1));

            // Verify restaurant2 does not have slot1 blocked
            assertFalse(restaurant2.isTimeSlotBlocked(slot1));
        }


        @Test
        @DisplayName("Should handle empty manager operations gracefully")
        void shouldHandleEmptyManagerOperationsGracefully() {
            // Get all restaurants from empty manager
            assertTrue(manager.getAllRestaurants().isEmpty());

            // Check for non-existent restaurant
            assertFalse(manager.hasRestaurant("Any Restaurant"));

            // Get non-existent restaurant
            assertNull(manager.getRestaurant("Any Restaurant"));
        }
    }

    // ==================== EDGE CASES TESTS ====================

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle restaurant with no time slots")
        void shouldHandleRestaurantWithNoTimeSlots() {
            Restaurant emptyRestaurant = new Restaurant("Empty Restaurant");
            manager.addRestaurant(emptyRestaurant);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(emptyRestaurant);
            List<TimeSlot> blockedSlots = manager.getBlockedTimeSlots(emptyRestaurant);

            assertTrue(availableSlots.isEmpty());
            assertTrue(blockedSlots.isEmpty());
        }

        @Test
        @DisplayName("Should handle blocking non-existent time slot")
        void shouldHandleBlockingNonExistentTimeSlot() {
            manager.addRestaurant(restaurant1);

            TimeSlot newSlot = new TimeSlot(LocalTime.of(18, 0), LocalTime.of(18, 30));

            // Should not throw exception
            assertDoesNotThrow(() -> manager.blockTimeSlot(newSlot, restaurant1));

            // Slot should be blocked even if not in capacity map
            assertTrue(restaurant1.isTimeSlotBlocked(newSlot));
        }

        @Test
        @DisplayName("Should handle unblocking never-blocked time slot")
        void shouldHandleUnblockingNeverBlockedTimeSlot() {
            manager.addRestaurant(restaurant1);

            // Should not throw exception
            assertDoesNotThrow(() -> manager.unblockTimeSlot(slot1, restaurant1));

            assertFalse(restaurant1.isTimeSlotBlocked(slot1));
        }

        @Test
        @DisplayName("Should handle restaurant name with special characters")
        void shouldHandleRestaurantNameWithSpecialCharacters() {
            Restaurant specialRestaurant = new Restaurant("Café & Restaurant #1");
            manager.addRestaurant(specialRestaurant);

            assertTrue(manager.hasRestaurant("Café & Restaurant #1"));
            assertNotNull(manager.getRestaurant("Café & Restaurant #1"));
        }

        @Test
        @DisplayName("Should handle very long restaurant name")
        void shouldHandleVeryLongRestaurantName() {
            String longName = "A".repeat(500);
            Restaurant longNameRestaurant = new Restaurant(longName);
            manager.addRestaurant(longNameRestaurant);

            assertTrue(manager.hasRestaurant(longName));
            assertEquals(longName, manager.getRestaurant(longName).getRestaurantName());
        }
    }

    // ==================== PERFORMANCE TESTS ====================

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle many restaurants efficiently")
        void shouldHandleManyRestaurantsEfficiently() {
            int restaurantCount = 100;

            for (int i = 0; i < restaurantCount; i++) {
                Restaurant r = new Restaurant("Restaurant " + i);
                r.setCapacityByTimeSlot(slot1, 10);
                manager.addRestaurant(r);
            }

            assertEquals(restaurantCount, manager.getAllRestaurants().size());

            // Should still perform quickly
            Restaurant restaurant = manager.getRestaurant("Restaurant 50");
            assertNotNull(restaurant);
            assertEquals("Restaurant 50", restaurant.getRestaurantName());
        }

        @Test
        @DisplayName("Should handle many time slots per restaurant efficiently")
        void shouldHandleManyTimeSlotsPerRestaurantEfficiently() {
            Restaurant restaurant = new Restaurant("Busy Restaurant");

            // Create 48 time slots (full day in 30-minute intervals)
            for (int hour = 0; hour < 24; hour++) {
                TimeSlot morningSlot = new TimeSlot(
                        LocalTime.of(hour, 0),
                        LocalTime.of(hour, 30)
                );
                TimeSlot eveningSlot = new TimeSlot(
                        LocalTime.of(hour, 30),
                        LocalTime.of((hour + 1) % 24, 0)
                );

                restaurant.setCapacityByTimeSlot(morningSlot, 10);
                restaurant.setCapacityByTimeSlot(eveningSlot, 10);
            }

            manager.addRestaurant(restaurant);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);
            assertEquals(48, availableSlots.size());
        }

        @Test
        @DisplayName("Should handle many block/unblock operations efficiently")
        void shouldHandleManyBlockUnblockOperationsEfficiently() {
            manager.addRestaurant(restaurant1);

            // Perform 1000 block/unblock operations
            for (int i = 0; i < 1000; i++) {
                manager.blockTimeSlot(slot1, restaurant1);
                manager.unblockTimeSlot(slot1, restaurant1);
            }

            // Should still be in correct state
            assertFalse(restaurant1.isTimeSlotBlocked(slot1));
            assertEquals(3, manager.getAvailableTimeSlots(restaurant1).size());
        }
    }
}