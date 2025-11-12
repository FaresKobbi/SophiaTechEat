package fr.unice.polytech.users;

import java.util.*;

public class StudentAccountManager {
    private final Map<String, StudentAccount> studentAccounts;

    public StudentAccountManager() {
        this.studentAccounts = new HashMap<>();
    }

    /**
     * Adds a new student account to the system.
     * If an account with the same ID already exists, it will be overwritten.
     *
     * @param account The StudentAccount object to add or update.
     * @throws IllegalArgumentException if the account or its ID is null.
     */
    public void addAccount(StudentAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Student account cannot be null.");
        }
        if (account.getStudentID() == null || account.getStudentID().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        studentAccounts.put(account.getStudentID(), account);
    }

    /**
     * Finds a student account by their unique student ID.
     *
     * @param studentId The ID of the student to find.
     * @return An Optional containing the StudentAccount if found, or an empty Optional otherwise.
     */
    public Optional<StudentAccount> findAccountById(String studentId) {
        if (studentId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(studentAccounts.get(studentId));
    }

    /**
     * Finds a student account by their email address.
     * Note: This is less efficient as it searches all values.
     *
     * @param email The email of the student to find.
     * @return An Optional containing the StudentAccount if found, or an empty Optional otherwise.
     */
    public Optional<StudentAccount> findAccountByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return studentAccounts.values().stream()
                .filter(account -> email.equals(account.getEmail()))
                .findFirst();
    }

    /**
     * Deletes a student account from the system using their student ID.
     *
     * @param studentId The ID of the student to delete.
     * @return true if an account was successfully removed, false otherwise.
     */
    public boolean deleteAccountById(String studentId) {
        if (studentId == null) {
            return false;
        }
        return studentAccounts.remove(studentId) != null;
    }

    /**
     * Retrieves a list of all student accounts currently in the system.
     *
     * @return A new List containing all StudentAccount objects.
     */
    public List<StudentAccount> getAllAccounts() {
        return new ArrayList<>(studentAccounts.values());
    }

    /**
     * Checks if an account with the given ID exists.
     *
     * @param studentId The ID to check.
     * @return true if an account with this ID is registered, false otherwise.
     */
    public boolean hasAccount(String studentId) {
        return studentId != null && studentAccounts.containsKey(studentId);
    }

}
