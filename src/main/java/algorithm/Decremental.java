package algorithm;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.ToolUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Decremental {
    private static final Logger LOGGER = Logger.getLogger(Decremental.class);

    private Hypergraph hypergraph;
    private HashMap<Integer, Integer> coreEMap;
    private HashMap<Integer, Integer> coreVMap;
    private final Integer e0;  //the deleted edge

    /**
     * constructor
     */
    public Decremental(Hypergraph hypergraph, HashMap<Integer, Integer> coreEMap, HashMap<Integer, Integer> coreVMap, Integer e0) {
        this.hypergraph = hypergraph;
        this.coreEMap = coreEMap;
        this.coreVMap = coreVMap;
        this.e0 = eId;
    }

    public Result run() {
        long startTime = System.nanoTime();

        /*
        private properties of hypergraph
         */
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        HashMap<Integer,ArrayList<Integer>> edgeMap = hypergraph.getEdgeMap();
        HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();

        //temp data
        HashMap<Integer, Integer> tempCoreVMap = new HashMap<>(coreVMap);
        HashMap<Integer, Integer> tempCoreEMap = new HashMap<>(coreEMap);

        /*
         compute pre core and update graph
         1.update nodeList
         2.update edgeList
         3.update nodeToEdgesMap
         */
        for (Integer v : e0) {
            ArrayList<Integer> edgesContainV = nodeToEdgesMap.get(v);

            if (edgesContainV.size() == 1) { //isolated node
                nodeList.remove(v); //1.update nodeList
            }
            //3.update nodeToEdgesMap
            edgesContainV.remove(e0);
            nodeToEdgesMap.put(v, edgesContainV);
        }
        edgeList.remove(e0);  //2.update edgeList

        /*
        traversal
        1.compute node support correlate with e_0
        2.shrink nodes cannot be k-core
        3.update core number of edges in k-core
         */

        /*
        1.compute node support correlate with e_0
         */
        int core_root = tempCoreEMap.get(e0);
        HashMap<Integer, Boolean> visitedNode = new HashMap<>();
        for (Integer v : nodeList) {
            visitedNode.put(v, false);
        }

        Stack<Integer> stack = new Stack<>();
        for (Integer v : e0) {
            if (tempCoreVMap.get(v) == core_root) {
                stack.push(v);
                visitedNode.put(v, true); //NEED!,the initialized value not only one
            }
        }

        HashMap<Integer, Integer> supportMap = new HashMap<>();
        while (!stack.isEmpty()) {
            Integer v = stack.pop();

            for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                //compute support
                int core_e_contain_v = tempCoreEMap.get(e_contain_v);
                if (core_e_contain_v >= core_root) {
                    int support = supportMap.get(v) == null ? 1 : (supportMap.get(v) + 1);
                    supportMap.put(v, support);
                }
                //travel
                if (core_e_contain_v == core_root) {
                    for (Integer u : e_contain_v) {
                        if (!visitedNode.get(u) && tempCoreVMap.get(u) == core_root){
                            stack.push(u);
                            visitedNode.put(u, true);
                        }
                    }
                }
            }
        }

        /*
        2.shrink nodes cannot be k-core
         */

        ArrayList<Integer> reduceCoreNodes = new ArrayList<>();
        supportMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(supportMap, 1); //sorted by value
        for (Integer v : supportMap.keySet()) {
            if (supportMap.get(v) < core_root) {
                tempCoreVMap.put(v, core_root - 1);
                reduceCoreNodes.add(v);
                for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                    for (Integer u : e_contain_v) {
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
        int countEdge=0;
        for (Integer v : reduceCoreNodes) {
            for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                if (tempCoreEMap.get(e_contain_v) == core_root) { //only the core_root edges may be increase
                    countEdge++;
                    int core_min = Integer.MAX_VALUE;
                    for (Integer u : e_contain_v) {
                        core_min = Math.min(core_min, tempCoreVMap.get(u)); //update the core of edge
                    }
                    tempCoreEMap.put(e_contain_v, core_min);
                }
            }
        }
        tempCoreEMap.remove(e0);

        setCoreVMap(tempCoreVMap);
        setCoreEMap(tempCoreEMap);

        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.error(takenTime);
        System.out.println("countEdge:"+countEdge);
        System.out.println("countNode:"+reduceCoreNodes.size());

        return new Result(coreVMap, takenTime, "Decremental", "rest");
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

    public HashMap<ArrayList<Integer>, Integer> getCoreEMap() {
        return coreEMap;
    }

    public void setCoreEMap(HashMap<ArrayList<Integer>, Integer> coreEMap) {
        this.coreEMap = coreEMap;
    }

    public HashMap<Integer, Integer> getCoreVMap() {
        return coreVMap;
    }

    public void setCoreVMap(HashMap<Integer, Integer> coreVMap) {
        this.coreVMap = coreVMap;
    }
}