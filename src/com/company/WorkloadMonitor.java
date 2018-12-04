package com.company;

/**
 * Created by Yuanshao on 11/14/18.
 */
public class WorkloadMonitor {
    private int get = 0;
    private int put = 0;
    private LSMTree tree;
    private int miniBatch;

    public WorkloadMonitor(LSMTree tree, int miniBatch) {
        this.tree = tree;
        this.miniBatch = miniBatch;
    }

    public void print() {
        System.out.println(tree.getNumOfEntries());
    }

    private double cost(int fanout) {
        int numOfEntries = tree.getNumOfEntries();
        int bufferSize = tree.bufferSize;
        double lookupCost = tree.tiered ? fanout * Utils.logn(fanout, numOfEntries / bufferSize)
                : Utils.logn(fanout, numOfEntries / bufferSize);
        double updateCost = tree.tiered ? Utils.logn(fanout, numOfEntries / bufferSize)
                : fanout * Utils.logn(fanout, numOfEntries / bufferSize);
        return get * lookupCost + put * updateCost;
    }

    public int getFanout() {
        int result = 2;
        double minCost = cost(2);
        int upper = (int) Utils.logn(2, tree.getNumOfEntries() / tree.bufferSize);
        for (int i = 3; i <= upper; ++i) {
            double c = cost(i);
            if(minCost > c) {
                minCost = c;
                result = i;
            }
        }
        return result;
    }

    public boolean changeFanout() {
        return totalOps() == miniBatch;
    }

    public void incGet() {
        get++;
    }

    public void incPut() {
        put++;
    }

    public int totalOps() {
        return get + put;
    }

    public double cost() {
        return 0;
    }

    public void clear() {
        get = 0;
        put = 0;
    }



}
