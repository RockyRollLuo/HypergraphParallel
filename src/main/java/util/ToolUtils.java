package util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ToolUtils {
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

    /**
     * sorted key in map by value
     * flag = 1 ascending order
     * flag = 0 descending order
     *
     * @param map
     * @param flag
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map, int flag) {
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

    public static String getDegreePosition(int degreePosition) {
        String degreePos="high";
        switch (degreePosition) {
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



    public static HashMap<Integer, Integer> getDegreeDistribution(HashMap<Integer, Integer> degreeMap) {
        HashMap<Integer, Integer> degreeNums = new HashMap<>();
        for (int value : degreeMap.values()) {
            int num=degreeNums.get(value)==null?1:degreeNums.get(value)+1;
            degreeNums.put(value, num);
        }
        return degreeNums;
    }

    public static ArrayList<Integer> getCoreDistribution(HashMap<Integer, Integer> coreVMap) {
        HashMap<Integer, Integer> coreNumMap = new HashMap<>();
        for (int value : coreVMap.values()) {
            int num=coreNumMap.get(value)==null?1:coreNumMap.get(value)+1;
            coreNumMap.put(value, num);
        }

        ArrayList<Integer> coreNumList = new ArrayList<>();
        int maxCore=Collections.max(coreNumMap.keySet());
        for (int i = 1; i <= maxCore; i++) {
            int num = coreNumMap.get(i) == null ? 0 : coreNumMap.get(i);
            coreNumList.add(num);
        }
        return coreNumList;
    }

    /**
     * get the node index randomly
     * @param degreePosition the node degree level
     * @return rand index
     */
    public static int getNodeIndexRand(int degreePosition,int length) {
        int index1 = length * 2 / 10;
        int index2 = length * 8 / 10;

        int randIndex = getRandomInt(index1, index2);
        if (degreePosition == 0) {//low
            randIndex = getRandomInt(index2, length);
        } else if (degreePosition == 2) {//high
            randIndex = getRandomInt(0, index1);
        }
        return randIndex;
    }

    public static HashMap<Integer,ArrayList<ArrayList<Integer>>> getCoreToEdgesMap(HashMap<ArrayList<Integer>, Integer> coreEMap) {
        HashMap<Integer, ArrayList<ArrayList<Integer>>> map = new HashMap<>();
        for (ArrayList<Integer> e : coreEMap.keySet()) {
            int core_e = coreEMap.get(e);
            if (map.containsKey(core_e)) {
                ArrayList<ArrayList<Integer>> edges = map.get(core_e);
                edges.add(e);
                map.put(core_e, edges);
            } else {
                ArrayList<ArrayList<Integer>> edges = new ArrayList<>();
                edges.add(e);
                map.put(core_e, edges);
            }
        }
        return map;
    }

    public static HashMap<Integer,ArrayList<ArrayList<Integer>>> getCardiToEdgesMap(ArrayList<ArrayList<Integer>> edgeList) {
        HashMap<Integer, ArrayList<ArrayList<Integer>>> map = new HashMap<>();
        for (ArrayList<Integer> e : edgeList) {
            int cardi = e.size();

            if (map.containsKey(cardi)) {
                ArrayList<ArrayList<Integer>> edges = map.get(cardi);
                edges.add(e);
                map.put(cardi, edges);
            } else {
                ArrayList<ArrayList<Integer>> edges = new ArrayList<>();
                edges.add(e);
                map.put(cardi, edges);
            }
        }
        return map;
    }
}

