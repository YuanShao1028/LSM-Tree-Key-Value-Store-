package com.company;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.nio.*;

/**
 * Created by Yuanshao on 11/6/18.
 */
public class Run {
    final static String PREFIX = "lsm";
    final static String SUFFIX = ".tmp";
    final static String DIR = "/Users/Yuanshao/workspace/CS239/tmp";
    final static int PAGE_SIZE = 4096; // 4096 / 8 = 512 entries
    //final static int PAGE_SIZE = 32;
    final static int ENTRY_SIZE = 8;
    final static int NUM_OF_ENTRIES_PER_PAGE = PAGE_SIZE / ENTRY_SIZE;


    List<Integer> fencePointers;
    public String tmpFileStr;
    File tmpFile = null;
    FileOutputStream fos = null;
    DataOutputStream dos = null;
    RandomAccessFile raf = null;
    RandomAccessFile raf_write = null;
    MappedByteBuffer writeBuffer = null;
    BloomFilter bloomFilter;

    public int numOfEntries = 0;
    long maxSize;

    public long fileSize() {
        return ENTRY_SIZE * numOfEntries;
    }

    public Run(long maxSize, float bf_bits_per_entry) throws IOException {
        this.maxSize = maxSize;
        //System.out.println((int)(maxSize * bf_bits_per_entry));
        bloomFilter = new BloomFilter((int)(maxSize * bf_bits_per_entry));
        tmpFile = File.createTempFile(PREFIX, SUFFIX, new File(DIR));
        tmpFileStr = tmpFile.getAbsolutePath();
        fencePointers = new ArrayList<>();
    }

    public void startRead() throws IOException {
        raf = new RandomAccessFile(tmpFileStr, "r");
    }

    public void endRead() throws IOException {
        raf.close();
        raf = null;
    }


    int read(int offset, int length, byte[] buffer) throws IOException {
        //byte[] buffer = new byte[PAGE_SIZE];
        //RandomAccessFile raf = new RandomAccessFile(tmpFileStr, "r");
        raf.seek(offset);
        int size = raf.read(buffer);
        //raf.close();
        return size;
    }

    MappedByteBuffer mmapRead(int offset, int length) throws IOException {
        //RandomAccessFile raf = new RandomAccessFile(tmpFileStr, "r");
        FileChannel channel = raf.getChannel();
        MappedByteBuffer mappedByteBuffer = channel
                .map(FileChannel.MapMode.READ_ONLY, offset, length);
        return mappedByteBuffer;
    }

    MappedByteBuffer mmapRead() throws IOException {
        return mmapRead(0, numOfEntries * ENTRY_SIZE);
    }

    public void clear() {
        tmpFile.delete();
        tmpFileStr = null;
        fencePointers.clear();

    }

    public void startWrite() throws IOException {
        fos = new FileOutputStream(tmpFile);
        dos = new DataOutputStream(fos);
    }

    public void endWrite() throws IOException {
        dos.close();
        dos = null;
        fos = null;
    }

    public void startmmapWrite() throws IOException {
        raf_write = new RandomAccessFile(tmpFileStr, "rw");
        writeBuffer = raf_write
                .getChannel()
                .map(FileChannel.MapMode.READ_WRITE, 0, maxSize * ENTRY_SIZE);
    }

    public void endmmapWrite() throws IOException {
        raf_write.close();
        raf_write = null;
        writeBuffer = null;
    }

    void mmpWrite() throws  IOException {
         writeBuffer = raf_write
                .getChannel()
                .map(FileChannel.MapMode.READ_WRITE, 0, maxSize * ENTRY_SIZE);
    }

    public void printFencePoint() {
        for(int i = 0; i < fencePointers.size(); ++i) {
            if(i % 8 == 0)
                System.out.println();
            System.out.println(fencePointers.get(i));

        }
    }
    /*
    public void put(int key, int val) throws IOException {
        if(numOfEntries >= maxSize)
            throw new IOException("out of max capacity");

        bloomFilter.set(key);
        if(numOfEntries % NUM_OF_ENTRIES_PER_PAGE == 0) {
            fencePointers.add(key);
        }
        numOfEntries++;
        dos.writeInt(key);
        dos.writeInt(val);
    }
    */

    public void mmap_put(int key, int val) throws IOException {
        if(numOfEntries >= maxSize)
            throw new IOException("out of max capacity");

        bloomFilter.set(key);
        if(numOfEntries % NUM_OF_ENTRIES_PER_PAGE == 0) {
            fencePointers.add(key);
        }
        numOfEntries++;
        writeBuffer.putInt(key);
        writeBuffer.putInt(val);
    }


    public Integer get(int k) throws IOException {
        if(fencePointers.isEmpty() || k < fencePointers.get(0) || !bloomFilter.get(k))
            return null;
        int nextPageIndex = Utils.upperBound(fencePointers, k);
        int PageIndex = nextPageIndex - 1;
        if (PageIndex < 0) {
            //System.out.println("page index out of bound");
            throw new IOException("page index out of bound");
        }
        byte[] buf = new byte[PAGE_SIZE];
        startRead();
        int size = read(PageIndex * PAGE_SIZE, PAGE_SIZE, buf);
        endRead();
        for (int i = 0; i < size; i += 8) {
            byte[] ktmp = {buf[i], buf[i + 1], buf[i + 2], buf[i + 3]};
            byte[] vtmp = {buf[i + 4], buf[i + 5], buf[i + 6], buf[i + 7]};
            int key = Utils.byteArrayToInt(ktmp);
            int val = Utils.byteArrayToInt(vtmp);
            if(key == k)
                return val;
        }
        return null;
    }

    public Integer mmap_get(int k, boolean verbose) throws IOException {
        if(fencePointers.isEmpty() || k < fencePointers.get(0) || !bloomFilter.get(k))
            return null;
        int nextPageIndex = Utils.upperBound(fencePointers, k);
        int PageIndex = nextPageIndex - 1;
        System.out.println("PageIndex:" + PageIndex);
        if (PageIndex < 0) {
            //System.out.println("page index out of bound");
            throw new IOException("page index out of bound");
        }
        startRead();
        MappedByteBuffer mappedByteBuffer = mmapRead(PageIndex * PAGE_SIZE, PAGE_SIZE);
        for(int i = 0; i < NUM_OF_ENTRIES_PER_PAGE; ++i) {
            int key = mappedByteBuffer.getInt();
            int val = mappedByteBuffer.getInt();
            System.out.println(key + " " + val);
            if(key == k)
                return val;
        }
        endRead();
        return null;
    }

    public Integer mmap_get(int k) throws IOException {
        if(fencePointers.isEmpty() || k < fencePointers.get(0) || !bloomFilter.get(k))
            return null;
        int nextPageIndex = Utils.upperBound(fencePointers, k);
        int PageIndex = nextPageIndex - 1;
        if (PageIndex < 0) {
            //System.out.println("page index out of bound");
            throw new IOException("page index out of bound");
        }
        startRead();
        MappedByteBuffer mappedByteBuffer = mmapRead(PageIndex * PAGE_SIZE, PAGE_SIZE);
        for(int i = 0; i < NUM_OF_ENTRIES_PER_PAGE; ++i) {
            int key = mappedByteBuffer.getInt();
            int val = mappedByteBuffer.getInt();
            if(key == k)
                return val;
        }
        endRead();
        return null;
    }

    public void print() throws IOException {
        startRead();
        MappedByteBuffer mappedByteBuffer = mmapRead();
        for(int i = 0; i < numOfEntries; ++i) {
            int key = mappedByteBuffer.getInt();
            int val = mappedByteBuffer.getInt();
            System.out.print("(" + key + "," + val + ") ");
        }
        endRead();
    }

}
