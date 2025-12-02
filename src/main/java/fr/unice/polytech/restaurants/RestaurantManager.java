package  fr.unice.polytech.restaurants;


import fr.unice.polytech.dishes.DietaryLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class RestaurantManager {

    
    private Map<String, Restaurant> restaurants;



    public RestaurantManager() {
        this.restaurants = new HashMap<>();

    }


    
    public Restaurant getRestaurant(String restaurantName) {
        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty");
        }
        return restaurants.get(restaurantName);
    }


    
    public void blockTimeSlot(TimeSlot slot, Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
       restaurant.blockTimeSlot(slot);

    }

    
    public List<TimeSlot> getAvailableTimeSlots(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        
        return restaurant.getAvailableTimeSlots();
    }



    


    
    public void addRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        restaurants.put(restaurant.getRestaurantName(), restaurant);
    }


    
    public List<Restaurant> getAllRestaurants() {
        return new ArrayList<>(restaurants.values());
    }




    
    public void unblockTimeSlot(TimeSlot slot, Restaurant restaurant) {
        if ( restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        restaurant.unblockTimeSlot(slot);
    }


     
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


    public List<Restaurant> search(CuisineType cuisine, List<DietaryLabel> labels) {
        
        List<Restaurant> result = new ArrayList<>(restaurants.values());

        
        if (cuisine != null) {
            result.removeIf(r -> r.getCuisineType() != cuisine);
        }

        
        
        if (labels != null && !labels.isEmpty()) {
            for (DietaryLabel label : labels) {
                result.removeIf(r -> !r.getAvailableDietaryLabels().contains(label));
            }
        }

        return result;
    }
}


