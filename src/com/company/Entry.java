package com.company;
//import javafx.util.Pair;
/**
 * Created by Yuanshao on 11/5/18.
 */
public class Entry implements  Comparable<Entry>{
    private int key;
    private int val;

    public Entry(int k, int v) {
        this.key = k;
        this.val = v;
    }

    public String toString() {
        return "(" + key + ", " + val + ")";
    }
    @Override
    public int compareTo(Entry e) {
        return key - e.key;
    }
}
