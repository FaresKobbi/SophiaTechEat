package fr.unice.polytech.restaurants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;

class TimeSlotTest {

    private final LocalTime START = LocalTime.of(11, 30);
    private final LocalTime END = LocalTime.of(12, 0);

    
    @Test
    void testTimeSlotCreationAndGetters() {
        TimeSlot slot = new TimeSlot(START, END);
        
        assertEquals(START, slot.getStartTime(), "Start time should be set correctly.");
        assertEquals(END, slot.getEndTime(), "End time should be set correctly.");
    }
    
    
    @Test
    void testToString() {
        TimeSlot slot = new TimeSlot(START, END);
        String expected = "11:30 - 12:00";
        
        assertEquals(expected, slot.toString(), "toString should format the time slot correctly.");
    }
    
    
    @Test
    void testTimeSlotWithSingleDigitHour() {
        LocalTime earlyStart = LocalTime.of(9, 0);
        LocalTime earlyEnd = LocalTime.of(9, 30);
        TimeSlot slot = new TimeSlot(earlyStart, earlyEnd);

        
        assertEquals("09:00 - 09:30", slot.toString(), "toString should correctly format single-digit hours (if LocalTime standard does).");
    }
}