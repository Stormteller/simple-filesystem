package com.univ.labs.fileSystem.errors;

/**
 * Created by Masha Kereb on 24-Apr-17.
 */
public class ShellArgumentsException extends Exception{
    public ShellArgumentsException() {
    }

    public ShellArgumentsException(String argName) {
        super("Illegal argument " + argName);
    }
}
