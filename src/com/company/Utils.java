package com.company;

/**
 * Created by Yuanshao on 11/8/18.
 */
import java.util.*;
public class Utils {
    final static int MAX_KEY = Integer.MAX_VALUE;
    final static int MIN_KEY = Integer.MIN_VALUE + 1;

    public static int upperBound(List<Integer> arr, int target) {
        int start = 0, end = arr.size() - 1;
        int result = -1;
        while (start <= end) {
            int mid = (start + end) / 2;
            if(arr.get(mid) <= target)
                start = mid + 1;
            else {
                result = mid;
                end = mid - 1;
            }
        }
        return result == -1 ? arr.size() : result;
    }

    public static int byteArrayToInt(byte[] b) {
        return  b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }


}
