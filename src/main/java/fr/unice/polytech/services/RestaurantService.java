package fr.unice.polytech.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.dishes.DietaryLabel;
import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.dishes.DishCategory;
import fr.unice.polytech.dishes.DishType;
import fr.unice.polytech.restaurants.CuisineType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.server.ApiRegistry;
import fr.unice.polytech.server.SimpleServer;
import fr.unice.polytech.services.handlers.restaurant.DynamicRestaurantHandler;
import fr.unice.polytech.services.handlers.restaurant.StaticRestaurantHandler;
import fr.unice.polytech.services.handlers.suggestion.SuggestionHandler;
import fr.unice.polytech.suggestion.HybridSuggestionService;

import java.io.IOException;
import java.util.List;

public class RestaurantService {
        private static final RestaurantManager restaurantManager = new RestaurantManager();
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static void main(String[] args) throws IOException {

                populateMockData();

                objectMapper.registerModule(new JavaTimeModule());

                // MOCK
                restaurantManager.addRestaurant(new Restaurant("Pizza Palace"));
                restaurantManager.addRestaurant(new Restaurant("Sushi Shop"));
                restaurantManager.getRestaurant("Sushi Shop").addDish("California Roll",
                                "Fresh sushi roll with crab, avocado, and cucumber", 8.99);
                restaurantManager.getRestaurant("Pizza Palace").addDish("Margherita Pizza",
                                "Classic pizza with tomato sauce, mozzarella, and basil", 12.50);

                int port = 8081;
                SimpleServer server = new SimpleServer(port);

                ApiRegistry registry = new ApiRegistry();

                registry.register("GET", "/restaurants", new StaticRestaurantHandler(restaurantManager, objectMapper));
                registry.register("POST", "/restaurants", new StaticRestaurantHandler(restaurantManager, objectMapper));
                registry.register("GET", "/restaurants/dishes/dietarylabels",
                                new StaticRestaurantHandler(restaurantManager, objectMapper));
                registry.register("GET", "/restaurants/dishes/cuisinetypes",
                                new StaticRestaurantHandler(restaurantManager, objectMapper));

                HybridSuggestionService suggestionService = new HybridSuggestionService();
                registry.register("GET", "/suggestions", new SuggestionHandler(suggestionService, objectMapper));

                registry.registerFallback(
                                new DynamicRestaurantHandler(restaurantManager, objectMapper, suggestionService));
                server.start(registry);
                System.out.println("RestaurantService started on port " + port);
                System.out.println("Serving STATIC routes:  GET /restaurants");
                System.out.println("Serving STATIC routes:  GET /restaurants/dishes/dietarylabels");
                System.out.println("Serving STATIC routes:  GET /restaurants/dishes/cuisinetypes");
                System.out.println("Serving STATIC routes:  GET /suggestions");
                System.out.println("Now serving exact route: POST /restaurants");
                System.out.println("Serving DYNAMIC routes: GET /restaurants/{restaurantId}/dishes");

        }

        private static void populateMockData() {
                Restaurant italian = new Restaurant("Luigi's Trattoria");
                italian.setCuisineType(CuisineType.ITALIAN);

                addDishToRestaurant(italian, "Carbonara Royale", "Traditional pasta with guanciale and egg yolk", 14.50,
                                DishType.PASTA, DishCategory.MAIN_COURSE, List.of()); // Pas de label spécifique

                addDishToRestaurant(italian, "Pizza Margherita", "Tomato sauce, mozzarella di bufala, basil", 12.00,
                                DishType.PIZZA, DishCategory.MAIN_COURSE, List.of(DietaryLabel.VEGETARIAN));

                addDishToRestaurant(italian, "Tiramisu", "Coffee-flavoured Italian dessert", 7.00,
                                DishType.CAKE, DishCategory.DESSERT, List.of(DietaryLabel.VEGETARIAN));

                restaurantManager.addRestaurant(italian);

                Restaurant japanese = new Restaurant("Tokyo Zen");
                japanese.setCuisineType(CuisineType.JAPANESE);

                addDishToRestaurant(japanese, "Salmon Sashimi", "Fresh raw salmon slices", 16.00,
                                DishType.SUSHI, DishCategory.MAIN_COURSE,
                                List.of(DietaryLabel.GLUTEN_FREE, DietaryLabel.HALAL));

                addDishToRestaurant(japanese, "Veggie Ramen", "Noodle soup with tofu and vegetables", 13.50,
                                DishType.GENERAL, DishCategory.MAIN_COURSE,
                                List.of(DietaryLabel.VEGAN, DietaryLabel.VEGETARIAN));

                addDishToRestaurant(japanese, "Mochi Ice Cream", "Rice cake with ice cream filling", 5.50,
                                DishType.ICE_CREAM, DishCategory.DESSERT,
                                List.of(DietaryLabel.VEGETARIAN, DietaryLabel.GLUTEN_FREE));

                restaurantManager.addRestaurant(japanese);

                Restaurant american = new Restaurant("Uncle Sam's BBQ");
                american.setCuisineType(CuisineType.AMERICAN);

                addDishToRestaurant(american, "Double Bacon Burger", "Two beef patties, bacon, cheddar", 15.00,
                                DishType.BURGER, DishCategory.MAIN_COURSE, List.of()); // Contient porc et viande

                addDishToRestaurant(american, "Caesar Salad", "Romaine lettuce, croutons, parmesan", 9.00,
                                DishType.SALAD, DishCategory.STARTER, List.of(DietaryLabel.VEGETARIAN));

                addDishToRestaurant(american, "Coca Cola", "Chilled soda 33cl", 3.00,
                                DishType.DRINK, DishCategory.DRINK,
                                List.of(DietaryLabel.VEGAN, DietaryLabel.VEGETARIAN, DietaryLabel.GLUTEN_FREE));

                restaurantManager.addRestaurant(american);

                Restaurant french = new Restaurant("Le Petit Gourmet");
                french.setCuisineType(CuisineType.FRENCH);

                addDishToRestaurant(french, "Boeuf Bourguignon", "Beef stew braised in red wine", 18.00,
                                DishType.MEAT, DishCategory.MAIN_COURSE, List.of());

                addDishToRestaurant(french, "Ratatouille", "Stewed vegetables (Tomato, Eggplant, Zucchini)", 14.00,
                                DishType.GENERAL, DishCategory.MAIN_COURSE,
                                List.of(DietaryLabel.VEGAN, DietaryLabel.VEGETARIAN, DietaryLabel.GLUTEN_FREE));

                restaurantManager.addRestaurant(french);

                Restaurant indian = new Restaurant("Bollywood Spice");
                indian.setCuisineType(CuisineType.INDIAN);

                addDishToRestaurant(indian, "Chicken Tikka Masala", "Roasted marinated chicken in spiced curry", 16.50,
                                DishType.MEAT, DishCategory.MAIN_COURSE,
                                List.of(DietaryLabel.HALAL, DietaryLabel.GLUTEN_FREE));

                addDishToRestaurant(indian, "Palak Paneer", "Cottage cheese in spinach gravy", 13.00,
                                DishType.GENERAL, DishCategory.MAIN_COURSE,
                                List.of(DietaryLabel.VEGETARIAN, DietaryLabel.GLUTEN_FREE));

                restaurantManager.addRestaurant(indian);
        }

        private static void addDishToRestaurant(Restaurant restaurant, String name, String desc, double price,
                        DishType type, DishCategory category, List<DietaryLabel> labels) {
                restaurant.addDish(name, desc, price);

                Dish dish = restaurant.findDishByName(name);
                if (dish != null) {
                        dish.setDishType(type);
                        dish.setCategory(category);
                        dish.setDietaryLabels(labels);

                        for (DietaryLabel label : labels) {
                                restaurant.addDietaryLabel(label);
                        }
                }
        }

}
