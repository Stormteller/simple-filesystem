package com.univ.labs.fileSystem;

/**
 * Created by Andrey on 04/16/2017.
 */
public class FSConfig {
    public static final int blockSize = 64;
    public static final int blockNum = 64;
    public static final int blocksForBitmap = 1;
    public static final int OFTSize = 4;
    public static final int fileDescriptorsNumber = 16;
    public static final int bitmapBlockStartIndex = 0;
    public static final int fileMaxBlocksLength = 3;
    public static final int fileNameMaxLength = 4;
    public static final int defaulRootDescriptorIndex = bitmapBlockStartIndex + blocksForBitmap;
}
