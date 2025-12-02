package fr.unice.polytech.restaurants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstablishmentTypeTest {

    @Test
    void testEnumValues() {
        assertEquals(3, EstablishmentType.values().length);
        assertEquals(EstablishmentType.CROUS, EstablishmentType.valueOf("CROUS"));
        assertEquals(EstablishmentType.RESTAURANT, EstablishmentType.valueOf("RESTAURANT"));
        assertEquals(EstablishmentType.FOOD_TRUCK, EstablishmentType.valueOf("FOOD_TRUCK"));
    }
}
