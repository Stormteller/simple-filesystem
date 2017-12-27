package com.univ.labs.fileSystem.file;

import com.univ.labs.fileSystem.FSConfig;
import com.univ.labs.fileSystem.bitmap.BitmapManager;
import com.univ.labs.fileSystem.errors.OutOfMemory;
import com.univ.labs.fileSystem.fileDescriptor.FileDescriptor;
import com.univ.labs.fileSystem.fileDescriptor.FileDescriptorManager;
import com.univ.labs.iosystem.IOSystem;

import java.util.Arrays;

/**
 * Created by Andrey on 05/06/2017.
 */
public class DataManager {
    private static IOSystem disk = IOSystem.getInstance();

    public static byte[] readFullFileBytes(File file) {
        FileDescriptor fileDescriptor = file.descriptorWrapper.descriptor;
        byte[] resBytes = new byte[fileDescriptor.fileLength];
        int fileBlocksNum = fileDescriptor.fileLength / FSConfig.blockSize;
        int bytesInLastBlock = fileDescriptor.fileLength % FSConfig.blockSize == 0 ?
                FSConfig.blockSize : fileDescriptor.fileLength % FSConfig.blockSize;

        for(int i = 0; i < fileBlocksNum; i++) {
            int blockIndex = fileDescriptor.blocksIndices[i];
            if(blockIndex == -1) throw new IllegalArgumentException();

            byte[] currBlock = disk.readBlock(fileDescriptor.blocksIndices[i]);
            int copyLen = i == fileBlocksNum - 1 ? bytesInLastBlock : FSConfig.blockSize;
            System.arraycopy(currBlock,0, resBytes, i * FSConfig.blockSize, copyLen);
        }
        return resBytes;
    }

    public static void writeFullFileToDisk(File file, byte[] data) {
        FileDescriptor fileDescriptor = file.descriptorWrapper.descriptor;
        int blocksToWrite = data.length / FSConfig.blockSize;

        if(blocksToWrite > FSConfig.fileMaxBlocksLength)
            throw new IllegalArgumentException("Error: blocksToWrite > FSConfig.fileMaxBlocksLength");

        int writeInLastBlock = data.length % FSConfig.blockSize == 0 ?
                FSConfig.blockSize : data.length % FSConfig.blockSize;

        for(int i = 0; i < blocksToWrite; i++) {
            int blockIndex = fileDescriptor.blocksIndices[i];
            if(fileDescriptor.blocksIndices[i] == -1) {
                int freeBlock = BitmapManager.getFreeBlockIndex();
                fileDescriptor.blocksIndices[i] = freeBlock;
                BitmapManager.markBlockAsUsed(freeBlock);
                blockIndex = freeBlock;
            }

            int writeLen = i == blocksToWrite - 1 ? writeInLastBlock : FSConfig.blockSize;
            byte[] currBlock = Arrays.copyOfRange(data, i * FSConfig.blockSize, i * FSConfig.blockSize + writeLen);
            disk.writeBlock(blockIndex, currBlock);
        }

        FileDescriptorManager.saveToDisk(file.descriptorWrapper);
    }

    public static void writeCurrentBlockToDisk(File file) throws OutOfMemory {
        FileDescriptor fileDescriptor = file.descriptorWrapper.descriptor;
        int blockNumber = file.position / FSConfig.blockSize;
        if(fileDescriptor.blocksIndices[blockNumber] == -1) {
            int freeBlock = BitmapManager.getFreeBlockIndex();
            if(freeBlock == -1)
                throw new OutOfMemory("No empty block");
            fileDescriptor.blocksIndices[blockNumber] = freeBlock;
            BitmapManager.markBlockAsUsed(freeBlock);
        }
        disk.writeBlock(fileDescriptor.blocksIndices[blockNumber], file.buffer);
        FileDescriptorManager.saveToDisk(file.descriptorWrapper);
    }

    public static byte[] readBytesByBlockIndex(int index) {
        return disk.readBlock(index);
    }
}
