package com.univ.labs.fileSystem.directory;

import com.univ.labs.fileSystem.FSConfig;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Andrey on 05/06/2017.
 */
public class DirectoryEntry {
    public byte[] name = new byte[FSConfig.fileNameMaxLength];
    public int descriptorIndex;

    public DirectoryEntry(int index, String name) {
        descriptorIndex = index;
        this.name = name.getBytes();
    }

    public DirectoryEntry(int index, byte[] nameBytes) {
        descriptorIndex = index;
        this.name = nameBytes;
    }

    public static int getByteLength() {
        return Integer.BYTES + FSConfig.fileNameMaxLength;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(descriptorIndex);
        byte[] indexBytes = buffer.array();

        //Concat byte arrays
        byte[] res = new byte[indexBytes.length + FSConfig.fileNameMaxLength]; // name.length
        System.arraycopy(indexBytes, 0, res, 0, indexBytes.length);
        System.arraycopy(name, 0, res, indexBytes.length, name.length);
        return res;
    }

    public static DirectoryEntry fromBytes(byte[] entryBytes) {
        if(Arrays.equals(entryBytes, new byte[entryBytes.length])) return null;

        byte[] indexBytes = new byte[Integer.BYTES];
        byte[] nameBytes = new byte[FSConfig.fileNameMaxLength];
        System.arraycopy(entryBytes, 0, indexBytes, 0, Integer.BYTES);
        System.arraycopy(entryBytes, Integer.BYTES, nameBytes, 0, entryBytes.length - Integer.BYTES);

        int index = ByteBuffer.wrap(indexBytes).getInt();
        return new DirectoryEntry(index, nameBytes);
    }
}
