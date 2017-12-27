package com.univ.labs.fileSystem.fileDescriptor;

/**
 * Created by Andrey on 05/06/2017.
 */
public class FileDescriptorWrapper {
    public FileDescriptor descriptor;
    public int index;

    public FileDescriptorWrapper(FileDescriptor descriptor, int index) {
        this.descriptor = descriptor;
        this.index = index;
    }
}
