package com.univ.labs.fileSystem.fileDescriptor;

import com.univ.labs.fileSystem.FSConfig;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Created by Andrey on 04/16/2017.
 */

public class FileDescriptor {
    public int fileLength;
    public int[] blocksIndices;
    public boolean isModified = false;

    FileDescriptor() {
        this.fileLength = -1;
        this.blocksIndices = new int[FSConfig.fileMaxBlocksLength];
        Arrays.fill(blocksIndices, -1);
    }

    FileDescriptor(int fileLength) {
        this.fileLength = fileLength;
        this.blocksIndices = new int[FSConfig.fileMaxBlocksLength];
        Arrays.fill(blocksIndices, -1);
    }


    FileDescriptor(int fileLength, int[] blocksIndices) {
        this.fileLength = fileLength;
        this.blocksIndices = blocksIndices;
    }


    byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Integer.BYTES * blocksIndices.length);
        buffer.putInt(fileLength);
        for (int blockIndex : blocksIndices)
            buffer.putInt(blockIndex);

        return buffer.array();
    }

    static FileDescriptor fromBytes(byte[] fdBytes) {
        byte[] fileLengthBytes = new byte[Integer.BYTES];
        byte[] blocksIndicesBytes = new byte[FSConfig.fileMaxBlocksLength * Integer.BYTES];
        System.arraycopy(fdBytes, 0, fileLengthBytes, 0, Integer.BYTES);
        System.arraycopy(fdBytes, Integer.BYTES, blocksIndicesBytes, 0, FSConfig.fileMaxBlocksLength * Integer.BYTES);

        int fileLength = ByteBuffer.wrap(fileLengthBytes).getInt();
        IntBuffer intBuf = ByteBuffer.wrap(blocksIndicesBytes).asIntBuffer();
        int[] blocksIndices = new int[FSConfig.fileMaxBlocksLength];
        intBuf.get(blocksIndices);

//        ByteBuffer buffer = ByteBuffer.wrap(blocksIndicesBytes);
//        int[] blocksIndices = new int[FSConfig.fileMaxBlocksLength];
//        for (byte i = 0; i < blocksIndices.length; ++i)
//            blocksIndices[i] = buffer.get();

        return new FileDescriptor(fileLength, blocksIndices);
    }
}