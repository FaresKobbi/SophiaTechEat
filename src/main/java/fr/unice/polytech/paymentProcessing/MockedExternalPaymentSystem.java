package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orders.*;
import fr.unice.polytech.users.StudentAccount;

import java.time.YearMonth;
import java.util.Random;

public class MockedExternalPaymentSystem {

    private final Order order;
    private final Random random = new Random();


    public MockedExternalPaymentSystem(Order order) {
        this.order = order;
    }

    public boolean mockedCheckingInformation() {
        BankInfo bankInfo = ((StudentAccount)order.getClient()).getBankInfo();

        if (bankInfo.getExpirationDate().isBefore(YearMonth.now())) {
            return false;
        }

        return random.nextDouble() < 0.8;

    }
}
