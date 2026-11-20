package com.foundit.apk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model penyimpanan data pengguna untuk Admin.
 */
public class UserRepository {
    private static final UserRepository INSTANCE = new UserRepository();
    private final List<User> userList = new ArrayList<>();

    private UserRepository() {
        userList.add(new User("U1", "Budi Santoso", "220101002", "budi@campus.ac.id", "pass", "081234567890", Role.MAHASISWA));
        userList.add(new User("U2", "Sarah Amelia", "230102045", "sarah@campus.ac.id", "pass", "089876543211", Role.MAHASISWA));
        userList.add(new User("U3", "Dimas Anggara", "210103099", "dimas@campus.ac.id", "pass", "085211223344", Role.MAHASISWA));
    }

    public static UserRepository getInstance() {
        return INSTANCE;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void addUser(String nama, String nim, String email, String telp) {
        userList.add(new User("U" + (userList.size() + 1), nama, nim, email, "pass", telp, Role.MAHASISWA));
    }

    public void removeUser(int index) {
        if (index >= 0 && index < userList.size()) {
            userList.remove(index);
        }
    }
}
