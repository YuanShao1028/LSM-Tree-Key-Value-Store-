package com.company;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.io.*;




public class Main {
    static void formatString(List<Long> times) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (Long t : times) {
            stringBuilder.append(t).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]");
        System.out.println(stringBuilder.toString());
    }

    static void testWorkload(String workload, LSMTree tree, int miniBatch)throws IOException {
        List<Long> times = new ArrayList<>();
        List<Long> fanouts = new ArrayList<>();
        fanouts.add((long)tree.fanOut);
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
                times.add(miniBatchTime);
                miniBatchStart = System.currentTimeMillis();
                int fanout = workloadMonitor.getFanout();
                System.out.println("new fanout : " + fanout);
                fanouts.add((long)fanout);
                tree.setFanOut(fanout);
                workloadMonitor.clear();
            }
        }
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        System.out.println("elapsedTimeMillis = " + elapsedTimeMillis);
        workloadMonitor.print();
        formatString(times);
        formatString(fanouts);

    }


    static void testWorkload1(String workload, LSMTree tree, int miniBatch)throws IOException {
        List<Long> times = new ArrayList<>();
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
                times.add(miniBatchTime);
                miniBatchStart = System.currentTimeMillis();
                workloadMonitor.clear();
            }
        }
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        System.out.println("elapsedTimeMillis = " + elapsedTimeMillis);
        workloadMonitor.print();
        formatString(times);
    }


    public static void main(String[] args) throws IOException {

        int bufferSize = 10 * 4096 / 8;
        int miniBatch = 200000;
        String workload = "/Users/Yuanshao/workspace/CS239/cs265-lsm-tree/generator/1.txt";
        LSMTree tree = new LSMTree(bufferSize, 15, 8, false);
        testWorkload1(workload, tree, miniBatch);
        //tree.get(2055300197, false);
        //tree.print();
        tree.clear();
        //workload 2
        //dynamic
        //[2076,1804,2055,1768,2022,2624,2473,2697,3393,2036,4199,2727,5123,5333,2737,5277,5066,2821,5475,4992,3064,6077,7296,6602,4262,5850,6976,7126,3925,6669,851,461,494,415,459,2268,464,323,336,350,1117,376,287,350,299,1621,400,336,285,342]
        //[8,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,4,3,3,3,3,4,3,3,3,3,5,6,7,7,7,7,8,8,8,8,8,8,9,9,9,9,9,9,9,9]
        //fanout 8
        //[2156,1963,2109,1715,1940,4017,4574,3595,5496,6973,13396,14415,13976,14185,18222,8087,7826,7840,8115,8978,8858,20953,18312,24152,18547,13611,28417,31341,32147,30773,2669,1041,530,479,538,8306,342,401,282,321,1783,322,306,269,548,1071,400,321,314,278]
        //fanout 4
        //[2196,1915,2076,1743,1999,4132,4802,3596,5202,6747,8434,5285,9643,9759,9589,9944,11628,11487,11173,11224,8818,10721,11992,12025,12089,9766,19368,20878,17626,24023,1107,579,644,562,672,3360,491,459,290,319,1383,416,336,294,412,976,358,313,308,350]
        //fanout 2
        //[2179,1927,2013,1723,1968,3017,3586,2614,4365,2609,5918,7125,7403,3917,7332,8049,9241,10474,9442,7430,7334,19073,16508,16673,20911,7582,15726,13923,14139,13867,1528,575,690,683,596,4010,323,408,417,335,1663,335,508,349,344,1886,337,384,505,369]
        //workload 1
        // dynamic tiered
        //[4891,9699,9444,10408,11477,10488,14624,15004,12684,11424,952,495,463,414,449,2469,548,307,329,289,31821,5329,7077,6487,6416,8245,9857,10272,11099,11387,818,398,469,408,497,2234,368,374,286,334,19827,13204,8031,6867,7204,10034,10319,10397,10289,10164]
        //[2,4,3,3,3,3,4,3,3,3,3,6,6,7,7,7,8,8,8,8,8,3,3,3,3,3,3,3,3,3,3,8,8,8,8,8,9,9,9,9,9,3,3,3,3,3,3,3,3,3]
        //leveled
        //[2818,5887,5830,8048,5153,4839,5342,3821,6623,6442,678,570,520,323,333,1117,418,362,452,342,4806,7028,7357,7044,5347,8289,9758,10042,9832,9763,613,581,595,525,316,877,574,368,391,369,6782,8214,6454,7920,8597,8139,8623,8332,8545,6143]
        //[2,4,4,4,4,4,4,5,5,5,5,3,3,3,3,3,3,3,3,3,3,8,8,8,8,8,8,8,8,8,8,3,3,3,3,3,3,3,3,3,3,8,8,8,8,8,8,8,8,8,8]

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
