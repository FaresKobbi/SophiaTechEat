package fr.unice.polytech.users;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentAccountTest {

    private final String NAME = "Alice";
    private final String SURNAME = "Smith";
    private final String EMAIL = "alice.smith@etu.unice.fr";
    private final double INITIAL_BALANCE = 50.00;

    // Test 1: Verify constructor initialization and inherited attributes
    @Test
    void testStudentAccountCreation() {
        StudentAccount student = new StudentAccount(NAME, SURNAME, EMAIL, INITIAL_BALANCE);
        
        // Inherited from UserAccount
        assertEquals(NAME, student.getName(), "Name should be inherited correctly.");
        assertEquals(SURNAME, student.getSurname(), "Surname should be inherited correctly.");
        assertEquals(EMAIL, student.getEmail(), "Email should be inherited correctly.");
        
        // StudentAccount specific attributes
        assertEquals(INITIAL_BALANCE, student.getBalance(), 0.001, "Initial balance should be set correctly.");
        assertNull(student.getStudentID(), "StudentID should be null initially (not set in constructor).");
    }
    
    // NOTE: Since the provided StudentAccount class is missing debit/credit methods, 
    // the only testable functionality beyond constructor/getters is inherited logic.
    // If you add debit/credit, you must add tests for them here.
}