package fr.unice.polytech.paymentProcessing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class MockedExternalPaymentSystemTest {

    private MockedExternalPaymentSystem paymentSystem;

    @BeforeEach
    void setUp() {
        paymentSystem = new MockedExternalPaymentSystem();
    }

    @Test
    void testMockedCheckingInformationValid() {
        BankInfo validBankInfo = new BankInfo("1234567890123456", 123, 12, YearMonth.now().getYear() + 1);
        assertTrue(paymentSystem.mockedCheckingInformation(validBankInfo));
    }

    @Test
    void testMockedCheckingInformationNull() {
        assertFalse(paymentSystem.mockedCheckingInformation(null));
    }

    @Test
    void testMockedCheckingInformationExpired() {
        BankInfo expiredBankInfo = new BankInfo("1234567890123456", 123, 1, 2020);
        assertFalse(paymentSystem.mockedCheckingInformation(expiredBankInfo));
    }
}
