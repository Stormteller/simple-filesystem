package com.univ.labs.fileSystem.directory;

import com.univ.labs.fileSystem.bitmap.BitmapManager;
import com.univ.labs.fileSystem.file.DataManager;
import com.univ.labs.fileSystem.FSConfig;
import com.univ.labs.fileSystem.file.File;
import com.univ.labs.fileSystem.errors.OutOfMemory;
import com.univ.labs.fileSystem.fileDescriptor.FileDescriptorManager;
import com.univ.labs.fileSystem.fileDescriptor.FileDescriptorWrapper;
import com.univ.labs.iosystem.IOSystem;

import java.util.Arrays;

/**
 * Created by Andrey on 05/06/2017.
 */
public class DirectoryManager {
    private static IOSystem disk = IOSystem.getInstance();

    public static FileDescriptorWrapper initFromFile() throws OutOfMemory {
        FileDescriptorWrapper directoryDescriptorWrapper =
                FileDescriptorManager.readFromDisk(FSConfig.defaulRootDescriptorIndex);

        assert directoryDescriptorWrapper != null;

        // Directory directoryData = DirectoryManager.readFromDisk(new File(directoryDescriptorWrapper));
        return directoryDescriptorWrapper;
    }

    public static FileDescriptorWrapper init() throws OutOfMemory {
       // FileDescriptorWrapper directoryDescriptorWrapper = FileDescriptorManager.getFreeFileDescriptor();
        FileDescriptorWrapper directoryDescriptorWrapper =
                FileDescriptorManager.readFromDisk(FSConfig.defaulRootDescriptorIndex);

        assert directoryDescriptorWrapper != null;

        Directory directoryData = new Directory();

        byte[] directoryBytes = directoryData.toBytes();
        int directoryBlocksNum = Directory.getLengthBlocks();

        if(directoryBlocksNum > FSConfig.fileMaxBlocksLength) throw new OutOfMemory("Directory is too big");

        for(int i = 0; i < Directory.getLengthBlocks(); i++) {
            int freeBlockIndex = BitmapManager.getFreeBlockIndex();
            assert freeBlockIndex == FSConfig.blocksForBitmap + FSConfig.fileDescriptorsNumber + i;

            BitmapManager.markBlockAsUsed(freeBlockIndex);
            byte[] blockToWrite = Arrays.copyOfRange(directoryBytes,
                    i * FSConfig.blockSize,
                    Math.min((i + 1) * FSConfig.blockSize, directoryBytes.length));
            disk.writeBlock(freeBlockIndex, blockToWrite);
            directoryDescriptorWrapper.descriptor.blocksIndices[i] = freeBlockIndex;
        }
        directoryDescriptorWrapper.descriptor.fileLength = directoryData.toBytes().length;
        FileDescriptorManager.saveToDisk(directoryDescriptorWrapper);

        return directoryDescriptorWrapper;
    }

    public static void saveToDisk(File file, Directory directory) {
       // if (file.isModified) {
            byte[] directoryBytes = directory.toBytes();
            DataManager.writeFullFileToDisk(file, directoryBytes);
        //}
//        for(int i = 0; i < Directory.getLengthBlocks(); i++) {
//            byte[] blockToWrite = Arrays.copyOfRange(directoryBytes,
//                    i * FSConfig.blockSize,
//                    Math.min((i + 1) * FSConfig.blockSize, directoryBytes.length));
//            disk.writeBlock(FSConfig.blocksForBitmap + i, blockToWrite);
//        }
    }

    public static Directory readFromDisk(File file) {
        byte[] directoryBytes = DataManager.readFullFileBytes(file);
        return Directory.fromBytes(directoryBytes);
    }



    public static int getFreeEntryIndex(Directory directory) {
        for(int i = 0; i < directory.entries.length; i++) {
            if(directory.entries[i] == null) return i;
        }
        return -1;
    }

    public static int getEntryIndexByName(Directory directory, String filename){
        byte[] filenameBytes = filename.getBytes();
        byte[] filenameBytesNormalized = new byte[FSConfig.fileNameMaxLength];
        System.arraycopy(filenameBytes, 0, filenameBytesNormalized, 0, filenameBytes.length);
        for (int i = 0; i < directory.entries.length; i++) {
            if(directory.entries[i] != null && Arrays.equals(directory.entries[i].name, filenameBytesNormalized))
                return i;
        }
        return -1;
    }
}
