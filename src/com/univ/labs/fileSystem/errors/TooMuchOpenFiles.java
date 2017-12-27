package com.univ.labs.fileSystem.errors;

/**
 * Created by Andrey on 05/06/2017.
 */
public class TooMuchOpenFiles extends Exception {
    public TooMuchOpenFiles() {
        super();
    }

    public TooMuchOpenFiles(String message) {
        super(message);
    }
}
