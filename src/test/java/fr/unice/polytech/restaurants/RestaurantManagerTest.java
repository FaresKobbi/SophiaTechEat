package fr.unice.polytech.restaurants;





import fr.unice.polytech.dishes.DietaryLabel;
import fr.unice.polytech.dishes.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalTime;
import java.util.Arrays;
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
        restaurant1.setCapacity(slot1, 10);
        restaurant1.setCapacity(slot2, 15);
        restaurant1.setCapacity(slot3, 20);
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
            newPizzaPalace.addDish("Margherita", "Classic Italian pizza", 12.0);


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
        @DisplayName("Should block time slot by decreasing capacity")
        void shouldBlockTimeSlotByDecreasingCapacity() {
            manager.addRestaurant(restaurant1);
            int initialCapacity = restaurant1.getCapacity(slot1);

            manager.blockTimeSlot(slot1, restaurant1);

            assertEquals(initialCapacity - 1, restaurant1.getCapacity(slot1));
        }

        @Test
        @DisplayName("Should throw exception when blocking with null restaurant")
        void shouldThrowExceptionWhenBlockingWithNullRestaurant() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.blockTimeSlot(slot1, null));
            assertEquals("Restaurant cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should block multiple time slots independently")
        void shouldBlockMultipleTimeSlotsIndependently() {
            manager.addRestaurant(restaurant1);

            int capacity1Before = restaurant1.getCapacity(slot1);
            int capacity2Before = restaurant1.getCapacity(slot2);

            manager.blockTimeSlot(slot1, restaurant1);
            manager.blockTimeSlot(slot2, restaurant1);

            assertEquals(capacity1Before - 1, restaurant1.getCapacity(slot1));
            assertEquals(capacity2Before - 1, restaurant1.getCapacity(slot2));
        }

        @Test
        @DisplayName("Should not reduce capacity below zero")
        void shouldNotReduceCapacityBelowZero() {
            Restaurant restaurant = new Restaurant("Test");
            restaurant.setCapacity(slot1, 1);
            manager.addRestaurant(restaurant);

            manager.blockTimeSlot(slot1, restaurant);
            assertEquals(0, restaurant.getCapacity(slot1));

            manager.blockTimeSlot(slot1, restaurant);
            assertEquals(0, restaurant.getCapacity(slot1)); // Reste à 0
        }
    }

    // ==================== UNBLOCK TIME SLOT TESTS ====================

    @Nested
    @DisplayName("Unblock Time Slot Tests")
    class UnblockTimeSlotTests {

        @Test
        @DisplayName("Should unblock time slot by increasing capacity")
        void shouldUnblockTimeSlotByIncreasingCapacity() {
            manager.addRestaurant(restaurant1);

            int capacityBefore = restaurant1.getCapacity(slot1);
            manager.unblockTimeSlot(slot1, restaurant1);

            assertEquals(capacityBefore + 1, restaurant1.getCapacity(slot1));
        }

        @Test
        @DisplayName("Should allow unblocking without prior blocking")
        void shouldAllowUnblockingWithoutPriorBlocking() {
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
    @Test
    @DisplayName("Should exclude blocked time slots from available slots")
    void shouldExcludeBlockedTimeSlotsFromAvailableSlots() {
        manager.addRestaurant(restaurant1);

        // Réduire la capacité à 0 pour bloquer
        for (int i = 0; i < 10; i++) {
            manager.blockTimeSlot(slot1, restaurant1);
        }
        for (int i = 0; i < 15; i++) {
            manager.blockTimeSlot(slot2, restaurant1);
        }

        List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant1);

        assertEquals(1, availableSlots.size());
        assertFalse(availableSlots.contains(slot1));
        assertFalse(availableSlots.contains(slot2));
        assertTrue(availableSlots.contains(slot3));
    }

    @Test
    @DisplayName("Should return empty list when all slots have zero capacity")
    void shouldReturnEmptyListWhenAllSlotsHaveZeroCapacity() {
        manager.addRestaurant(restaurant1);

        // Mettre toutes les capacités à 0
        restaurant1.setCapacity(slot1, 0);
        restaurant1.setCapacity(slot2, 0);
        restaurant1.setCapacity(slot3, 0);

        List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant1);
        assertTrue(availableSlots.isEmpty());
    }



    



    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should correctly filter available slots based on capacity only")
    void shouldCorrectlyFilterAvailableSlotsBasedOnCapacity() {
        Restaurant restaurant = new Restaurant("Complex Restaurant");

        // slot1: capacity 10  available
        restaurant.setCapacity(slot1, 10);

        // slot2: capacity 0 -> not available
        restaurant.setCapacity(slot2, 0);

        // slot3: capacity 5, puis bloqué (capacité réduite) -> toujours disponible si capacité > 0
        restaurant.setCapacity(slot3, 5);

        manager.addRestaurant(restaurant);
        manager.blockTimeSlot(slot3, restaurant); // Réduit à 4

        List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);

        assertEquals(2, availableSlots.size()); // slot1 et slot3
        assertTrue(availableSlots.contains(slot1));
        assertFalse(availableSlots.contains(slot2));
        assertTrue(availableSlots.contains(slot3)); // Toujours disponible car capacité > 0
    }

    @Test
    @DisplayName("Should handle blocking and unblocking cycles")
    void shouldHandleBlockingAndUnblockingCycles() {
        manager.addRestaurant(restaurant1);

        int initialCapacity = restaurant1.getCapacity(slot1);
        int initialAvailable = manager.getAvailableTimeSlots(restaurant1).size();

        // Block slot1
        manager.blockTimeSlot(slot1, restaurant1);
        assertEquals(initialCapacity - 1, restaurant1.getCapacity(slot1));

        // Unblock slot1
        manager.unblockTimeSlot(slot1, restaurant1);
        assertEquals(initialCapacity, restaurant1.getCapacity(slot1));

        // Vérifier que le nombre de slots disponibles est revenu
        assertEquals(initialAvailable, manager.getAvailableTimeSlots(restaurant1).size());
    }
    // ==================== EDGE CASES TESTS ====================

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {


        @Test
        @DisplayName("Should handle blocking slot not in capacity map")
        void shouldHandleBlockingSlotNotInCapacityMap() {
            manager.addRestaurant(restaurant1);
            TimeSlot newSlot = new TimeSlot(LocalTime.of(18, 0), LocalTime.of(18, 30));

            // Ne devrait rien faire car le slot n'existe pas dans capacityMap
            assertDoesNotThrow(() -> manager.blockTimeSlot(newSlot, restaurant1));
            assertEquals(0, restaurant1.getCapacity(newSlot));
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
                r.setCapacity(slot1, 10);
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

                restaurant.setCapacity(morningSlot, 10);
                restaurant.setCapacity(eveningSlot, 10);
            }

            manager.addRestaurant(restaurant);

            List<TimeSlot> availableSlots = manager.getAvailableTimeSlots(restaurant);
            assertEquals(48, availableSlots.size());
        }

        @Test
        @DisplayName("Should handle many block/unblock operations efficiently")
        void shouldHandleManyBlockUnblockOperationsEfficiently() {
            manager.addRestaurant(restaurant1);
            int initialCapacity = restaurant1.getCapacity(slot1);

            // Perform 100 block/unblock operations
            for (int i = 0; i < 100; i++) {
                manager.blockTimeSlot(slot1, restaurant1);
                manager.unblockTimeSlot(slot1, restaurant1);
            }

            // La capacité devrait être revenue à l'initiale
            assertEquals(initialCapacity, restaurant1.getCapacity(slot1));
            assertEquals(3, manager.getAvailableTimeSlots(restaurant1).size());
        }
    }

    // ==================== SEARCH TESTS ====================
    @Nested
    @DisplayName("Search Tests")
    class SearchTests {

        private Restaurant italianRest;
        private Restaurant japaneseRest;
        private Restaurant mixedRest;
        private Dish glutenFreePasta;
        private Dish normalPizza;
        private Dish sushi;
        private Dish veganSalad;

        @BeforeEach
        void setUpSearchData() {
            // 1. Setup Italian Restaurant (Italian Cuisine)
            italianRest = new Restaurant("Luigi's");
            italianRest.setCuisineType(CuisineType.ITALIAN);

            glutenFreePasta = new Dish("GF Pasta", 12.0);
            glutenFreePasta.setDietaryLabels(List.of(DietaryLabel.GLUTEN_FREE, DietaryLabel.VEGETARIAN));

            normalPizza = new Dish("Pizza", 10.0);
            normalPizza.setDietaryLabels(List.of(DietaryLabel.VEGETARIAN)); // No GF label

            italianRest.addDish(glutenFreePasta.getName(), "Desc", glutenFreePasta.getPrice());
            italianRest.addDish(normalPizza.getName(), "Desc", normalPizza.getPrice());

            // Manually setting labels because addDish(name, desc, price) creates a generic Dish inside Restaurant
            // We need to update the created dishes with our specific labels
            italianRest.getDishes().stream()
                    .filter(d -> d.getName().equals("GF Pasta"))
                    .findFirst().ifPresent(d -> d.setDietaryLabels(List.of(DietaryLabel.GLUTEN_FREE, DietaryLabel.VEGETARIAN)));

            italianRest.getDishes().stream()
                    .filter(d -> d.getName().equals("Pizza"))
                    .findFirst().ifPresent(d -> d.setDietaryLabels(List.of(DietaryLabel.VEGETARIAN)));


            // 2. Setup Japanese Restaurant (Japanese Cuisine)
            japaneseRest = new Restaurant("Sushi Zen");
            japaneseRest.setCuisineType(CuisineType.JAPANESE);

            sushi = new Dish("Sushi Set", 15.0);
            sushi.setDietaryLabels(List.of(DietaryLabel.GLUTEN_FREE)); // GF but not Vegetarian/Vegan explicitly here

            japaneseRest.addDish(sushi.getName(), "Desc", sushi.getPrice());
            japaneseRest.getDishes().get(0).setDietaryLabels(List.of(DietaryLabel.GLUTEN_FREE));


            // 3. Setup Mixed/General Restaurant (General Cuisine)
            mixedRest = new Restaurant("Healthy Spot");
            mixedRest.setCuisineType(CuisineType.GENERAL);

            veganSalad = new Dish("Super Salad", 9.0);
            veganSalad.setDietaryLabels(List.of(DietaryLabel.VEGAN, DietaryLabel.VEGETARIAN, DietaryLabel.GLUTEN_FREE));

            mixedRest.addDish(veganSalad.getName(), "Desc", veganSalad.getPrice());
            mixedRest.getDishes().get(0).setDietaryLabels(List.of(DietaryLabel.VEGAN, DietaryLabel.VEGETARIAN, DietaryLabel.GLUTEN_FREE));


            // Add all to manager
            manager.addRestaurant(italianRest);
            manager.addRestaurant(japaneseRest);
            manager.addRestaurant(mixedRest);
        }

        // --- Search by Cuisine ---

        @Test
        @DisplayName("Should find restaurants by specific cuisine")
        void shouldFindRestaurantsByCuisine() {
            List<Restaurant> italianResults = manager.searchByCuisine(CuisineType.ITALIAN);
            assertEquals(1, italianResults.size());
            assertTrue(italianResults.contains(italianRest));

            List<Restaurant> japaneseResults = manager.searchByCuisine(CuisineType.JAPANESE);
            assertEquals(1, japaneseResults.size());
            assertTrue(japaneseResults.contains(japaneseRest));
        }

        @Test
        @DisplayName("Should return empty list for cuisine with no restaurants")
        void shouldReturnEmptyForMissingCuisine() {
            List<Restaurant> frenchResults = manager.searchByCuisine(CuisineType.FRENCH);
            assertTrue(frenchResults.isEmpty());
        }

        @Test
        @DisplayName("Should return all restaurants when cuisine is null")
        void shouldReturnAllRestaurantsWhenCuisineIsNull() {
            List<Restaurant> results = manager.searchByCuisine(null);
            assertEquals(3, results.size());
        }

        // --- Search by Dietary Label ---

        @Test
        @DisplayName("Should find restaurants having at least one dish with the dietary label")
        void shouldFindRestaurantsByDietaryLabel() {
            // Italian has GF Pasta, Japanese has Sushi (GF), Mixed has Salad (GF)
            List<Restaurant> gfResults = manager.searchByDietaryLabel(DietaryLabel.GLUTEN_FREE);
            assertEquals(3, gfResults.size());

            // Only Mixed has VEGAN
            List<Restaurant> veganResults = manager.searchByDietaryLabel(DietaryLabel.VEGAN);
            assertEquals(1, veganResults.size());
            assertTrue(veganResults.contains(mixedRest));
        }

        @Test
        @DisplayName("Should return empty list for label found in no dishes")
        void shouldReturnEmptyForMissingLabel() {
            // Assume we add HALAL to enum but no dish has it
            List<Restaurant> halalResults = manager.searchByDietaryLabel(DietaryLabel.HALAL);
            assertTrue(halalResults.isEmpty());
        }

        @Test
        @DisplayName("Should return all restaurants when dietary label is null")
        void shouldReturnAllRestaurantsWhenLabelIsNull() {
            List<Restaurant> results = manager.searchByDietaryLabel(null);
            assertEquals(3, results.size());
        }

        // --- Combined Search ---

        @Test
        @DisplayName("Should filter by both Cuisine AND Dietary Label")
        void shouldFilterByBothCuisineAndLabel() {
            // Italian + Vegetarian (Both Luigi's and Healthy Spot have veg options, but only Luigi is Italian)
            List<Restaurant> results = manager.search(CuisineType.ITALIAN, List.of(DietaryLabel.VEGETARIAN));

            assertEquals(1, results.size());
            assertTrue(results.contains(italianRest));
        }

        @Test
        @DisplayName("Should return empty if cuisine matches but label does not")
        void shouldReturnEmptyIfCuisineMatchesButLabelDoesNot() {
            // Italian restaurant exists, but assumes no VEGAN dish in Luigi's
            List<Restaurant> results = manager.search(CuisineType.ITALIAN,List.of(DietaryLabel.VEGAN));
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should return empty if label matches but cuisine does not")
        void shouldReturnEmptyIfLabelMatchesButCuisineDoesNot() {
            // Japanese restaurant exists, but assumes Sushi is not VEGAN
            List<Restaurant> results = manager.search(CuisineType.JAPANESE, List.of(DietaryLabel.VEGAN));
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should default to cuisine search if label is null")
        void shouldDefaultToCuisineSearchIfLabelIsNull() {
            List<Restaurant> results = manager.search(CuisineType.ITALIAN, null);
            assertEquals(1, results.size());
            assertTrue(results.contains(italianRest));
        }

        @Test
        @DisplayName("Should default to label search if cuisine is null")
        void shouldDefaultToLabelSearchIfCuisineIsNull() {
            // Vegetarian matches Italian and Mixed
            List<Restaurant> results = manager.search(null, List.of(DietaryLabel.VEGETARIAN));
            assertEquals(2, results.size());
            assertTrue(results.contains(italianRest));
            assertTrue(results.contains(mixedRest));
            assertFalse(results.contains(japaneseRest));
        }

        @Test
        @DisplayName("Should return all restaurants if both arguments are null")
        void shouldReturnAllIfBothNull() {
            List<Restaurant> results = manager.search(null, null);
            assertEquals(3, results.size());
        }
    }
}