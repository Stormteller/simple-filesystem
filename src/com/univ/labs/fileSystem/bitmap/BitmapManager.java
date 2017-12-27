package com.univ.labs.fileSystem.bitmap;

import com.univ.labs.fileSystem.FSConfig;
import com.univ.labs.iosystem.IOSystem;

/**
 * Created by Andrey on 04/16/2017.
 */
public class BitmapManager {
    private static IOSystem disk = IOSystem.getInstance();

    public static void init() {
        for(int i = 0; i < FSConfig.blocksForBitmap; i++) {
            markBlockAsUsed(i);
        }
    }

    private static void setIsBlockFreeValue(int blockIndex, boolean isFree) {
        int offsetBlocks = FSConfig.bitmapBlockStartIndex + blockIndex / (FSConfig.blockSize * Byte.SIZE);
        int offsetBytes = (blockIndex % (FSConfig.blockSize * Byte.SIZE)) / Byte.SIZE;
        int offsetBits = (blockIndex % (FSConfig.blockSize * Byte.SIZE)) % Byte.SIZE;

        byte[] bitmapBlock = disk.readBlock(offsetBlocks);
        byte b = bitmapBlock[offsetBytes];
        if(isFree) b = (byte) (b & ~(1 << offsetBits));
        else b = (byte) (b | (1 << offsetBits));
        bitmapBlock[offsetBytes] = b;
        disk.writeBlock(offsetBlocks, bitmapBlock);
    }

    public static void markBlockAsFree(int blockIndex) {
        setIsBlockFreeValue(blockIndex, true);
    }

    public static void markBlockAsUsed(int blockIndex) {
        setIsBlockFreeValue(blockIndex, false);
    }

    public static int getFreeBlockIndex() {
        for (int blockIndex = FSConfig.bitmapBlockStartIndex;
            blockIndex < FSConfig.bitmapBlockStartIndex + FSConfig.blocksForBitmap; blockIndex++) {
            byte[] bitmapBlock = disk.readBlock(blockIndex);
            for (int byteIndex = 0; byteIndex < FSConfig.blockSize; byteIndex++) {
                byte bitmapByte = bitmapBlock[byteIndex];
                for (int bitIndex = 0; bitIndex < Byte.SIZE; bitIndex++) {
                    if (((bitmapByte >> bitIndex) & 1) == 0) {
                        int index = (((blockIndex * FSConfig.blockSize) + byteIndex) * Byte.SIZE) + bitIndex;
                        if (index < FSConfig.blockNum) return index;
                        else return -1;
                    }
                }
            }
        }
        return -1;
    }
}
