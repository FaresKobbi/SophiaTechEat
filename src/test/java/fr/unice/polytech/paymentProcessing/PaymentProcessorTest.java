package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.users.StudentAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class PaymentProcessorTest {

    private final String NAME = "Alice";
    private final String SURNAME = "Smith";
    private final String EMAIL = "alice.smith@etu.unice.fr";
    private final String ID = "22400632";

    Order order;

    @BeforeEach
    void setUp() {
        StudentAccount student = new StudentAccount.Builder(NAME, SURNAME)
                .email(EMAIL)
                .studentId(ID)
                .bankInfo("3151 2136 8946 4151", 401, 5,28)
                .build();

        //order = new Order(student, 20,null);
    }


    @Test
    void testPaymentProcessing(){
    }

}