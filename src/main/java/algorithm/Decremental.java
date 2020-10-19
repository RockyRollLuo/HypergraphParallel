package algorithm;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.GetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Decremental {
    private static final Logger LOGGER = Logger.getLogger(Decremental.class);

    private Hypergraph hypergraph;
    private HashMap<Integer, Integer> coreVMap;
    private HashMap<Integer, Integer> coreEMap;
    private final Integer e0Id;  //the deleted edge ID

    /**
     * constructor
     */
    public Decremental(Hypergraph hypergraph, HashMap<Integer, Integer> coreVMap, HashMap<Integer, Integer> coreEMap, Integer e0Id) {
        this.hypergraph = hypergraph;
        this.coreVMap = coreVMap;
        this.coreEMap = coreEMap;
        this.e0Id = e0Id;
    }



    public Result run() {
        long startTime = System.nanoTime();

        /*
        private properties of hypergraph
         */
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        HashMap<Integer, ArrayList<Integer>> edgeMap = hypergraph.getEdgeMap();
        HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();

        //temp data
        HashMap<Integer, Integer> tempCoreVMap = new HashMap<>(coreVMap);
        HashMap<Integer, Integer> tempCoreEMap = new HashMap<>(coreEMap);

        /*
         compute pre core and update graph
         1.update nodeList
         2.update nodeToEdgesMap
         */
        for (Integer v : edgeMap.get(e0Id)) {
            ArrayList<Integer> edgesContainV = nodeToEdgesMap.get(v);

            if (edgesContainV.size() == 1) { //isolated node
                nodeList.remove(v); //1.update nodeList
            }
            //2.update nodeToEdgesMap
            edgesContainV.remove(e0Id);
            nodeToEdgesMap.put(v, edgesContainV);
        }

        /*
        traversal
        1.compute node support correlate with e_0
        2.shrink nodes cannot be k-core
        3.update core number of edges in k-core
         */

        /*
        1.compute node support correlate with e_0
         */
        int core_root = tempCoreEMap.get(e0Id);
        HashMap<Integer, Boolean> visitedNode = new HashMap<>();
        for (Integer v : nodeList) {
            visitedNode.put(v, false);
        }

        Stack<Integer> stack = new Stack<>();
        for (Integer v : edgeMap.get(e0Id)) {
            if (tempCoreVMap.get(v) == core_root) {
                stack.push(v);
                visitedNode.put(v, true); //NEED!,the initialized value not only one
            }
        }

        HashMap<Integer, Integer> supportMap = new HashMap<>();
        while (!stack.isEmpty()) {
            Integer v = stack.pop();

            for (Integer eId : nodeToEdgesMap.get(v)) {
                //compute support
                int core_e_contain_v = tempCoreEMap.get(eId);
                if (core_e_contain_v >= core_root) {
                    int support = supportMap.get(v) == null ? 1 : (supportMap.get(v) + 1);
                    supportMap.put(v, support);
                }
                //travel
                if (core_e_contain_v == core_root) {
                    for (Integer u : edgeMap.get(eId)) {
                        if (!visitedNode.get(u) && tempCoreVMap.get(u) == core_root) {
                            stack.push(u);
                            visitedNode.put(u, true);
                        }
                    }
                }
            }
        }

        /*
        2.shrink nodes cannot be k-core
        TODO:change to decomposition-like, use a stack
         */
        ArrayList<Integer> reduceCoreNodes = new ArrayList<>();
        supportMap = (HashMap<Integer, Integer>) GetUtils.getSortMapByValue(supportMap, 1); //sorted by value ascending

        for (Map.Entry<Integer,Integer> entry : supportMap.entrySet()) {
            Integer v = entry.getKey();
            Integer support_v = entry.getValue();

            if (support_v < core_root) {
                tempCoreVMap.put(v, core_root - 1);
                reduceCoreNodes.add(v);
                for (Integer e_contain_v : nodeToEdgesMap.get(v)) {
                    for (Integer u : edgeMap.get(e_contain_v)) {
                        if (supportMap.containsKey(u) && !reduceCoreNodes.contains(u)) {
                            int support_u = supportMap.get(u) - 1;
                            supportMap.put(u, support_u);
                        }
                    }
                }
            }
        }

        /*
        3.update core number of edges in k-core
         */
        int countDynamicEdge = 0;
        for (Integer v : reduceCoreNodes) {
            for (Integer e_contain_v : nodeToEdgesMap.get(v)) {
                if (tempCoreEMap.get(e_contain_v) == core_root) { //only the core_root edges may be increase
                    countDynamicEdge++;
                    tempCoreEMap.put(e_contain_v, core_root - 1); //these edges contain node whose core decrease will decrease
                }
            }
        }
        tempCoreEMap.remove(e0Id);

        setCoreVMap(tempCoreVMap);
        setCoreEMap(tempCoreEMap);

        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.error(takenTime);
        System.out.println("countSearchNode:" + supportMap.size());
        System.out.println("countDynamicNode:" + reduceCoreNodes.size());
        System.out.println("countDynamicEdge:" + countDynamicEdge);

        return new Result(coreVMap, coreEMap, takenTime, "Decremental", "rest");
    }

    /**
     * getter and setter
     */
    public Hypergraph getHypergraph() {
        return hypergraph;
    }

    public void setHypergraph(Hypergraph hypergraph) {
        this.hypergraph = hypergraph;
    }

    public HashMap<Integer, Integer> getCoreEMap() {
        return coreEMap;
    }

    public void setCoreEMap(HashMap<Integer, Integer> coreEMap) {
        this.coreEMap = coreEMap;
    }

    public HashMap<Integer, Integer> getCoreVMap() {
        return coreVMap;
    }

    public void setCoreVMap(HashMap<Integer, Integer> coreVMap) {
        this.coreVMap = coreVMap;
    }
}
