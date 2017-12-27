package com.univ.labs.fileSystem.errors;

import java.io.IOException;

/**
 * Created by Masha Kereb on 24-Apr-17.
 */
public class FileWritingException extends IOException {
    public FileWritingException(String message) {
        super(message);
    }

    public FileWritingException() {
    }
}
