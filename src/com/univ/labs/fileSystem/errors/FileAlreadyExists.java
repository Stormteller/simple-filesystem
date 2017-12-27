package com.univ.labs.fileSystem.errors;

/**
 * Created by Andrey on 04/26/2017.
 */
public class FileAlreadyExists extends Exception {
    public FileAlreadyExists() {
        super();
    }

    public FileAlreadyExists(String message) {
        super(message);
    }
}
