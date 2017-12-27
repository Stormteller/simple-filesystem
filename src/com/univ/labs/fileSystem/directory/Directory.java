package com.univ.labs.fileSystem.directory;

import com.univ.labs.fileSystem.FSConfig;

import java.util.Arrays;

/**
 * Created by Andrey on 05/06/2017.
 */
public class Directory {
    public DirectoryEntry[] entries;

    public Directory() {
        this.entries = new DirectoryEntry[FSConfig.fileDescriptorsNumber];
    }

    public Directory(DirectoryEntry[] entries) {
        this.entries = entries;
    }

    public byte[] toBytes() {
        int entryByteLength = DirectoryEntry.getByteLength();
        byte[] res = new byte[entryByteLength * FSConfig.fileDescriptorsNumber];
        for (int i = 0; i < FSConfig.fileDescriptorsNumber; i++) {
            byte[] entriesBytes = entries[i] != null ? entries[i].toBytes() : new byte[entryByteLength];
            System.arraycopy(entriesBytes, 0, res, i * entryByteLength, entryByteLength);
        }
        return res;
    }

    public static Directory fromBytes(byte[] directoryBytes) {
        DirectoryEntry[] entries = new DirectoryEntry[FSConfig.fileDescriptorsNumber];
        for (int i = 0; i < entries.length; i++) {
            byte[] entryBytes = new byte[DirectoryEntry.getByteLength()];
            System.arraycopy(directoryBytes, i * DirectoryEntry.getByteLength(),
                    entryBytes,0, DirectoryEntry.getByteLength());
            entries[i] = DirectoryEntry.fromBytes(entryBytes);
        }
        return new Directory(entries);
    }

    public static int getLengthBlocks() {
        int directoryBytesLength = DirectoryEntry.getByteLength() * FSConfig.fileDescriptorsNumber;
        return directoryBytesLength % FSConfig.blockSize > 0 ?
                directoryBytesLength / FSConfig.blockSize + 1: directoryBytesLength / FSConfig.blockSize;
    }
}
