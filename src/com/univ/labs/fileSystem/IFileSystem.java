package com.univ.labs.fileSystem;

import com.univ.labs.fileSystem.errors.*;

import java.util.List;

/**
 * Created by Andrey on 04/16/2017.
 */
public interface IFileSystem {
    void create(String filename) throws NameTooLong, OutOfMemory, FileAlreadyExists;

    void destroy(String filename) throws NoSuchFile, OpenFileDestruction;

    int open(String filename) throws NoSuchFile, TooMuchOpenFiles, AlreadyOpened;

    void close(int index) throws NoSuchFile, OutOfMemory;

    List<String> directory();

    byte[] read(int index, int count) throws OutOfRange, OutOfMemory;

    void write(int index, byte[] src) throws OutOfMemory;

    void lseek(int index, int pos) throws OutOfRange;
}
