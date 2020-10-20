package util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GetUtils {
    private static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    public static int getRandomInt(int max) {
        return getRandom().nextInt(max);
    }

    public static int getRandomInt(int min, int max) {
        return getRandom().nextInt(max - min + 1) + min;
    }

    public static <E> E getRandomElement(List<E> list) {
        return list.get(getRandomInt(list.size()));
    }

    /**
     * random choose k form N
     * Reservoir sampling
     *
     * @param list
     * @param k
     * @return
     */
    public static <E> E getRandomKFormN(List<E> list, int k) {
        int N = list.size();
        List<E> kList = null;

        for (int i = 0; i < k; i++) {
            kList.add(list.get(i));
        }
        for (int i = k; i < N; i++) {
            int r = getRandomInt(k + 1);
            if (r < k) {
                kList.add(r, list.get(i));
                kList.remove(r + 1);
            }
        }
        return (E) kList;
    }

    /**
     * select k number for [1, N]
     * @param N
     * @param k
     * @return
     */
    public static ArrayList<Integer> getRandomIndexsList(int N, int k) {
        ArrayList<Integer> NList = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            NList.add(i);
        }
        Collections.shuffle(NList);
        return new ArrayList<Integer>(NList.subList(0, k));
    }

    public static String getDelim(int delimType) {
        String delim="\t";
        switch (delimType) {
            case 0:
                delim="\t";
                break;
            case 1:
                delim=" ";
                break;
            case 2:
                delim = ",";
                break;
            default:
                break;
        }
        return delim;
    }

    public static String getAlgorithmType(int algorithmType) {
        String algorithm="decomposition";
        switch (algorithmType) {
            case 0:
                algorithm="decomposition";
                break;
            case 1:
                algorithm="incremental";
                break;
            case 2:
                algorithm = "decremental";
                break;
            case 3:
                algorithm = "degreeDistribution";
                break;
            default:
                break;
        }
        return algorithm;
    }

    public static String getCardiDistribution(int cardi) {
        String degreePos="avg";
        switch (cardi) {
            case 0:
                degreePos="low";
                break;
            case 1:
                degreePos="avg";
                break;
            case 2:
                degreePos = "high";
                break;
            default:
                break;
        }
        return degreePos;
    }

    /**
     * sorted key in map by value
     * flag = 1 ascending order
     * flag = 0 descending order
     *
     * @param map
     * @param flag
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> getSortMapByValue(Map<K, V> map, int flag) {
        Map<K, V> sortMap = new LinkedHashMap<>();
        if (flag == 1) {
            map.entrySet().stream()
                    .sorted((o1, o2) -> o1.getValue().compareTo(o2.getValue()))
                    .forEach(entry -> sortMap.put(entry.getKey(), entry.getValue()));
        } else {
            map.entrySet().stream()
                    .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                    .forEach(entry -> sortMap.put(entry.getKey(), entry.getValue()));
        }
        return sortMap;
    }


}

