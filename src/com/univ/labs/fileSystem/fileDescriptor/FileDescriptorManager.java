package com.univ.labs.fileSystem.fileDescriptor;

import com.univ.labs.fileSystem.bitmap.BitmapManager;
import com.univ.labs.fileSystem.FSConfig;
import com.univ.labs.iosystem.IOSystem;

/**
 * Created by Andrey on 05/06/2017.
 */
public class FileDescriptorManager {
    private static IOSystem disk = IOSystem.getInstance();

    public static void init() {
        for(int i = FSConfig.blocksForBitmap; i < FSConfig.blocksForBitmap + FSConfig.fileDescriptorsNumber; i++) {
            FileDescriptor fileDescriptor = new FileDescriptor();
            saveToDisk(new FileDescriptorWrapper(fileDescriptor, i));
            BitmapManager.markBlockAsUsed(i);
        }
    }

    public static FileDescriptorWrapper getFreeFileDescriptor() {
        for(int i = FSConfig.blocksForBitmap; i < FSConfig.blocksForBitmap + FSConfig.fileDescriptorsNumber; i++) {
            FileDescriptorWrapper fdWrapper = readFromDisk(i);
            if(fdWrapper.descriptor.fileLength == -1) return fdWrapper;
        }
        //TODO: is it possible to find an empty descriptor in the bitmap?
        return null;
    }

    public static void saveToDisk(FileDescriptorWrapper descriptorWrapper) {
        disk.writeBlock(descriptorWrapper.index, descriptorWrapper.descriptor.toBytes());
    }

    public static FileDescriptorWrapper readFromDisk(int index) {
        byte[] descriptorBytes = disk.readBlock(index);
        return new FileDescriptorWrapper(FileDescriptor.fromBytes(descriptorBytes), index);
    }

}
