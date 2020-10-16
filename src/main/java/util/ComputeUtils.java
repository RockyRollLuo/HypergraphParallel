package util;

import model.Hypergraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComputeUtils {

    public static ArrayList<ArrayList<Integer>> constructMultiDeletionEdges(Hypergraph hypergraph, ArrayList<ArrayList<Integer>> deletedEdges) {
        ArrayList<ArrayList<Integer>> multiEdge = new ArrayList<>();









        //update deletedEdges
        deletedEdges.removeAll(multiEdge);
        return multiEdge;
    }


    /**
     * compute coreEMap by coreVMap
     *
     * @param edgeMap edge list
     * @param coreVMap core of nodes
     * @return coreEMap
     */
    public static HashMap<Integer, Integer> computeCoreEMapByCoreVMap(HashMap<Integer, ArrayList<Integer>> edgeMap, HashMap<Integer, Integer> coreVMap) {
        HashMap<Integer, Integer> coreEMap = new HashMap<>();

        for (Map.Entry<Integer,ArrayList<Integer>> entry:edgeMap.entrySet()) {
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

        for (Map.Entry<Integer,ArrayList<Integer>> entry : nodeToEdgesMap.entrySet()) {
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
}

