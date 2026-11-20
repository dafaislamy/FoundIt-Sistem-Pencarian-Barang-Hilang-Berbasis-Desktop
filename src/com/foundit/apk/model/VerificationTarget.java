package com.foundit.apk.model;

public interface VerificationTarget {
    void approve(User admin);
    void reject(User admin);
    boolean isPending();
}

