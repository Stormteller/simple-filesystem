package com.univ.labs.fileSystem.errors;

/**
 * Created by Andrey on 04/26/2017.
 */
public class OutOfMemory extends Exception {
    public OutOfMemory() {
        super();
    }

    public OutOfMemory(String message) {
        super(message);
    }
}
