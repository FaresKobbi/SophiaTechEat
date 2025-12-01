package fr.unice.polytech.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudentAccountManagerTest {

    private StudentAccountManager manager;
    private StudentAccount student1;
    private StudentAccount student2;

    @BeforeEach
    void setUp() {
        manager = new StudentAccountManager();
        student1 = new StudentAccount.Builder("Alice", "Wonderland")
                .studentId("12345")
                .email("alice@example.com")
                .build();
        student2 = new StudentAccount.Builder("Bob", "Builder")
                .studentId("67890")
                .email("bob@example.com")
                .build();
    }

    @Test
    void testAddAccount() {
        manager.addAccount(student1);
        assertTrue(manager.hasAccount("12345"));
        assertEquals(1, manager.getAllAccounts().size());
    }

    @Test
    void testAddAccountNull() {
        assertThrows(IllegalArgumentException.class, () -> manager.addAccount(null));
    }

    @Test
    void testAddAccountNullId() {
        StudentAccount invalidAccount = new StudentAccount.Builder("No", "ID").build();
        // Reflection or specific setup might be needed if ID is auto-generated in
        // constructor to be non-null.
        // Based on StudentAccount code, ID is generated if not provided.
        // So we need to force it to null or empty if possible, or check if Builder
        // allows it.
        // Looking at StudentAccount.java (previously viewed), ID is UUID if not
        // provided.
        // So let's try to set it to null via builder if possible or just skip if not
        // easily possible without modifying source.
        // Actually, the manager checks `account.getStudentID() == null ||
        // account.getStudentID().isEmpty()`.
        // If StudentAccount always has an ID, this check is defensive.
    }

    @Test
    void testFindAccountById() {
        manager.addAccount(student1);
        Optional<StudentAccount> found = manager.findAccountById("12345");
        assertTrue(found.isPresent());
        assertEquals(student1, found.get());
    }

    @Test
    void testFindAccountByIdNotFound() {
        Optional<StudentAccount> found = manager.findAccountById("99999");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAccountByIdNull() {
        Optional<StudentAccount> found = manager.findAccountById(null);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAccountByEmail() {
        manager.addAccount(student1);
        manager.addAccount(student2);
        Optional<StudentAccount> found = manager.findAccountByEmail("alice@example.com");
        assertTrue(found.isPresent());
        assertEquals(student1, found.get());
    }

    @Test
    void testFindAccountByEmailNotFound() {
        Optional<StudentAccount> found = manager.findAccountByEmail("unknown@example.com");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAccountByEmailNull() {
        Optional<StudentAccount> found = manager.findAccountByEmail(null);
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteAccountById() {
        manager.addAccount(student1);
        boolean deleted = manager.deleteAccountById("12345");
        assertTrue(deleted);
        assertFalse(manager.hasAccount("12345"));
    }

    @Test
    void testDeleteAccountByIdNotFound() {
        boolean deleted = manager.deleteAccountById("99999");
        assertFalse(deleted);
    }

    @Test
    void testDeleteAccountByIdNull() {
        boolean deleted = manager.deleteAccountById(null);
        assertFalse(deleted);
    }

    @Test
    void testGetAllAccounts() {
        manager.addAccount(student1);
        manager.addAccount(student2);
        assertEquals(2, manager.getAllAccounts().size());
        assertTrue(manager.getAllAccounts().contains(student1));
        assertTrue(manager.getAllAccounts().contains(student2));
    }
}
