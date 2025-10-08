package fr.unice.polytech; // Assuming this package

import java.time.LocalTime; // Best for time-only data

public class TimeSlot {
    
    // Attributes from Class Diagram: (Implied to be start/end times)
    private LocalTime startTime;
    private LocalTime endTime;



    /**
     * Constructor for a TimeSlot.
     * @param startTime The start time of the slot.
     * @param endTime The end time of the slot.
     */
    public TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    // A utility method for display/debugging
    @Override
    public String toString() {
        return startTime + " - " + endTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return startTime.equals(timeSlot.startTime) && endTime.equals(timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return startTime.hashCode() + endTime.hashCode();
    }
}