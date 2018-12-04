package com.company;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.io.*;




public class Main {

    static void testWorkload(String workload, LSMTree tree, int miniBatch)throws IOException {
        WorkloadMonitor workloadMonitor = new WorkloadMonitor(tree, miniBatch);
        workloadMonitor.print();
        File file = new File(workload);
        Scanner scanner = new Scanner(file);
        long start = System.currentTimeMillis();
        long miniBatchStart = start;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] token = line.split(" ");
            if (token[0].equals("g")) {
                int key = Integer.valueOf(token[1]);
                tree.get(key, false);
                workloadMonitor.incGet();
            } else if (token[0].equals("p")) {
                int key = Integer.valueOf(token[1]);
                int val = Integer.valueOf(token[2]);
                tree.put(key, val);
                workloadMonitor.incPut();
                //System.out.println("PUT " + key + " " + val);
            }
            if (workloadMonitor.changeFanout()) {
                long miniBatchTime = System.currentTimeMillis() - miniBatchStart;
                System.out.println("miniBatchTimeMillis = " + miniBatchTime);
                miniBatchStart = System.currentTimeMillis();
                int fanout = workloadMonitor.getFanout();
                System.out.println("new fanout : " + fanout);
                tree.setFanOut(fanout);
                workloadMonitor.clear();
            }
        }
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        System.out.println("elapsedTimeMillis = " + elapsedTimeMillis);
        workloadMonitor.print();
    }


    static void testWorkload1(String workload, LSMTree tree, int miniBatch)throws IOException {
        WorkloadMonitor workloadMonitor = new WorkloadMonitor(tree, miniBatch);
        workloadMonitor.print();
        File file = new File(workload);
        Scanner scanner = new Scanner(file);
        long start = System.currentTimeMillis();
        long miniBatchStart = start;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] token = line.split(" ");
            if (token[0].equals("g")) {
                int key = Integer.valueOf(token[1]);
                tree.get(key, false);
                workloadMonitor.incGet();
            } else if (token[0].equals("p")) {
                int key = Integer.valueOf(token[1]);
                int val = Integer.valueOf(token[2]);
                tree.put(key, val);
                workloadMonitor.incPut();
                //System.out.println("PUT " + key + " " + val);
            }
            if (workloadMonitor.changeFanout()) {
                long miniBatchTime = System.currentTimeMillis() - miniBatchStart;
                System.out.println("miniBatchTimeMillis = " + miniBatchTime);
                miniBatchStart = System.currentTimeMillis();
                workloadMonitor.clear();
            }
        }
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        System.out.println("elapsedTimeMillis = " + elapsedTimeMillis);
        workloadMonitor.print();
    }


    public static void main(String[] args) throws IOException {

        int bufferSize = 10 * 4096 / 8;
        int miniBatch = 200000;
        String workload = "/Users/Yuanshao/workspace/CS239/cs265-lsm-tree/generator/2.txt";
        LSMTree tree = new LSMTree(bufferSize, 10, 4, true);
        testWorkload1(workload, tree, miniBatch);
        //tree.get(2055300197, false);
        //tree.print();
        tree.clear();




        /*
        int bufferSize = 100 * 4096 / 8;
        LSMTree tree = new LSMTree(4, 5, 4, true);
        //for (Level level : tree.levels) {
        //    System.out.println(level.max_runs + " " + level.max_run_size);
        //}
        //System.out.println(tree.levels.size());

        for(int i = 0; i < 200; ++i) {
            if(i % 2 == 1)
                tree.put(i, i);
        }
        for(int i = 0; i < 200; ++i) {
            if(i % 3 == 1)
                tree.put(i, -i);
        }
        for(int i = 10; i < 10; ++i) {
            tree.put(i, i + 1);
        }

        for(int i = 0; i < 200; ++i) {
            tree.put(i, -i + 1);
        }

        tree.print();

        tree.setFanOut(3);
        tree.print();

        for(int i = 0; i < 1020; ++i) {
            if (i % 3 == 1)
            tree.put(i, -i + 1);
        }

        tree.print();



        tree.clear();

        */
        /*


         */

        /*
        Run run1 = new Run(4, (float) 0.5);
        Run run2 = new Run(4, (float) 0.5);
        Run run3 = new Run(4, (float) 0.5);
        Run run4 = new Run(4, (float) 0.5);

        run1.startmmapWrite();
        run1.mmap_put(-1384681616,-1296236065);
        run1.mmap_put(1454827923,408940143);
        run1.mmap_put(1976143595,-1598430183);
        run1.mmap_put(2055300197,387009170);

        run1.endmmapWrite();
        System.out.println("file1 size : " + run1.fileSize());

        run2.startmmapWrite();
        //(-2120936234,783448592) (-2100101503,-1113306181) (194719064,1337007917) (1571909183,690668695)
        run2.mmap_put(-2120936234,783448592);
        run2.mmap_put(-2100101503,-1113306181);
        run2.mmap_put(194719064,1337007917);
        run2.mmap_put(1571909183,690668695);
        run2.endmmapWrite();
        System.out.println("file2 size : " + run2.fileSize());

        run3.startmmapWrite();
        run3.mmap_put(-1344552822,733025979);
        run3.mmap_put(353559988,1254737873);
        run3.mmap_put(604443636,1719671125);
        run3.mmap_put(975463691,1854762139);
        run3.endmmapWrite();
        System.out.println("file3 size : " + run3.fileSize());

        run4.startmmapWrite();
        run4.mmap_put(-2137162676,892407903);
        run4.mmap_put(463051032,555093390);
        run4.mmap_put(550289315,-1944024987);
        run4.mmap_put(1240860315,357233022);
        run4.endmmapWrite();
        System.out.println("file4 size : " + run3.fileSize());
        run1.print();
        System.out.println();
        run2.print();
        System.out.println();
        run3.print();
        System.out.println();
        run4.print();
        System.out.println();

        run1.startRead();
        run2.startRead();
        run3.startRead();
        run4.startRead();
        MappedByteBuffer m1 = run1.mmapRead();
        MappedByteBuffer m2 = run2.mmapRead();
        MappedByteBuffer m3 = run3.mmapRead();
        MappedByteBuffer m4 = run4.mmapRead();
        MergeContext mergeContext = new MergeContext();
        mergeContext.add(m1, run1.numOfEntries);
        mergeContext.add(m2, run2.numOfEntries);
        mergeContext.add(m3, run3.numOfEntries);
        mergeContext.add(m4, run4.numOfEntries);
        Run run5 = new Run(16, (float) 0.5);
        run5.startmmapWrite();


        while(!mergeContext.done()) {
            int[] cur = mergeContext.next();
            System.out.println(cur[0] + " " + cur[1]);
            run5.mmap_put(cur[0], cur[1]);
        }
        run5.endmmapWrite();
        System.out.println("num of entry :" + run5.numOfEntries);

        run5.print();

        run1.endRead();
        run2.endRead();
        run3.endRead();
        run4.endRead();

        //run.printFencePoint();
        run1.clear();
        run2.clear();
        run3.clear();
        run4.clear();
        //run1.printFencePoint();
        */





    }
}
