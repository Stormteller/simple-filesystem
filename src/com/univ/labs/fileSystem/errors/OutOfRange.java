package com.univ.labs.fileSystem.errors;

/**
 * Created by Andrey on 05/07/2017.
 */
public class OutOfRange extends Exception {
    public OutOfRange () {
        super();
    }

    public OutOfRange (String message) {
        super(message);
    }
}
