package com.morningstar.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomUtil {
    private static final Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

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

    /**
     * 随机获取一个固定长度的中文字符串
     */
    public static String getChineseString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int codePoint = (int) (Math.random() * (0x9FA5 - 0x4E00) + 0x4E00); // 常用汉字的Unicode范围U+4E00～U+9FA5)
            sb.append(Character.toChars(codePoint));
        }
        return sb.toString();
    }

    /**
     * 随机获取一个固定长度的英文字符串
     */
    public static String getEnglishString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }
}
