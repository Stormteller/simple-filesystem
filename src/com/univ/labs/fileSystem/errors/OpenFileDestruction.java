package com.univ.labs.fileSystem.errors;

/**
 * Created by Andrey on 05/07/2017.
 */
public class OpenFileDestruction extends Exception {
    public OpenFileDestruction() {
        super();
    }

    public OpenFileDestruction(String message) {
        super(message);
    }
}
