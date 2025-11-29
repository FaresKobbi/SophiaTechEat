package fr.unice.polytech.restaurants;

import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OpeningHoursTest {

    @Test
    void shouldGenerateSlotsWithZeroCapacityOnInit() {
        OpeningHours hours = new OpeningHours(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(13, 0));

        Map<TimeSlot, Integer> slots = hours.getSlots();

        assertEquals(2, slots.size(), "Should generate 2 slots of 30 mins");

        TimeSlot slot1 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(12, 30));
        assertTrue(slots.containsKey(slot1));
        assertEquals(0, slots.get(slot1), "Capacity should be initialized to 0");
    }

    @Test
    void shouldUpdateSlotCapacity() {
        OpeningHours hours = new OpeningHours(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(13, 0));

        hours.setSlotCapacity(LocalTime.of(12, 0), LocalTime.of(12, 30), 50);

        TimeSlot target = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(12, 30));
        assertEquals(50, hours.getSlots().get(target));
    }

    @Test
    void shouldThrowExceptionForInvalidSlot() {
        OpeningHours hours = new OpeningHours(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(13, 0));

        assertThrows(IllegalArgumentException.class, () -> {
            hours.setSlotCapacity(LocalTime.of(18, 0), LocalTime.of(18, 30), 10);
        });
    }
}