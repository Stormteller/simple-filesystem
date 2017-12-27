package com.univ.labs.fileSystem.errors;

/**
 * Created by Andrey on 04/26/2017.
 */
public class NameTooLong extends Exception {
    public NameTooLong() {
        super();
    }

    public NameTooLong(String message) {
        super(message);
    }
}
