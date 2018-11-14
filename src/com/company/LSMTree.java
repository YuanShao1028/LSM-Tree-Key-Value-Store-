package com.company;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.*;
public class LSMTree {
    final static float BF_BITS_PER_ENTRY = (float) 0.5;
    final static int TREE_DEPTH = 5;
    final static int FANOUT_SIZE = 4;
    final static int SINGLE_RUN = 1;

    private Buffer buffer;
    List<Level> levels;
    int fanOut;
    int depth;
    int bufferSize;
    boolean tiered;

    public LSMTree(int bufferSize, int depth, int fanOut, boolean tiered) {
        buffer = new Buffer(bufferSize);
        int maxRunSize = bufferSize;
        levels = new ArrayList<>(depth);
        this.fanOut = fanOut;
        this.depth = depth;
        this.bufferSize = bufferSize;
        this.tiered = tiered;

        for (int i = 0; i < depth; ++i) {
            if(tiered)
                levels.add(new Level(fanOut, maxRunSize, true));
            else
                levels.add(new Level(SINGLE_RUN, maxRunSize * fanOut, false));
            maxRunSize *= fanOut;
        }
    }

    public void setFanOut(int f) throws IOException {
        if (f == fanOut)
            return;

        boolean enableMerge = (f < fanOut);  //only decrease fan_out size enable following merge process
        fanOut = f;
        int maxRunSize = bufferSize;
        if (tiered) {
            for (int i = 0; i < depth; ++i) {
                levels.get(i).changeRun(fanOut, maxRunSize);
                maxRunSize *= fanOut;
            }

            if (enableMerge) {
                for (int i = depth - 1; i >= 0; --i) {
                    mergeDown(i);
                }
            }
        } else {
            for (int i = 0; i < depth; ++i) {
                System.out.println(maxRunSize * fanOut);
                levels.get(i).changeRun(SINGLE_RUN, maxRunSize * fanOut);
                maxRunSize *= fanOut;
            }

            if (enableMerge) {
                System.out.println("here");
                for (int i = 0; i < depth; ++i) {
                    if (levels.get(i).isFull()) {
                        System.out.println("hhhh");
                        Run empty = null;
                        leveledMergeDown(empty, i);
                    }
                }
            }

        }
    }

    public void leveledMergeDown(Run run, int start) throws IOException {
        // run is constructed by entries in buffer
        // or run could be empty (null), acting as a placeholder, which is used in merging caused by changing fan_out size
        int cur = start;
        int sumOfEntries = 0;
        MergeContext mergeContext = new MergeContext();
        if(run != null) {
            sumOfEntries += run.numOfEntries;
            run.startRead();
            mergeContext.add(run.mmapRead(), run.numOfEntries);
        }

        while (!levels.get(cur).deque.isEmpty()) {
                //levels.get(cur).deque.peekFirst().numOfEntries + sumOfEntries > levels.get(cur).max_run_size) {
            Run curRun = levels.get(cur).deque.peekFirst();
            curRun.startRead();
            mergeContext.add(curRun.mmapRead(), curRun.numOfEntries);
            sumOfEntries += curRun.numOfEntries;
            if(sumOfEntries <= curRun.maxSize) // merge in this level
                break;
            cur++;
        }

        if(cur >= levels.size())
            throw new IOException("run out of memory");
        //System.out.println("max_run_size :" + levels.get(cur).max_run_size);
        Run nextRun = new Run(levels.get(cur).max_run_size, BF_BITS_PER_ENTRY);
        nextRun.startmmapWrite();

        while (!mergeContext.done()) {
            int[] pair = mergeContext.next();
            int key = pair[0], val = pair[1];
            nextRun.mmap_put(key, val);
        }

        nextRun.endmmapWrite();
        if (run != null) {
            run.endRead();
            run.clear();
        }

        for (int i = start; i <= cur; ++i) {
            Level level = levels.get(i);
            if (!level.deque.isEmpty()) {
                Run tmpRun = level.deque.peekFirst();
                tmpRun.endRead();
                tmpRun.clear();
            }
            level.deque.clear();
        }
        levels.get(cur).deque.addFirst(nextRun);
    }

    public void mergeDown(int levelIndex) throws IOException {
        Level curLevel = levels.get(levelIndex);
        Level nextLevel;
        MergeContext mergeContext = new MergeContext();

        if (!curLevel.isFull()) {
            return;
        } else if (levelIndex >= levels.size() - 1) {
            throw new IOException("run out of memory");
        } else {
            nextLevel = levels.get(levelIndex + 1);
        }

        if(nextLevel.isFull()) {
            mergeDown(levelIndex + 1); // nextIndex = levelIndex + 1
            if(nextLevel.isFull())
                throw new IOException("merge failure");
        }
        //System.out.println("Merge-Level :" + levelIndex);
        //curLevel.print(levelIndex);
        for(Run run : curLevel.deque) {
            //System.out.print(run.numOfEntries + " ");
            run.startRead();
            mergeContext.add(run.mmapRead(), run.numOfEntries);
        }
        //System.out.println();

        nextLevel.deque.addFirst(new Run(nextLevel.max_run_size, BF_BITS_PER_ENTRY));
        Run nextLevelRun = nextLevel.deque.peekFirst();
        nextLevelRun.startmmapWrite();
        while(!mergeContext.done()) {
            int[] cur = mergeContext.next();
            int key = cur[0], val = cur[1];
            nextLevelRun.mmap_put(key, val);
        }
        nextLevelRun.endmmapWrite();
        //System.out.println("After Merge");
        //nextLevelRun.print();

        for (Run run : curLevel.deque) {
            run.endRead();
            run.clear();
        }

        curLevel.deque.clear();
    }

    public void put(int key, int val) throws IOException {
        if(buffer.put(key, val))
            return;
        if(tiered) {
            mergeDown(0);
            levels.get(0).deque.addFirst(new Run(levels.get(0).max_run_size, BF_BITS_PER_ENTRY));
            Run curRun = levels.get(0).deque.peekFirst();
            curRun.startmmapWrite();
            Set<Map.Entry<Integer, Integer>> set = buffer.entrySet();
            for (Map.Entry<Integer, Integer> entry : set) {
                curRun.mmap_put(entry.getKey(), entry.getValue());
            }
            curRun.endmmapWrite();
        } else {
            Run run = new Run(bufferSize, BF_BITS_PER_ENTRY);
            run.startmmapWrite();
            Set<Map.Entry<Integer, Integer>> set = buffer.entrySet();
            for (Map.Entry<Integer, Integer> entry : set) {
                run.mmap_put(entry.getKey(), entry.getValue());
            }
            run.endmmapWrite();
            leveledMergeDown(run, 0);
        }
        buffer.clear();
        buffer.put(key, val);
    }


    public boolean get(int key , boolean verbose) throws IOException {
        Integer bufferVal = buffer.get(key);
        if (bufferVal != null) {
            //System.out.println("key: " + key + " val: " + bufferVal);
            return true;
        }

        for(Level level : levels) {
            for (Run run : level.deque) {
                Integer val = verbose ? run.mmap_get(key, true) :run.get(key);
                if(verbose) {
                    System.out.println("run entry size / max_size:" + run.numOfEntries + " / " + run.maxSize);
                }
                if (val != null) {
                    //System.out.println("key: " + key + " val: " + val);
                    return true;
                }
            }
        }
        //System.out.println("key " + key + " does not exist");
        return false;
    }

    public void print(boolean verbose) throws IOException {
        System.out.println("Buffer");
        System.out.println("Buffer size is :" + buffer.size());
        if (verbose)
            buffer.print();
        System.out.println("Disk");
        for(int i = 0; i < levels.size(); ++i) {
            levels.get(i).print(i);
        }
    }

    public void print() throws IOException {
        print(false);
    }

    public void clear() {
        buffer.clear();
        for(Level level : levels) {
            level.clear();
        }
    }




}
