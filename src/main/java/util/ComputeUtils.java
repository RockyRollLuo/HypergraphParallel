package util;

import model.Hypergraph;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ComputeUtils {

    /**
     * construct a multi edges that can be delete in one iteration
     * @param hypergraph graph
     * @param coreEMap core of edge
     * @param deltaEdges a batch of edges
     * @return multi edges
     */
    public static ArrayList<Integer> constructMultiDeletionEdges(Hypergraph hypergraph, HashMap<Integer, Integer> coreEMap, ArrayList<Integer> deltaEdges) {
        ArrayList<Integer> multiEdge = new ArrayList<>();
        ArrayList<Integer> coreList = new ArrayList<>();

        LinkedList<Integer> deltaEdgesList = new LinkedList<>(deltaEdges);
        multiEdge.add(deltaEdgesList.poll());

        HashMap<Integer, ArrayList<Integer>> edgeMap = hypergraph.getEdgeMap();


        for (Integer e_delta : deltaEdgesList) {
            int core_e_delta = coreEMap.get(e_delta);
            ArrayList<Integer> nodes_e_delta = edgeMap.get(e_delta);

            for (Integer e_multi : multiEdge) {
                int core_e_multi = coreEMap.get(e_multi);
                ArrayList<Integer> nodes_e_multi = edgeMap.get(e_multi);

                if (Collections.disjoint(nodes_e_delta, nodes_e_multi)) {
                    multiEdge.add(e_delta);
                }else if (1) {//todo

                } else if (core_e_delta != core_e_multi) {


                }



            }

        }

        //update deletedEdges
        deltaEdges.removeAll(multiEdge);
        return multiEdge;
    }


    /**
     * compute coreEMap by coreVMap
     *
     * @param edgeMap  edge list
     * @param coreVMap core of nodes
     * @return coreEMap
     */
    public static HashMap<Integer, Integer> computeCoreEMapByCoreVMap(HashMap<Integer, ArrayList<Integer>> edgeMap, HashMap<Integer, Integer> coreVMap) {
        HashMap<Integer, Integer> coreEMap = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : edgeMap.entrySet()) {
            Integer eId = entry.getKey();
            ArrayList<Integer> edge = entry.getValue();

            int core_e = Integer.MAX_VALUE;
            for (Integer v : edge) {
                core_e = Math.min(coreVMap.get(v), core_e);
            }
            coreEMap.put(eId, core_e);
        }
        return coreEMap;
    }

    /**
     * compute the support value of each node
     *
     * @param nodeToEdgesMap nodeToEdgesMap
     * @param coreEMap       core number of edge
     * @param coreVMap       core number of node
     * @return supportMap
     */
    public HashMap<Integer, Integer> computeSupportMap(HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap, HashMap<Integer, Integer> coreEMap, HashMap<Integer, Integer> coreVMap) {

        HashMap<Integer, Integer> supportMap = new HashMap<>();
        long startTime = System.nanoTime();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : nodeToEdgesMap.entrySet()) {
            Integer node = entry.getKey();
            ArrayList<Integer> edgeList = entry.getValue();

            int core_v = coreVMap.get(node);
            int support = 0;

            for (Integer eId : edgeList) {
                int core_e = coreEMap.get(eId);
                if (core_e >= core_v) {
                    support++;
                }
            }
            supportMap.put(node, support);
        }

        long endTime = System.nanoTime();
        System.out.println((double) (endTime - startTime) / 1.0E9D);
        return supportMap;
    }

    /**
     * compute a < k.num > map from a < k,v> map
     *
     * @param map < k,v>
     * @return < k,num>
     */
    public static HashMap<Integer, Integer> computeDistribution(HashMap<Integer, Integer> map) {
        HashMap<Integer, Integer> numMap = new HashMap<>();
        for (int value : map.values()) {
            int num = numMap.get(value) == null ? 1 : numMap.get(value) + 1;
            numMap.put(value, num);
        }
        return numMap;
    }

    /**
     * compute a accumulative proportion list from a < k,v> map
     *
     * @param map < k, v>
     * @return proportion double list
     */
    public static ArrayList<Double> computeAccumulativeProportion(HashMap<Integer, Integer> map) {
        HashMap<Integer, Integer> numMap = computeDistribution(map);

        int allSum = 0;
        for (Integer key : numMap.keySet()) {
            allSum += numMap.get(key);
        }

        int curSum = 0;
        HashMap<Integer, Double> proprotionMap = new HashMap<>();
        for (Integer key : numMap.keySet()) {
            curSum += numMap.get(key);
            double precent = 1.0 * curSum / allSum;
            proprotionMap.put(key, (new BigDecimal(precent * 100).setScale(3, RoundingMode.FLOOR)).doubleValue());
        }

        return (ArrayList<Double>) proprotionMap.values();
    }

    /**
     * choose dynamic edges by cardinality distribution
     *
     * @param edgeMap     edgeMap
     * @param dynamicNums the number of dynamic edges
     * @param cardi       cardinality distribution
     * @return id list of dynamic edges
     */
    public static ArrayList<Integer> computeDynamicEdges(HashMap<Integer, ArrayList<Integer>> edgeMap, int dynamicNums, int cardi) {
        ArrayList<Integer> dynamicEdges = new ArrayList<>();

        HashMap<Integer, Integer> edgeCardiMap = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : edgeMap.entrySet()) {
            Integer eId = entry.getKey();
            ArrayList<Integer> edgeNodes = entry.getValue();

            edgeCardiMap.put(eId, edgeNodes.size());
        }
        edgeCardiMap = (HashMap<Integer, Integer>) GetUtils.getSortMapByValue(edgeCardiMap, 0);

        ArrayList<Integer> sortedNodesList = new ArrayList<>(edgeCardiMap.keySet());

        int length = sortedNodesList.size();
        int index1 = length * 3 / 10;
        int index2 = length * 7 / 10;

        ArrayList<Integer> choosenSubList = null;

        if (cardi == 0) {//low
            choosenSubList = new ArrayList<>(sortedNodesList.subList(0, index1));
        } else if (cardi == 2) {//high
            choosenSubList = new ArrayList<>(sortedNodesList.subList(index2, length));
        }
        Collections.shuffle(choosenSubList);
        dynamicEdges = new ArrayList<>(choosenSubList.subList(0, dynamicNums));

        return dynamicEdges;
    }


}

