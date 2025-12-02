package fr.unice.polytech.restaurants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.unice.polytech.dishes.*;


import fr.unice.polytech.dishes.DishType;
import fr.unice.polytech.users.UserAccount;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public class Restaurant extends UserAccount {
    private String restaurantId;
    private String restaurantName;
    private List<Dish> dishes;
    private List<String> orders;
    private List<OpeningHours> openingHours;
    private CuisineType cuisineType;
    private Set<DietaryLabel> availableDietaryLabels = new HashSet<>();
    @JsonIgnore
    private final DishManager dishManager = new DishManager();

    public Restaurant(String restaurantName) {
        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty");
        }
        this.restaurantId = UUID.randomUUID().toString();
        this.restaurantName = restaurantName;
        this.dishes = new ArrayList<>();
        orders = new ArrayList<>();
        this.openingHours = new ArrayList<>();
        this.cuisineType = CuisineType.GENERAL;
    }

    
    
    
    private Restaurant(Builder builder) {
        this.restaurantId = UUID.randomUUID().toString();
        this.restaurantName = builder.restaurantName;
        this.dishes = new ArrayList<>(builder.dishes);
        orders = new ArrayList<>();
        this.openingHours = new ArrayList<>(builder.openingHours);
        this.cuisineType = builder.cuisineType != null ? builder.cuisineType : CuisineType.GENERAL;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    
    public List<Dish> getDishes() {
        return new ArrayList<>(dishes);
    }

    public void setAvailableDietaryLabels(Set<DietaryLabel> availableDietaryLabels) {
        this.availableDietaryLabels = availableDietaryLabels;
    }

    public Set<DietaryLabel> getAvailableDietaryLabels() {
        return availableDietaryLabels;
    }

    public List<TimeSlot> getAvailableTimeSlots() {
        List<TimeSlot> availableSlots = new ArrayList<>();
        for (OpeningHours oh : openingHours) {
            oh.getSlots().forEach((slot, capacity) -> {
                if (capacity > 0) {
                    availableSlots.add(slot);
                }
            });
        }
        return availableSlots;
    }

    public void setRestaurantName(String restaurantName) {
        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty");
        }
        this.restaurantName = restaurantName;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public void setCapacity(TimeSlot slot, int capacity) {
        if (slot == null)
            throw new IllegalArgumentException("TimeSlot cannot be null");
        if (capacity < 0)
            throw new IllegalArgumentException("Capacity cannot be negative");

        boolean found = false;
        for (OpeningHours oh : openingHours) {
            if (oh.contains(slot)) {
                oh.setSlotCapacity(slot.getStartTime(), slot.getEndTime(), capacity);
                found = true;
            }
        }

        if (!found) {
            
            Optional<OpeningHours> existingOh = openingHours.stream()
                    .filter(oh -> oh.getDay() == slot.getDayOfWeek()
                            || (slot.getDayOfWeek() == null && oh.getDay() == DayOfWeek.MONDAY))
                    .findFirst();

            if (existingOh.isPresent()) {
                
                
                TimeSlot slotToAdd = slot;
                if (slot.getDayOfWeek() == null) {
                    slotToAdd = new TimeSlot(existingOh.get().getDay(), slot.getStartTime(), slot.getEndTime());
                }
                existingOh.get().addSlot(slotToAdd, capacity);
            } else {
                
                DayOfWeek day = slot.getDayOfWeek() != null ? slot.getDayOfWeek() : DayOfWeek.MONDAY;
                OpeningHours oh = new OpeningHours(day, slot.getStartTime(), slot.getEndTime());
                oh.setSlotCapacity(slot.getStartTime(), slot.getEndTime(), capacity);
                this.addOpeningHours(oh);
            }
        }
    }

    public void setOpeningHours(List<OpeningHours> openingHours) {
        if (openingHours == null) {
            throw new IllegalArgumentException("Opening hours list cannot be null.");
        }
        this.openingHours = new ArrayList<>(openingHours);
    }

    public void addOpeningHours(OpeningHours openingHours) {
        if (openingHours == null) {
            throw new IllegalArgumentException("OpeningHours cannot be null");
        }
        this.openingHours.add(openingHours);
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
            throw new IllegalArgumentException(
                    "Cannot update opening hours: No entry found for " + updatedOpeningHours.getDay());
        }

        this.openingHours.removeIf(oh -> oh.getDay() == updatedOpeningHours.getDay());
        this.openingHours.add(updatedOpeningHours);
    }

    public void updateSlotCapacity(DayOfWeek day, LocalTime start, LocalTime end, int capacity) {
        TimeSlot target = new TimeSlot(day, start, end);
        boolean found = false;

        for (OpeningHours oh : openingHours) {
            if (oh.contains(target)) {
                oh.setSlotCapacity(start, end, capacity);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException(
                    "Slot " + day + " " + start + "-" + end + " does not exist in any opening hours");
        }
    }

    public Map<TimeSlot, Integer> getAllCapacities() {
        Map<TimeSlot, Integer> globalRequest = new HashMap<>();
        for (OpeningHours oh : openingHours) {
            globalRequest.putAll(oh.getSlots());
        }
        return globalRequest;
    }

    public void addDietaryLabel(DietaryLabel label) {
        if (label == null) {
            throw new IllegalArgumentException("Dietary label cannot be null");
        }
        availableDietaryLabels.add(label);
    }

    
    
    
    
    
    
    

    public int getCapacity(TimeSlot slot) {
        for (OpeningHours oh : openingHours) {
            if (oh.contains(slot)) {
                return oh.getSlots().get(slot);
            }
        }
        return 0;
    }

    public void blockTimeSlot(TimeSlot slot) {
        if (slot == null)
            throw new IllegalArgumentException("TimeSlot cannot be null");
        decreaseCapacity(slot);
    }

    public void unblockTimeSlot(TimeSlot slot) {
        if (slot == null)
            throw new IllegalArgumentException("TimeSlot cannot be null");
        increaseCapacity(slot);
    }

    public void decreaseCapacity(TimeSlot slot) {
        if (slot == null)
            throw new IllegalArgumentException("TimeSlot cannot be null");

        for (OpeningHours oh : openingHours) {
            if (oh.contains(slot)) {
                int currentCapacity = oh.getSlots().get(slot);

                if (currentCapacity > 0) {
                    oh.setSlotCapacity(slot.getStartTime(), slot.getEndTime(), currentCapacity - 1);
                } else {
                    System.out.println("No capacity left for slot " + slot);
                }
                return;
            }
        }
    }

    public void increaseCapacity(TimeSlot slot) {
        if (slot == null)
            throw new IllegalArgumentException("TimeSlot cannot be null");

        for (OpeningHours oh : openingHours) {
            if (oh.contains(slot)) {
                int currentCapacity = oh.getSlots().get(slot);
                oh.setSlotCapacity(slot.getStartTime(), slot.getEndTime(), currentCapacity + 1);
                return;
            }
        }
    }

    
    public void addDish(String name, String description, double price) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Dish name cannot be null or empty");
        }
        if (description == null) {
            throw new IllegalArgumentException("Dish description cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Dish price cannot be negative");
        }
        Dish dish = dishManager.createDish(name, description, price);
        dishes.add(dish);
    }

    public void updateDish(Dish oldDish, String description) {
        if (!dishes.contains(oldDish)) {
            throw new IllegalArgumentException("Dish not found in the menu");
        }
        if (description == null) {
            throw new IllegalArgumentException("Dish description cannot be null");
        }

        dishManager.updateDescription(oldDish, description);
    }

    public void updateDish(Dish oldDish, int price) {
        if (!dishes.contains(oldDish)) {
            throw new IllegalArgumentException("Dish not found in the menu");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Dish description cannot be null");
        }

        dishManager.updatePrice(oldDish, price);
    }

    public void updateDish(Dish oldDish, DishCategory dishCategory) {
        if (!dishes.contains(oldDish)) {
            throw new IllegalArgumentException("Dish not found in the menu");
        }
        if (dishCategory == null) {
            throw new IllegalArgumentException("Dish category cannot be null");
        }

        dishManager.updateDishCategory(oldDish, dishCategory);
    }

    public void updateDish(Dish oldDish, DishType dishType) {
        if (!dishes.contains(oldDish)) {
            throw new IllegalArgumentException("Dish not found in the menu");
        }
        if (dishType == null) {
            throw new IllegalArgumentException("Dish category cannot be null");
        }

        dishManager.updateDishType(oldDish, dishType);
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public void setOrders(List<String> orders) {
        this.orders = orders;
    }

    
    
    
    

    public void setCuisineType(CuisineType cuisineType) {
        this.cuisineType = cuisineType;
    }

    public List<String> getOrders() {
        return orders;
    }

    public CuisineType getCuisineType() {
        return cuisineType;
    }

    public DishManager getDishManager() {
        return dishManager;
    }

    public void addOrder(String orderId) {
        orders.add(orderId);
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantName='" + restaurantName + '\'' +
                ", dishCount=" + dishes.size() +
                
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Restaurant that = (Restaurant) o;
        return restaurantName.equals(that.restaurantName);
    }

    @Override
    public int hashCode() {
        return restaurantName.hashCode();
    }

    public List<OpeningHours> getOpeningHours() {
        return openingHours;
    }

    
    public void removeDish(String dishName) {
        if (dishName == null || dishName.isEmpty()) {
            throw new IllegalArgumentException("Dish name cannot be null or empty");
        }
        dishes.removeIf(dish -> dish.getName().equals(dishName));
    }

    
    public Dish findDishByName(String dishName) {
        if (dishName == null || dishName.isEmpty()) {
            throw new IllegalArgumentException("Dish name cannot be null or empty");
        }
        return dishes.stream()
                .filter(dish -> dish.getName().equals(dishName))
                .findFirst()
                .orElse(null);
    }

    public List<String> getOrderIds() {
        return orders;
    }

    

    
    public static class Builder {
        private final String restaurantName;
        public CuisineType cuisineType;
        private List<Dish> dishes = new ArrayList<>();
        private List<TimeSlot> availableTimeSlots = new ArrayList<>();
        private List<OpeningHours> openingHours = new ArrayList<>();

        public Builder(String restaurantName) {
            if (restaurantName == null || restaurantName.isEmpty()) {
                throw new IllegalArgumentException("Restaurant name is required");
            }
            this.restaurantName = restaurantName;
        }

        public Builder withCuisineType(CuisineType cuisineType) {
            this.cuisineType = cuisineType;
            return this;
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

        public Builder withTimeSlots(List<TimeSlot> timeSlots) {
            if (timeSlots != null) {
                this.availableTimeSlots.addAll(timeSlots);
            }
            return this;
        }

        public Builder withOpeningHours(List<OpeningHours> hours) {
            if (hours != null) {
                this.openingHours.addAll(hours);
            }
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }

}
