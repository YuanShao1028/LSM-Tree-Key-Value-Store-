package com.company;

/**
 * Created by Yuanshao on 11/9/18.
 */
import java.nio.MappedByteBuffer;
import java.util.*;

public class MergeContext {
    PriorityQueue<MergeEntry> priorityQueue;

    public MergeContext() {
        priorityQueue = new PriorityQueue<>();
    }

    void add(MappedByteBuffer mappedByteBuffer, long numOfEntries) {
        if(numOfEntries > 0) {
            int precedence = priorityQueue.size();
            MergeEntry mergeEntry = new MergeEntry(precedence, numOfEntries, mappedByteBuffer);
            priorityQueue.add(mergeEntry);
        }
    }

    int[] next() {  // int[]  size = 2  [0] key [1] val
        int[] result = new int[2];
        MergeEntry cur = priorityQueue.peek();
        int key = cur.curKey;
        int val = cur.curVal;
        MergeEntry nextMergeEntry = cur;
        while (!priorityQueue.isEmpty() && nextMergeEntry.curKey == key)  {
            priorityQueue.poll();
            if(nextMergeEntry.next()) {
                priorityQueue.add(nextMergeEntry);
            }
            nextMergeEntry = priorityQueue.peek();
        }
        result[0] = key;
        result[1] = val;
        return result;
    }

    boolean done() {
        return priorityQueue.isEmpty();
    }

}
