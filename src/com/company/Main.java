package com.company;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
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



        //for(int i = 0; i < 200; ++i) {
        //    tree.get(i);
        //}

        tree.clear();





        //for(int i = 0; i < 5; ++i) {
        //    tree.get(i);
        //}





        /*
        Run run1 = new Run(128, (float) 0.5);
        System.out.println(run1.tmpFileStr);
        Run run2 = new Run(128, (float) 0.5);
        System.out.println(run2.tmpFileStr);
        Run run3 = new Run(128, (float) 0.5);
        System.out.println(run3.tmpFileStr);

        run1.startmmapWrite();
        for(int i = 0; i < 256; ++i)
            if(i % 2 == 1)
                run1.mmap_put(i, i + 1);
        run1.endmmapWrite();
        System.out.println("file1 size : " + run1.fileSize());

        run2.startmmapWrite();
        for(int i = 0; i < 256; ++i)
            if(i % 2 == 1)
                run2.mmap_put(i, i + 2);
        run2.endmmapWrite();
        System.out.println("file2 size : " + run2.fileSize());

        run3.startmmapWrite();
        for(int i = 0; i < 256; ++i)
            if(i % 2 == 0)
                run3.mmap_put(i, i + 3);
        run3.endmmapWrite();
        System.out.println("file3 size : " + run3.fileSize());


        run1.startRead();
        run2.startRead();
        run3.startRead();
        MappedByteBuffer m1 = run1.mmapRead();
        MappedByteBuffer m2 = run2.mmapRead();
        MappedByteBuffer m3 = run3.mmapRead();
        MergeContext mergeContext = new MergeContext();
        mergeContext.add(m1, run1.numOfEntries);
        mergeContext.add(m2, run2.numOfEntries);
        mergeContext.add(m3, run3.numOfEntries);
        Run run4 = new Run(128 * 3, (float) 0.5);
        run4.startmmapWrite();

        while(!mergeContext.done()) {
            int[] cur = mergeContext.next();
            System.out.println(cur[0] + " " + cur[1]);
            run4.mmap_put(cur[0], cur[1]);
        }
        run4.endmmapWrite();
        System.out.println("num of entry :" + run4.numOfEntries);
        run4.startRead();
        MappedByteBuffer mappedByteBuffer = run4.mmapRead();
        for (int i = 0; i < run4.numOfEntries; ++i) {
            System.out.println(mappedByteBuffer.getInt() + " " + mappedByteBuffer.getInt());
        }
        run4.endRead();

        run1.endRead();
        run2.endRead();
        run3.endRead();

        //run.printFencePoint();
        run1.clear();
        run2.clear();
        run3.clear();
        run4.clear();
        //run1.printFencePoint();
        */



    }
}
