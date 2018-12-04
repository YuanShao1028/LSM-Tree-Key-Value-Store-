package com.company;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.util.*;
/**
 * Created by Yuanshao on 11/6/18.
 */
public class Level {
    boolean tiered; // true for tiered  false for leveled
    int max_runs; //for leveled method max_runs == 1
    long max_run_size;
    Deque<Run> deque = new LinkedList<>();

    public Level(int n, long s, boolean tiered) {
        this.max_runs = n;
        this.max_run_size = s;
        this.tiered = tiered;
    }

    public boolean isFull() {
        if (tiered)
            return max_runs <= deque.size();
        else {
            if(deque.isEmpty())
                System.out.println("empty");
            else{
                System.out.println(deque.peekFirst().numOfEntries  + " " + max_run_size);
            }
            return (!deque.isEmpty() && deque.peekFirst().numOfEntries > max_run_size);
        }
    }

    public void changeRun(int n, long s) {
        max_runs = n;
        max_run_size = tiered ? Math.max(max_run_size, s) : s; //tiered tree need to maintain max_run_size to avoid out of memory error
        for (Run run : deque)
            run.maxSize = max_run_size;
    }

    public void print(int level, boolean verbose) throws IOException {
        int count = 0;
        for(Run run : deque) {
            System.out.println("level:" + level + " run:" + count + " count:" + run.numOfEntries);
            if (verbose)
                run.print();
            count++;
            System.out.println();
        }
    }

    public int getNumOfEntries() {
        int sum = 0;
        for (Run run : deque) {
            sum += run.numOfEntries;
        }
        return sum;
    }

    public void print(int level) throws IOException {
        print(level, false);
    }

    public void clear() {
        for(Run run : deque)
            run.clear();
        deque.clear();
    }

}
