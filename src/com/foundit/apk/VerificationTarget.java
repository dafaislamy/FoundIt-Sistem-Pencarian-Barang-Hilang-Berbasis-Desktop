package com.foundit.apk;

public interface VerificationTarget {
    void approve(User admin);
    void reject(User admin);
    boolean isPending();
}
