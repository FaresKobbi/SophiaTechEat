package fr.unice.polytech.restaurants;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// RestaurantManager: Manages restaurants and their time slots.

public class RestaurantManager {

    // Storage for all restaurants (simple in-memory storage)
    private Map<String, Restaurant> restaurants;

    // Storage for blocked time slots per restaurant
    private Map<String, List<TimeSlot>> blockedTimeSlots;



    public RestaurantManager() {
        this.restaurants = new HashMap<>();
        this.blockedTimeSlots = new HashMap<>();
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
        if (slot == null) {
            throw new IllegalArgumentException("TimeSlot cannot be null");
        }
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }

        String restaurantName = restaurant.getRestaurantName();

        // Initialize blocked slots list for this restaurant if it doesn't exist
        if (!blockedTimeSlots.containsKey(restaurantName)) {
            blockedTimeSlots.put(restaurantName, new ArrayList<>());
        }

        // Add the slot to blocked slots if not already blocked
        List<TimeSlot> blocked = blockedTimeSlots.get(restaurantName);
        if (!blocked.contains(slot)) {
            blocked.add(slot);
        }
    }

    //Gets all available (non-blocked) time slots for a specific restaurant.
    public List<TimeSlot> getAvailableTimeSlots(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }

        String restaurantName = restaurant.getRestaurantName();
        List<TimeSlot> allSlots = restaurant.getAvailableTimeSlots();
        List<TimeSlot> blocked = blockedTimeSlots.get(restaurantName);

        // If no slots are blocked, return all slots
        if (blocked == null || blocked.isEmpty()) {
            return new ArrayList<>(allSlots);
        }

        // Filter out blocked slots
        List<TimeSlot> availableSlots = new ArrayList<>();
        for (TimeSlot slot : allSlots) {
            if (!isTimeSlotBlocked(slot, blocked)) {
                availableSlots.add(slot);
            }
        }

        return availableSlots;
    }


    //Checks if a time slot is blocked.
    private boolean isTimeSlotBlocked(TimeSlot slot, List<TimeSlot> blockedList) {
        for (TimeSlot blocked : blockedList) {
            // Compare start and end times
            if (slot.getStartTime().equals(blocked.getStartTime()) &&
                    slot.getEndTime().equals(blocked.getEndTime())) {
                return true;
            }
        }
        return false;
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




    //Gets all blocked time slots for a restaurant
    public List<TimeSlot> getBlockedTimeSlots(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }

        String restaurantName = restaurant.getRestaurantName();
        List<TimeSlot> blocked = blockedTimeSlots.get(restaurantName);

        return blocked == null ? new ArrayList<>() : new ArrayList<>(blocked);
    }

    //Unblock a time slot for a restaurant
    public void unblockTimeSlot(TimeSlot slot, Restaurant restaurant) {
        if (slot == null || restaurant == null) {
            throw new IllegalArgumentException("TimeSlot and Restaurant cannot be null");
        }

        String restaurantName = restaurant.getRestaurantName();
        List<TimeSlot> blocked = blockedTimeSlots.get(restaurantName);

        if (blocked != null) {
            blocked.removeIf(blockedSlot ->
                    blockedSlot.getStartTime().equals(slot.getStartTime()) &&
                            blockedSlot.getEndTime().equals(slot.getEndTime())
            );
        }
    }


     //Checks if a restaurant exists.
    public boolean hasRestaurant(String restaurantName) {
        return restaurants.containsKey(restaurantName);

    }
}