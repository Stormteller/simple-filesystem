package com.univ.labs.fileSystem.file;

import com.univ.labs.fileSystem.fileDescriptor.FileDescriptorWrapper;

/**
 * Created by Andrey on 04/16/2017.
 */
public class File {
    public FileDescriptorWrapper descriptorWrapper;
    public byte[] buffer;
    public int position = 0;
    public boolean isModified = false;

    public File(FileDescriptorWrapper descriptorWrapper) {
        if(descriptorWrapper.descriptor.fileLength == -1 ||
           descriptorWrapper.descriptor.blocksIndices[0] == -1) throw new IllegalArgumentException();

        this.descriptorWrapper = descriptorWrapper;
    }

    public File(FileDescriptorWrapper descriptorWrapper, byte[] buffer) {
        if(descriptorWrapper.descriptor.fileLength == -1 ||
                descriptorWrapper.descriptor.blocksIndices[0] == -1) throw new IllegalArgumentException();

        this.descriptorWrapper = descriptorWrapper;
        this.buffer = buffer;
    }
}