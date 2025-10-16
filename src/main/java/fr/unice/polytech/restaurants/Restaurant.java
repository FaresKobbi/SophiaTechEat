package fr.unice.polytech.restaurants;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.orderManagement.Order;

import fr.unice.polytech.dishes.DishType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Restaurant {
    private String restaurantName;
    private List<Dish> dishes;
    private List<TimeSlot> availableTimeSlots;
    private List<Order> orders;
   //Simple initialisation 
    private List<OpeningHours> openingHours;
    private Map<TimeSlot, Integer> capacityByTimeSlot;
    private EstablishmentType establishmentType;
    private DishType cuisineType;


    //Simple initialisation
    public Restaurant(String restaurantName) {
        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty");
        }
        this.restaurantName = restaurantName;
        this.dishes = new ArrayList<>();
        this.availableTimeSlots = new ArrayList<>();
        orders = new ArrayList<>();
        this.capacityByTimeSlot = new HashMap<>();
        this.openingHours = new ArrayList<>();
    }
    
    
    //Private constructor for Builder pattern.
    //Avoid public Restaurant(String restaurantName, List<Dish> dishes, List<TimeSlot> availableTimeSlots) {..}
    private Restaurant(Builder builder) {
        this.restaurantName = builder.restaurantName;
        this.dishes = new ArrayList<>(builder.dishes);
        this.availableTimeSlots = new ArrayList<>(builder.availableTimeSlots);
        orders = new ArrayList<>();
        this.capacityByTimeSlot = new HashMap<>();
        this.cuisineType = builder.cuisineType;
        this.openingHours = new ArrayList<>(builder.openingHours);
    }
    
    // ========== BUILDER PATTERN ==========
    
    /**
     * Builder class for constructing Restaurant objects with many optional parameters.
     * We use this class when we  need to create a Restaurant with initial dishes and time slots.
     */
    public static class Builder {
        private final String restaurantName;
        private List<Dish> dishes = new ArrayList<>();
        private List<TimeSlot> availableTimeSlots = new ArrayList<>();
        private DishType cuisineType;
        private List<OpeningHours> openingHours = new ArrayList<>();
        public Builder withCuisineType(DishType cuisineType) {
            this.cuisineType = cuisineType;
            return this;

        }
        public Builder(String restaurantName) {
            if (restaurantName == null || restaurantName.isEmpty()) {
                throw new IllegalArgumentException("Restaurant name is required");
            }
            this.restaurantName = restaurantName;
        }
        
        public Builder withDish(Dish dish) {
            if (dish != null) {
                this.dishes.add(dish);
            }
            return this;
        }
        
        public Builder withDishes(List<Dish> dishes) {
            if (dishes != null) {
                this.dishes.addAll(dishes);
            }
            return this;
        }
        
        public Builder withTimeSlot(TimeSlot timeSlot) {
            if (timeSlot != null) {
                this.availableTimeSlots.add(timeSlot);
            }
            return this;
        }

        public Builder withOpeningHours(List<OpeningHours> hours) {
            if (hours != null) {
                this.openingHours.addAll(hours);
            }
            return this;
        }
        
        public Builder withTimeSlots(List<TimeSlot> timeSlots) {
            if (timeSlots != null) {
                this.availableTimeSlots.addAll(timeSlots);
            }
            return this;
        }
        
        public Restaurant build() {
            return new Restaurant(this);
        }
    }
    

    
    public String getRestaurantName() {
        return restaurantName;
    }
    
    
    //return a copy of the dishes/TimeSlot list to prevent external modification.
    public List<Dish> getDishes() {
        return new ArrayList<>(dishes);
    }
    
   
    public List<TimeSlot> getAvailableTimeSlots() {
        return new ArrayList<>(availableTimeSlots);
    }
    
   
    
    public void setRestaurantName(String restaurantName) {
        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty");
        }
        this.restaurantName = restaurantName;
    }


    //======= Capacity by slot =====
    public void setCapacity(TimeSlot slot, int capacity) {
        if (slot == null) throw new IllegalArgumentException("TimeSlot cannot be null");
        if (capacity < 0) throw new IllegalArgumentException("Capacity cannot be negative");
        capacityByTimeSlot.put(slot, capacity);
        if (!availableTimeSlots.contains(slot)) availableTimeSlots.add(slot);
    }

    public void setOpeningHours(List<OpeningHours> openingHours) {
        if (openingHours == null) {
            throw new IllegalArgumentException("Opening hours list cannot be null.");
        }
        this.openingHours = new ArrayList<>(openingHours);
    }

    public void addOpeningHours(OpeningHours newOpeningHours) {
        if (newOpeningHours == null) {
            throw new IllegalArgumentException("Opening hours cannot be null.");
        }
        for (OpeningHours existingHours : this.openingHours) {
            if (existingHours.getDay() == newOpeningHours.getDay()) {
                throw new IllegalArgumentException(
                        "Opening hours for " + newOpeningHours.getDay() + " already exist."
                );
            }
        }
        this.openingHours.add(newOpeningHours);
    }

    public void updateOpeningHours(OpeningHours updatedOpeningHours) {
        if (updatedOpeningHours == null) {
            throw new IllegalArgumentException("Opening hours cannot be null.");
        }
        boolean dayFound = false;
        for (OpeningHours existingHours : this.openingHours) {
            if (existingHours.getDay() == updatedOpeningHours.getDay()) {
                dayFound = true;
                break;
            }
        }

        if (!dayFound) {
            throw new IllegalArgumentException("Cannot update opening hours: No entry found for " + updatedOpeningHours.getDay());
        }

        this.openingHours.removeIf(oh -> oh.getDay() == updatedOpeningHours.getDay());
        this.openingHours.add(updatedOpeningHours);
    }



    public int getCapacity(TimeSlot slot) {
        return capacityByTimeSlot.getOrDefault(slot, 0);
    }

    public Map<TimeSlot, Integer> getAllCapacities() {
        return new HashMap<>(capacityByTimeSlot);
    }

    public void decreaseCapacity(TimeSlot slot) {
        if (slot == null) throw new IllegalArgumentException("TimeSlot cannot be null");
        if (!availableTimeSlots.contains(slot)) return;
        int capacity= capacityByTimeSlot.get(slot);
        if (capacity > 0) { // Prevent negative capacity
            capacityByTimeSlot.put(slot, capacity - 1);
        } else {
            System.out.println(" No capacity left for slot " + slot);
        }
    }

    public void increaseCapacity(TimeSlot slot) {
        if (slot == null) throw new IllegalArgumentException("TimeSlot cannot be null");
        capacityByTimeSlot.put(slot, capacityByTimeSlot.getOrDefault(slot, 0) + 1);
    }


    // ========== DISH MANAGEMENT METHODS ==========
    /**
     * Adds a dish to the restaurant's menu.
     * Use this AFTER construction instead of passing dishes to constructor.
     * @param dish The dish to add
     * @throws IllegalArgumentException if the dish is null or already exists
     */
    public void addDish(Dish dish) {
        if (dish == null) {
            throw new IllegalArgumentException("Dish cannot be null");
        }
        if (dishes.contains(dish)) {
            throw new IllegalArgumentException("Dish already exists in the menu");
        }
        dishes.add(dish);
    }

    /**
     * Adds multiple dishes at once.
     * @param dishList List of dishes to add
     */
    public void addDishes(List<Dish> dishList) {
        if (dishList == null) {
            throw new IllegalArgumentException("Dish list cannot be null");
        }
        for (Dish dish : dishList) {
            addDish(dish);
        }
    }



    /**
     * Updates an existing dish in the restaurant's menu.
     * @param oldDish The dish to replace
     * @param newDish The new dish
     * @throws IllegalArgumentException if oldDish is not found or newDish is null
     */
    public void updateDish(Dish oldDish, Dish newDish) {
        if (oldDish == null || newDish == null) {
            throw new IllegalArgumentException("Old dish and new dish cannot be null");
        }
        int index = dishes.indexOf(oldDish);
        if (index == -1) {
            throw new IllegalArgumentException("The dish to update does not exist in the menu");
        }
        dishes.set(index, newDish);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }
    
    



    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantName='" + restaurantName + '\'' +
                ", dishCount=" + dishes.size() +
                // ", availableTimeSlotsCount=" + getAvailableTimeSlotCount() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return restaurantName.equals(that.restaurantName);
    }
    
    @Override
    public int hashCode() {
        return restaurantName.hashCode();
    }


    public DishType getCuisineType() {
        return cuisineType;
    }

    public List<OpeningHours> getOpeningHours() {
        return openingHours;
    }
}

