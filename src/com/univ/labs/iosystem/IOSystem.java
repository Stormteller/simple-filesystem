package com.univ.labs.iosystem;

import com.univ.labs.fileSystem.FSConfig;
import com.univ.labs.fileSystem.errors.FileReadingException;
import com.univ.labs.fileSystem.errors.FileWritingException;

import java.io.*;

/**
 * Created by Andrey on 04/16/2017.
 */
public class IOSystem {
    private byte[][] logicDisk;

    private int blockSize;
    private int blockNum;

    private static IOSystem instance;

    private IOSystem() {
        this.blockNum = FSConfig.blockNum;
        this.blockSize = FSConfig.blockSize;
        logicDisk = new byte[blockNum][blockSize];
    }

    public static IOSystem getInstance() {
        if (instance == null) {
            instance = new IOSystem();
        }
        return instance;
    }

    public byte[] readBlock(int index) {
        if (index >= blockNum || index < 0) throw new IllegalArgumentException();
        return logicDisk[index];
    }

    public void writeBlock(int index, byte[] buffer) {
        if (index >= blockNum || index < 0) throw new IllegalArgumentException();

        for (int i = 0; i < logicDisk[index].length; i++) {
            if (i < buffer.length) logicDisk[index][i] = buffer[i];
            else logicDisk[index][i] = 0;
        }
    }

    public void readDiskFromFile(File file) throws FileReadingException {

        try {
            FileInputStream scanner = new FileInputStream(file);


            byte[] block = new byte[4];
            scanner.read(block);
            int blNum = Converter.intFromByteArray(block);

            scanner.read(block);
            int blsize = Converter.intFromByteArray(block);

            byte[][] newLogicDisk = new byte[blNum][blsize];
            for (int i = 0; i < blNum; i++) {
                scanner.read(newLogicDisk[i]);

            }
            scanner.close();
            logicDisk = newLogicDisk;
            blockNum = blNum;
            blockSize = blsize;
        } catch (IOException e) {
            throw new FileReadingException("File reading error");
        }


    }

    public void writeDiskToFile(File file) throws FileWritingException {
        try {
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(Converter.intToByteArray(this.blockNum));
            writer.write(Converter.intToByteArray(this.blockSize));
            for (int i = 0; i < this.blockNum; i++)
                writer.write(this.logicDisk[i]);
        } catch (IOException e) {
            throw new FileWritingException("File writing error");
        }
    }


}
