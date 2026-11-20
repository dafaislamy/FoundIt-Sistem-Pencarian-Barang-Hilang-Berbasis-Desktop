package com.foundit.util;

/** Exception aplikasi dengan pesan yang aman/ramah untuk ditampilkan ke pengguna. */
public class AppException extends Exception {

    private static final long serialVersionUID = 1L;

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
