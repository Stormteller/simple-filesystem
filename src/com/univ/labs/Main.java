package com.univ.labs;

import com.univ.labs.fileSystem.AMDFileSystem;
import com.univ.labs.fileSystem.errors.*;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws OutOfMemory, FileAlreadyExists, NameTooLong, NoSuchFile, TooMuchOpenFiles, OutOfRange, AlreadyOpened, OpenFileDestruction {
        AMDFileSystem fs = new AMDFileSystem();

        fs.create("jj");
        fs.create("aaa");
        int index = fs.open("jj");
        byte[] b = "5kjkh5".getBytes();
        System.out.println(Arrays.toString(b));
        fs.write(index, b);
        fs.close(index);
        index = fs.open("jj");
        byte[] data = fs.read(index, 5);
        System.out.println(new String((data)));
        System.out.println(Arrays.toString(fs.directory().toArray()));
        fs.close(index);
        fs.destroy("jj");
        System.out.println(Arrays.toString(fs.directory().toArray()));
    }
}
