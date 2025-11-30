package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.users.StudentAccount;
import org.mockito.internal.matchers.Or;

import java.time.YearMonth;
import java.util.Random;

public class MockedExternalPaymentSystem {

    private final Random random = new Random();

    public MockedExternalPaymentSystem() {
    }


    public boolean mockedCheckingInformation(BankInfo bankInfo){
        if (bankInfo == null) {
            return false;
        }

        if (bankInfo.getExpirationDate().isBefore(YearMonth.now())) {
            return false;
        }

        //return random.nextDouble() < 0.8;
        return true;
    }
}
