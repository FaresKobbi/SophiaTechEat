package  fr.unice.polytech.restaurants;


import fr.unice.polytech.dishes.DietaryLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// RestaurantManager: Manages restaurants and their time slots.

public class RestaurantManager {

    // Storage for all restaurants (simple in-memory storage)
    private Map<String, Restaurant> restaurants;



    public RestaurantManager() {
        this.restaurants = new HashMap<>();

    }


    //Gets a restaurant by its name.
    public Restaurant getRestaurant(String restaurantName) {
        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty");
        }
        return restaurants.get(restaurantName);
    }


    //Blocks a time slot for a specific restaurant so it prevents the time slot from being available for orders.
    public void blockTimeSlot(TimeSlot slot, Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
       restaurant.blockTimeSlot(slot);

    }

    //Gets all available (non-blocked) time slots for a specific restaurant.
    public List<TimeSlot> getAvailableTimeSlots(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        
        return restaurant.getAvailableTimeSlots();
    }



    // ========== UTILITY METHODS for managing the restaurant collection ==========


    //Adds a restaurant to the manager
    public void addRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        restaurants.put(restaurant.getRestaurantName(), restaurant);
    }


    //Gets all the restaurants managed by this RestaurantManager
    public List<Restaurant> getAllRestaurants() {
        return new ArrayList<>(restaurants.values());
    }




    //Unblock a time slot for a restaurant
    public void unblockTimeSlot(TimeSlot slot, Restaurant restaurant) {
        if ( restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        restaurant.unblockTimeSlot(slot);
    }


     //Checks if a restaurant exists.
    public boolean hasRestaurant(String restaurantName) {
        return restaurants.containsKey(restaurantName);

    }

    public List<Restaurant> searchByCuisine(CuisineType cuisine) {
        if (cuisine == null) {
            return getAllRestaurants();
        }
        return restaurants.values().stream()
                .filter(r -> r.getCuisineType() == cuisine)
                .collect(Collectors.toList());
    }


    public List<Restaurant> searchByDietaryLabel(DietaryLabel label) {
        if (label == null) {
            return getAllRestaurants();
        }
        return restaurants.values().stream()
                .filter(r -> r.getDishes().stream()
                        .anyMatch(dish -> dish.getDietaryLabels().contains(label)))
                .collect(Collectors.toList());
    }


    public List<Restaurant> search(CuisineType cuisine, DietaryLabel label) {

        if (cuisine == null && label == null) {
            return getAllRestaurants();
        }

        if (label == null) {
            return searchByCuisine(cuisine);
        }

        if (cuisine == null) {
            return searchByDietaryLabel(label);
        }

        return restaurants.values().stream()
                .filter(r -> r.getCuisineType() == cuisine)
                .filter(r -> r.getDishes().stream()
                        .anyMatch(dish -> dish.getDietaryLabels().contains(label)))
                .collect(Collectors.toList());
    }


}