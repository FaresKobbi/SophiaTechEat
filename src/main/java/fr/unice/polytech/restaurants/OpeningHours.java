package fr.unice.polytech.restaurants;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OpeningHours {
    private String id;
    private DayOfWeek day;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Map<TimeSlot, Integer> slots;

    public OpeningHours(DayOfWeek day, LocalTime openingTime, LocalTime closingTime) {
        if (openingTime.isAfter(closingTime)) {
            throw new IllegalArgumentException("Opening time cannot be after closing time");
        }
        this.id = UUID.randomUUID().toString();
        this.day = day;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slots = new HashMap<>();

        generateSlots();
    }

    public String getId() {
        return id;
    }

    private void generateSlots() {
        LocalTime current = openingTime;
        while (current.isBefore(closingTime)) {
            LocalTime next = current.plusMinutes(30);
            if (next.isAfter(closingTime))
                break;

            TimeSlot slot = new TimeSlot(day, current, next);

            slots.put(slot, 0);

            current = next;
        }
    }

    public void setSlotCapacity(LocalTime start, LocalTime end, int newCapacity) {
        TimeSlot target = new TimeSlot(day, start, end);

        if (slots.containsKey(target)) {
            if (newCapacity < 0)
                throw new IllegalArgumentException("Capacity cannot be negative");
            slots.put(target, newCapacity);
        } else {
            throw new IllegalArgumentException(
                    "Time slot " + start + "-" + end + " not found in this opening schedule");
        }
    }

    public void addSlot(TimeSlot slot, int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Capacity cannot be negative");
        slots.put(slot, capacity);
    }

    public boolean contains(TimeSlot slot) {
        return slots.containsKey(slot);
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public Map<TimeSlot, Integer> getSlots() {
        return new HashMap<>(slots);
    }

    @Override
    public String toString() {
        return day + ": " + openingTime + " - " + closingTime;
    }

}
