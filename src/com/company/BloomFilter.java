package com.company;

/**
 * Created by Yuanshao on 11/5/18.
 */
import java.util.BitSet;

public class BloomFilter {
    private final BitSet bs;
    final int capacity;

    public BloomFilter(int slots) {
        bs = new BitSet(slots);
        capacity = slots;
    }

    private int hash32shift(int key) {

        key = ~key + (key << 15); // key = (key << 15) - key - 1;
        key = key ^ (key >>> 12);
        key = key + (key << 2);
        key = key ^ (key >>> 4);
        key = key * 2057; // key = (key + (key << 3)) + (key << 11);
        key = key ^ (key >>> 16);
        return (key >= 0 ? key : key + Integer.MAX_VALUE + 1) % capacity;
    }

    private int hash32shiftmult(int key) {

        int c2 = 0x27d4eb2d; // a prime or an odd constant
        key = (key ^ 61) ^ (key >>> 16);
        key = key + (key << 3);
        key = key ^ (key >>> 4);
        key = key * c2;
        key = key ^ (key >>> 15);
        //return (key > 0 ? key : key + Integer.MAX_VALUE + 1)  % capacity;
        return (key >= 0 ? key : key + Integer.MAX_VALUE + 1)  % capacity;
    }

    private int hash32mod(int key) {
        //System.out.println("key :"  + key);
        //int return_val = (key > 0 ? key : key + Integer.MAX_VALUE + 1)  % capacity;
        //System.out.println("return :"  + return_val);
        return (key >= 0 ? key : key + Integer.MAX_VALUE + 1)  % capacity;
    }

    public void set(int key) {
        bs.set(hash32shift(key));
        bs.set(hash32shiftmult(key));
        bs.set(hash32mod(key));
    }

    public boolean get(int key) {
        return bs.get(hash32shiftmult(key)) &&
                bs.get(hash32shift(key)) &&
                bs.get(hash32mod(key));
    }



}
