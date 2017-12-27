package com.univ.labs.fileSystem.errors;

/**
 * Created by Andrey on 05/07/2017.
 */
public class AlreadyOpened extends Exception {
    public AlreadyOpened() {
        super();
    }

    public AlreadyOpened(String message) {
        super(message);
    }
}
