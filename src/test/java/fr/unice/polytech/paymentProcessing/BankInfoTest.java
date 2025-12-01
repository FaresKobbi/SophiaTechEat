package fr.unice.polytech.paymentProcessing;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class BankInfoTest {

    @Test
    void testConstructorAndGetters() {
        BankInfo bankInfo = new BankInfo("1234567890123456", 123, 12, 2025);
        assertEquals("1234567890123456", bankInfo.getCardNumber());
        assertEquals(123, bankInfo.getCVV());
        assertEquals(YearMonth.of(2025, 12), bankInfo.getExpirationDate());
    }

    @Test
    void testEmptyConstructorAndSetters() {
        BankInfo bankInfo = new BankInfo();
        bankInfo.setCardNumber("9876543210987654");
        bankInfo.setCVV(456);
        bankInfo.setExpirationDate(YearMonth.of(2026, 1));

        assertEquals("9876543210987654", bankInfo.getCardNumber());
        assertEquals(456, bankInfo.getCVV());
        assertEquals(YearMonth.of(2026, 1), bankInfo.getExpirationDate());
    }

    @Test
    void testEquals() {
        BankInfo info1 = new BankInfo("1234", 123, 12, 2025);
        BankInfo info2 = new BankInfo("1234", 123, 12, 2025);
        BankInfo info3 = new BankInfo("5678", 123, 12, 2025);
        BankInfo info4 = new BankInfo("1234", 999, 12, 2025);
        BankInfo info5 = new BankInfo("1234", 123, 11, 2025);

        assertEquals(info1, info2);
        assertNotEquals(info1, info3);
        assertNotEquals(info1, info4);
        assertNotEquals(info1, info5);
        assertNotEquals(info1, null);
        assertNotEquals(info1, new Object());
    }

    @Test
    void testHashCode() {
        BankInfo info1 = new BankInfo("1234", 123, 12, 2025);
        BankInfo info2 = new BankInfo("1234", 123, 12, 2025);
        assertEquals(info1.hashCode(), info2.hashCode());
    }
}
