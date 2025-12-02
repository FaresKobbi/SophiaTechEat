package fr.unice.polytech.users;

import java.util.*;

public class StudentAccountManager {
    private final Map<String, StudentAccount> studentAccounts;

    public StudentAccountManager() {
        this.studentAccounts = new HashMap<>();
    }

    
    public void addAccount(StudentAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Student account cannot be null.");
        }
        if (account.getStudentID() == null || account.getStudentID().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        studentAccounts.put(account.getStudentID(), account);
    }

    
    public Optional<StudentAccount> findAccountById(String studentId) {
        if (studentId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(studentAccounts.get(studentId));
    }

    
    public Optional<StudentAccount> findAccountByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return studentAccounts.values().stream()
                .filter(account -> email.equals(account.getEmail()))
                .findFirst();
    }

    
    public boolean deleteAccountById(String studentId) {
        if (studentId == null) {
            return false;
        }
        return studentAccounts.remove(studentId) != null;
    }

    
    public List<StudentAccount> getAllAccounts() {
        return new ArrayList<>(studentAccounts.values());
    }

    
    public boolean hasAccount(String studentId) {
        return studentId != null && studentAccounts.containsKey(studentId);
    }

}
