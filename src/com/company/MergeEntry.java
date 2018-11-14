package com.company;

import java.nio.MappedByteBuffer;

/**
 * Created by Yuanshao on 11/9/18.
 */
public class MergeEntry implements Comparable<MergeEntry> {
    int precedence;
    long numOfEntries;
    int currentIndex;
    int curKey;
    int curVal;
    MappedByteBuffer mappedByteBuffer = null;
    public MergeEntry(int precedence, long numOfEntries, MappedByteBuffer mappedByteBuffer) {
        this.precedence = precedence;
        this.numOfEntries = numOfEntries;
        this.mappedByteBuffer = mappedByteBuffer;
        if(numOfEntries > 0) {
            curKey = mappedByteBuffer.getInt();
            curVal = mappedByteBuffer.getInt();
            currentIndex = 0;
        }
    }
    boolean done() {
        return currentIndex == numOfEntries;
    }

    boolean next() {
        if(currentIndex >= numOfEntries - 1)
            return false;
        curKey = mappedByteBuffer.getInt();
        curVal = mappedByteBuffer.getInt();
        currentIndex++;
        return true;
    }

    @Override
    public int compareTo(MergeEntry o) {
        if(curKey == o.curKey)
            return precedence > o.precedence ? 1 : (precedence == o.precedence ? 0 : -1);
        else
            return curKey > o.curKey ? 1 : (curKey == o.curKey ? 0 : -1);
    }
    /*
    @Override
    public int compareTo(MergeEntry o) {
        if(curKey == o.curKey)
            return precedence - o.precedence;
        else
            return curKey - o.curKey;
    }
    */

    @Override
    public String toString() {
        return "order: " + precedence + " Index: " + currentIndex + " curKey: " + curKey + " curVal: " +curVal;
    }
}
