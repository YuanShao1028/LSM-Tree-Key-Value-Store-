package com.company;

/**
 * Created by Yuanshao on 11/14/18.
 */
public class WorkloadMonitor {
    private int successfulGet = 0;
    private int failedGet = 0;
    private int put = 0;
    private double sgRatio;
    private double fgRatio;
    private double pRatio;

    public WorkloadMonitor(double sgRatio, double fgRatio, double pRatio) {
        this.sgRatio = sgRatio;
        this.fgRatio = fgRatio;
        this.pRatio = pRatio;
    }

    public void incSuccessfulGet() {
        successfulGet++;
    }

    public void incFailedGet() {
        failedGet++;
    }

    public void incPut() {
        put++;
    }

    public int totalOps() {
        return successfulGet + failedGet + put;
    }

    public double cost() {
        return successfulGet * sgRatio + failedGet * fgRatio + put * pRatio;
    }

    public void clear() {
        successfulGet = 0;
        failedGet = 0;
        put = 0;
    }



}
