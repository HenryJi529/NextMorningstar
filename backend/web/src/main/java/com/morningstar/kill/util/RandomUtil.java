package com.morningstar.kill.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomUtil {
    private static void swap(Integer[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * 获取打乱后元素的索引数组
     * @param N 需要打乱的元素个数
     * @return 打乱后元素的索引数组
     */
    public static Integer[] getShuffledIndexes(int N) {
        Integer[] shuffled = new Integer[N];
        for (int i = 0; i < N; i++) {
            shuffled[i] = i;
        }
        // 洗牌算法
        Random random = new Random();
        for (int i = N - 1; i > 0; i--) {
            swap(shuffled, i, random.nextInt(i + 1));
        }
        return shuffled;
    }

    /**
     * 知道总数的情况下获取样本的索引数组
     * @param M 样本数
     * @param N 总数
     * @return 样本的索引数组
     */
    public static Integer[] getSampleIndexes(int M, int N) {
        Random rand = new Random();
        Set<Integer> sample = new HashSet<>();
        while(sample.size() < M){
            sample.add(rand.nextInt(N));
        }
        return sample.toArray(new Integer[M]);
    }
}
