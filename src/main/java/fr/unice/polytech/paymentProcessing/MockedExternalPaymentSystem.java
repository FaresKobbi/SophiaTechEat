package fr.unice.polytech.paymentProcessing;

import java.time.YearMonth;

public class MockedExternalPaymentSystem {

    public MockedExternalPaymentSystem() {
    }


    public boolean mockedCheckingInformation(BankInfo bankInfo){
        if (bankInfo == null) {
            return false;
        }

        if (bankInfo.getExpirationDate().isBefore(YearMonth.now())) {
            return false;
        }

        
        return true;
    }
}
