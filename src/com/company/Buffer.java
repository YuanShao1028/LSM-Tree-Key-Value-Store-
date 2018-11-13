package com.company;
import java.util.*;
import javafx.util.Pair;
/**
 * Created by Yuanshao on 11/5/18.
 */
public class Buffer {
    private int maxSize;
    TreeMap<Integer, Integer> buffer;

    public Buffer(int capacity) {
        buffer = new TreeMap<>();
        maxSize = capacity;
    }

    public Set<Map.Entry<Integer, Integer>> entrySet() {
        return buffer.entrySet();
    }


    public Integer get(int key) {
        return buffer.get(key);
    }

    public boolean put(int key, int val) {
        //boolean found;
        if(buffer.size() == maxSize)
            return false;
        else {
            buffer.put(key, val);
        }
        return true;
    }

    public void clear() {
        buffer.clear();
    }

    public void print() {
        for(Map.Entry<Integer, Integer> e : buffer.entrySet()) {
            System.out.print("(" + e.getKey() + " " + e.getValue() + ") ");
        }
        System.out.println();
    }
}
