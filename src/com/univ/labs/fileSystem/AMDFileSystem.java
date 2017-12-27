package com.univ.labs.fileSystem;

import com.univ.labs.fileSystem.errors.*;
import com.univ.labs.fileSystem.bitmap.BitmapManager;
import com.univ.labs.fileSystem.directory.Directory;
import com.univ.labs.fileSystem.directory.DirectoryEntry;
import com.univ.labs.fileSystem.directory.DirectoryManager;
import com.univ.labs.fileSystem.file.DataManager;
import com.univ.labs.fileSystem.file.File;
import com.univ.labs.fileSystem.fileDescriptor.FileDescriptor;
import com.univ.labs.fileSystem.fileDescriptor.FileDescriptorManager;
import com.univ.labs.fileSystem.fileDescriptor.FileDescriptorWrapper;
import com.univ.labs.iosystem.IOSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey on 04/16/2017.
 */
public class AMDFileSystem implements IFileSystem {
    private File[] OFT;

    public AMDFileSystem() throws OutOfMemory {
        BitmapManager.init();

        FileDescriptorManager.init();

        OFT = new File[FSConfig.OFTSize];

        FileDescriptorWrapper directoryDescriptorWrapper = DirectoryManager.init();

        OFT[0] = new File(directoryDescriptorWrapper);
    }

    @Override
    public void create(String filename) throws NameTooLong, OutOfMemory, FileAlreadyExists {
        if(filename.length() > FSConfig.fileNameMaxLength) throw new NameTooLong();

        Directory directoryData = DirectoryManager.readFromDisk(OFT[0]);

        int fileIndex = DirectoryManager.getEntryIndexByName(directoryData, filename);
        if(fileIndex != -1) throw new FileAlreadyExists();

        FileDescriptorWrapper freeDescriptorWrapper = FileDescriptorManager.getFreeFileDescriptor();
        if(freeDescriptorWrapper == null) throw new OutOfMemory("No empty descriptor");

        int freeBlockIndex = BitmapManager.getFreeBlockIndex();
        if(freeBlockIndex == -1) throw new OutOfMemory("No empty block");

        int freeEntryIndex = DirectoryManager.getFreeEntryIndex(directoryData);
        if(freeEntryIndex == -1) throw new OutOfMemory("No empty directory entry");

        BitmapManager.markBlockAsUsed(freeBlockIndex);

        freeDescriptorWrapper.descriptor.fileLength = 0;
        freeDescriptorWrapper.descriptor.blocksIndices[0] = freeBlockIndex;
        FileDescriptorManager.saveToDisk(freeDescriptorWrapper);

        DirectoryEntry newEntry = new DirectoryEntry(freeDescriptorWrapper.index, filename);
        directoryData.entries[freeEntryIndex] = newEntry;
        DirectoryManager.saveToDisk(OFT[0], directoryData);
    }

    @Override
    public void destroy(String filename) throws NoSuchFile, OpenFileDestruction {
        Directory directoryData = DirectoryManager.readFromDisk(OFT[0]);

        int entryIndex = DirectoryManager.getEntryIndexByName(directoryData, filename);
        if(entryIndex == -1) throw new NoSuchFile("File not found");

        FileDescriptorWrapper descriptorWrapper =
                FileDescriptorManager.readFromDisk(directoryData.entries[entryIndex].descriptorIndex);
        FileDescriptor fileDescriptor = descriptorWrapper.descriptor;

        int OFTIndex = getOFTIndexByDescriptorIndex(descriptorWrapper.index);
        if (OFTIndex != -1) throw new OpenFileDestruction("Can't delete opened file");

        int i = 0;
        while(fileDescriptor.blocksIndices[i] != -1) {
            BitmapManager.markBlockAsFree(fileDescriptor.blocksIndices[i]);
            i++;
        }
        fileDescriptor.fileLength = -1;
        FileDescriptorManager.saveToDisk(descriptorWrapper);

        directoryData.entries[entryIndex] = null;
        DirectoryManager.saveToDisk(OFT[0], directoryData);
    }

    @Override
    public int open(String filename) throws NoSuchFile, TooMuchOpenFiles, AlreadyOpened {
        Directory directoryData = DirectoryManager.readFromDisk(OFT[0]);

        int entryIndex = DirectoryManager.getEntryIndexByName(directoryData, filename);
        if(entryIndex == -1) throw new NoSuchFile("File not found");

        int freeOFTIndex = getFreeOFTIndex();
        if(freeOFTIndex == -1) throw new TooMuchOpenFiles("Max files opened");

        FileDescriptorWrapper descriptorWrapper = FileDescriptorManager.readFromDisk(directoryData.entries[entryIndex].descriptorIndex);

        int OFTIndex = getOFTIndexByDescriptorIndex(descriptorWrapper.index);
        if (OFTIndex != -1) throw new AlreadyOpened("File already opened");

        byte[] firstFileBlock = DataManager.readBytesByBlockIndex(descriptorWrapper.descriptor.blocksIndices[0]);

        OFT[freeOFTIndex] = new File(descriptorWrapper, firstFileBlock);

        return freeOFTIndex;
    }

    @Override
    public void close(int index) throws NoSuchFile, OutOfMemory {
        File file = OFT[index];

        if(file == null) throw  new IllegalArgumentException("Illegal file index");;

        if (file.isModified)
            DataManager.writeCurrentBlockToDisk(file);

        OFT[index] = null;
    }

    @Override
    public List<String> directory() {
        List<String> fileNames = new ArrayList<>();
        Directory directory = DirectoryManager.readFromDisk(OFT[0]);
        for (int i = 0; i < directory.entries.length; i++) {
            if(directory.entries[i] != null)
                fileNames.add(new String(directory.entries[i].name));
        }
        return fileNames;
    }

    @Override
    public byte[] read(int index, int count){
        File file = OFT[index];
        if(file == null) throw  new IllegalArgumentException("Illegal file index");

        FileDescriptor fileDescriptor = file.descriptorWrapper.descriptor;
        //if(fileDescriptor.fileLength < count) throw new OutOfRange("Count out of file size");

        // will read only possible amount of bytes
        count = Math.min(count, fileDescriptor.fileLength - file.position);

        byte[] res = new byte[count];

        int offset = 0;
        while (offset < count) {
            int bufferPos = file.position % file.buffer.length;
            int blockNum = file.position / file.buffer.length;

            res[offset] = file.buffer[bufferPos];
            offset++;
            file.position++;
            if (bufferPos == file.buffer.length - 1) {
                if(file.isModified)
                    try {
                        DataManager.writeCurrentBlockToDisk(file);
                    } catch (OutOfMemory outOfMemory) {
                    // this situation is not possible - we write into the existing block
                        outOfMemory.printStackTrace();
                    }
                file.buffer = DataManager.readBytesByBlockIndex(fileDescriptor.blocksIndices[blockNum + 1]);
                file.isModified = false;
            }

        }
        return res;
    }

    @Override
    public void write(int index, byte[] src) throws OutOfMemory {
        File file = OFT[index];
        if(file == null) throw  new IllegalArgumentException("Illegal file index");

        FileDescriptor fileDescriptor = file.descriptorWrapper.descriptor;
        if(FSConfig.fileMaxBlocksLength * FSConfig.blockSize - file.position < src.length)
            throw new OutOfMemory("Data is too large");

        int written = 0;
        while(written < src.length) {
            int bufferPos = file.position % file.buffer.length;
            int blockNum = file.position / file.buffer.length;

            if(file.position == fileDescriptor.fileLength) {
                fileDescriptor.fileLength++;
            }

            if(bufferPos == file.buffer.length-1) {
                //TODO: if modified
                DataManager.writeCurrentBlockToDisk(file);
                if (file.descriptorWrapper.descriptor.blocksIndices[blockNum + 1] == -1){
                    int freeBlockIndex = BitmapManager.getFreeBlockIndex();
                    if(freeBlockIndex == -1) throw new OutOfMemory("No empty block");
                    file.descriptorWrapper.descriptor.blocksIndices[blockNum + 1] = freeBlockIndex;
                } //TODO: ELSE
                file.buffer = DataManager.readBytesByBlockIndex(fileDescriptor.blocksIndices[blockNum + 1]);
                file.isModified = false;
            }

            file.buffer[bufferPos] = src[written];
            written++;
            file.position++;
            file.isModified = true;

        }
        FileDescriptorManager.saveToDisk(file.descriptorWrapper);
    }

    @Override
    public void lseek(int index, int pos) throws OutOfRange {
        File file = OFT[index];
        if(file == null) throw  new IllegalArgumentException("Illegal file index");

        if (pos > file.descriptorWrapper.descriptor.fileLength || pos < 0)
            throw new OutOfRange("Position out of range");

        file.position = pos;
        int blockNum = file.position / file.buffer.length;

        //TODO: check if in current block
        if(file.isModified)
            try {
                DataManager.writeCurrentBlockToDisk(file);
            } catch (OutOfMemory outOfMemory) {
                // this situation is not possible - we write into the existing block
                outOfMemory.printStackTrace();
            }
        file.buffer = DataManager.readBytesByBlockIndex(file.descriptorWrapper.descriptor.blocksIndices[blockNum]);
        file.isModified = false;
    }

    public void dump(String filename) throws FileWritingException {
        IOSystem.getInstance().writeDiskToFile(new java.io.File(filename));
    }
    public void load(String filename) throws FileReadingException, OutOfMemory {
        IOSystem.getInstance().readDiskFromFile(new java.io.File(filename));

        OFT = new File[FSConfig.OFTSize];

        FileDescriptorWrapper directoryDescriptorWrapper = DirectoryManager.initFromFile();
        OFT[0] = new File(directoryDescriptorWrapper);
    }

    private int getFreeOFTIndex() {
        for (int i = 0; i < OFT.length; i++) {
            if(OFT[i] == null) return i;
        }
        return -1;
    }

    private int getOFTIndexByDescriptorIndex(int index) {
        for (int i = 0; i < OFT.length; i++) {
            if(OFT[i] != null && OFT[i].descriptorWrapper.index == index) return i;
        }
        return -1;
    }
}
