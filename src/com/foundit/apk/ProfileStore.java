package com.foundit.apk.view;

import java.util.prefs.Preferences;

final class ProfileStore {
    private static final Preferences PREF = Preferences.userNodeForPackage(ProfileStore.class);
    private ProfileStore() {}
    static String get(String email, String key, String fallback) {
        return PREF.get(email.toLowerCase() + "." + key, fallback);
    }
    static void set(String email, String key, String value) {
        PREF.put(email.toLowerCase() + "." + key, value == null ? "" : value);
    }
}
