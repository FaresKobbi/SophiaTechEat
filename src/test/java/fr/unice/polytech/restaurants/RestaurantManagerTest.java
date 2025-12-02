package fr.unice.polytech.restaurants;





import fr.unice.polytech.dishes.DietaryLabel;
import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.dishes.DishCategory;
import fr.unice.polytech.dishes.DishType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.DayOfWeek;
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

        
        restaurant1 = new Restaurant("Pizza Palace");
        restaurant2 = new Restaurant("Pasta House");

        
        slot1 = new TimeSlot(LocalTime.of(11, 0), LocalTime.of(11, 30));
        slot2 = new TimeSlot(LocalTime.of(11, 30), LocalTime.of(12, 0));
        slot3 = new TimeSlot(LocalTime.of(12, 0), LocalTime.of(12, 30));

        OpeningHours hours = new OpeningHours(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
        restaurant1.addOpeningHours(hours);

        restaurant1.updateSlotCapacity(DayOfWeek.MONDAY, slot1.getStartTime(), slot1.getEndTime(), 10);
        restaurant1.updateSlotCapacity(DayOfWeek.MONDAY, slot2.getStartTime(), slot2.getEndTime(), 15);
        restaurant1.updateSlotCapacity(DayOfWeek.MONDAY, slot3.getStartTime(), slot3.getEndTime(), 20);

    }

    

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

    

    @Nested
    @DisplayName("Block Time Slot Tests")
    class BlockTimeSlotTests {












        @Test
        @DisplayName("Should throw exception when blocking with null restaurant")
        void shouldThrowExceptionWhenBlockingWithNullRestaurant() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> manager.blockTimeSlot(slot1, null));
            assertEquals("Restaurant cannot be null", exception.getMessage());
        }





























    }

    

    @Nested
    @DisplayName("Unblock Time Slot Tests")
    class UnblockTimeSlotTests {












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

    





































    



    













































    

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {


        @Test
        @DisplayName("Should handle blocking slot not in capacity map")
        void shouldHandleBlockingSlotNotInCapacityMap() {
            manager.addRestaurant(restaurant1);
            TimeSlot newSlot = new TimeSlot(LocalTime.of(18, 0), LocalTime.of(18, 30));

            
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

    

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {














































        @Test
        @DisplayName("Should handle many block/unblock operations efficiently")
        void shouldHandleManyBlockUnblockOperationsEfficiently() {
            manager.addRestaurant(restaurant1);
            int initialCapacity = restaurant1.getCapacity(slot1);

            
            for (int i = 0; i < 100; i++) {
                manager.blockTimeSlot(slot1, restaurant1);
                manager.unblockTimeSlot(slot1, restaurant1);
            }

            
            assertEquals(initialCapacity, restaurant1.getCapacity(slot1));
            assertEquals(3, manager.getAvailableTimeSlots(restaurant1).size());
        }
    }

    
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
            
            italianRest = new Restaurant("Luigi's");
            italianRest.setCuisineType(CuisineType.ITALIAN);

            
            italianRest.addDish("GF Pasta", "Corn pasta", 12.0);
            Dish pasta = italianRest.findDishByName("GF Pasta");
            if (pasta != null) {
                pasta.setDietaryLabels(List.of(DietaryLabel.GLUTEN_FREE, DietaryLabel.VEGETARIAN));
                
                italianRest.addDietaryLabel(DietaryLabel.GLUTEN_FREE);
                italianRest.addDietaryLabel(DietaryLabel.VEGETARIAN);
            }

            
            italianRest.addDish("Pizza", "Cheese and tomato", 10.0);
            Dish pizza = italianRest.findDishByName("Pizza");
            if (pizza != null) {
                pizza.setDietaryLabels(List.of(DietaryLabel.VEGETARIAN));
                
                italianRest.addDietaryLabel(DietaryLabel.VEGETARIAN);
            }


            
            japaneseRest = new Restaurant("Sushi Zen");
            japaneseRest.setCuisineType(CuisineType.JAPANESE);

            
            japaneseRest.addDish("Sushi Set", "Fresh fish", 15.0);
            Dish sushi = japaneseRest.findDishByName("Sushi Set");
            if (sushi != null) {
                sushi.setDietaryLabels(List.of(DietaryLabel.GLUTEN_FREE));
                
                japaneseRest.addDietaryLabel(DietaryLabel.GLUTEN_FREE);
            }


            
            mixedRest = new Restaurant("Healthy Spot");
            mixedRest.setCuisineType(CuisineType.GENERAL);

            
            mixedRest.addDish("Super Salad", "Lettuce and tofu", 9.0);
            Dish salad = mixedRest.findDishByName("Super Salad");
            if (salad != null) {
                List<DietaryLabel> labels = List.of(DietaryLabel.VEGAN, DietaryLabel.VEGETARIAN, DietaryLabel.GLUTEN_FREE);
                salad.setDietaryLabels(labels);
                
                labels.forEach(mixedRest::addDietaryLabel);
            }


            
            manager.addRestaurant(italianRest);
            manager.addRestaurant(japaneseRest);
            manager.addRestaurant(mixedRest);
        }

        

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

        

        @Test
        @DisplayName("Should find restaurants having at least one dish with the dietary label")
        void shouldFindRestaurantsByDietaryLabel() {
            
            List<Restaurant> gfResults = manager.searchByDietaryLabel(DietaryLabel.GLUTEN_FREE);
            assertEquals(3, gfResults.size());

            
            List<Restaurant> veganResults = manager.searchByDietaryLabel(DietaryLabel.VEGAN);
            assertEquals(1, veganResults.size());
            assertTrue(veganResults.contains(mixedRest));
        }

        @Test
        @DisplayName("Should return empty list for label found in no dishes")
        void shouldReturnEmptyForMissingLabel() {
            
            List<Restaurant> halalResults = manager.searchByDietaryLabel(DietaryLabel.HALAL);
            assertTrue(halalResults.isEmpty());
        }

        @Test
        @DisplayName("Should return all restaurants when dietary label is null")
        void shouldReturnAllRestaurantsWhenLabelIsNull() {
            List<Restaurant> results = manager.searchByDietaryLabel(null);
            assertEquals(3, results.size());
        }

        

        @Test
        @DisplayName("Should filter by both Cuisine AND Dietary Label")
        void shouldFilterByBothCuisineAndLabel() {
            
            List<Restaurant> results = manager.search(CuisineType.ITALIAN, List.of(DietaryLabel.VEGETARIAN));

            assertEquals(1, results.size());
            assertTrue(results.contains(italianRest));
        }

        @Test
        @DisplayName("Should return empty if cuisine matches but label does not")
        void shouldReturnEmptyIfCuisineMatchesButLabelDoesNot() {
            
            List<Restaurant> results = manager.search(CuisineType.ITALIAN,List.of(DietaryLabel.VEGAN));
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should return empty if label matches but cuisine does not")
        void shouldReturnEmptyIfLabelMatchesButCuisineDoesNot() {
            
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