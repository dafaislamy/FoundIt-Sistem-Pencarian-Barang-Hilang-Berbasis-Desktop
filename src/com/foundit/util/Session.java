package com.foundit.util;

import com.foundit.model.Account;
import com.foundit.model.Admin;
import com.foundit.model.User;

public final class Session {

    private static Account currentAccount;

    private Session() {
    }

    public static void login(Account account) {
        currentAccount = account;
    }

    public static void logout() {
        currentAccount = null;
    }

    public static Account getCurrent() {
        return currentAccount;
    }

    public static boolean isAdmin() {
        return currentAccount instanceof Admin
                || (currentAccount instanceof User u && "admin".equalsIgnoreCase(u.getRole()));
    }

    public static User getUser() {
        return currentAccount instanceof User u ? u : null;
    }

    public static Admin getAdmin() {
        return currentAccount instanceof Admin a ? a : null;
    }

    public static String getDisplayName() {
        if (currentAccount != null && currentAccount.getFullName() != null && !currentAccount.getFullName().isBlank()) {
            return currentAccount.getFullName();
        }
        return "Pengguna FoundIt";
    }

    public static String getDisplayEmail() {
        if (currentAccount != null && currentAccount.getEmail() != null && !currentAccount.getEmail().isBlank()) {
            return currentAccount.getEmail();
        }
        return "-";
    }

    public static String getInitial() {
        String base = currentAccount != null && currentAccount.getFullName() != null && !currentAccount.getFullName().isBlank()
                ? currentAccount.getFullName().trim()
                : (currentAccount != null && currentAccount.getEmail() != null && !currentAccount.getEmail().isBlank()
                        ? currentAccount.getEmail().trim()
                        : "U");
        return base.substring(0, 1).toUpperCase();
    }
}