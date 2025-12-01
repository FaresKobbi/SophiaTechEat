package fr.unice.polytech.users;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryLocationTest {

    private final String NAME = "Office";
    private final String ADDRESS = "123 Main St";
    private final String CITY = "Nice";
    private final String ZIP = "06000";

    // Test 1: Constructor and getters
    @Test
    void testDeliveryLocationCreationAndGetters() {
        DeliveryLocation location = new DeliveryLocation(NAME, ADDRESS, CITY, ZIP);

        assertEquals(NAME, location.getName(), "Name should be set correctly.");
        assertEquals(ADDRESS, location.getAddress(), "Address should be set correctly.");
        assertEquals(CITY, location.getCity(), "City should be set correctly.");
        assertEquals(ZIP, location.getZipCode(), "Zip code should be set correctly.");
    }

    // Test 3: toString representation

    @Test
    void testEqualsAndHashCode() {
        DeliveryLocation loc1 = new DeliveryLocation("Home", "Address", "City", "Zip");
        DeliveryLocation loc2 = new DeliveryLocation("Work", "Address", "City", "Zip");
        loc2.setId(loc1.getId()); // Same ID -> Equal

        assertEquals(loc1, loc2);
        assertEquals(loc1.hashCode(), loc2.hashCode());

        DeliveryLocation loc3 = new DeliveryLocation();
        assertNotEquals(loc1, loc3);
    }

    @Test
    void testToString() {
        DeliveryLocation location = new DeliveryLocation("Office", "123 Main St", "Nice", "06000");
        // ID is random, so we check if it contains other fields
        String str = location.toString();
        assertTrue(str.contains("Office"));
        assertTrue(str.contains("123 Main St"));
        assertTrue(str.contains("ID:"));
    }
}