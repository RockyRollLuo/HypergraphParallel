package algorithm;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class IncrementalBatchEdges {
    private static final Logger LOGGER = Logger.getLogger(IncrementalBatchEdges.class);

    private Hypergraph hypergraph;
    private HashMap<ArrayList<Integer>, Integer> coreEMap;
    private HashMap<Integer, Integer> coreVMap;
    private final ArrayList<ArrayList<Integer>> e0List;  //the inserted edge

    /**
     * constructor
     */
    public IncrementalBatchEdges(Hypergraph hypergraph, HashMap<ArrayList<Integer>, Integer> coreEMap, HashMap<Integer, Integer> coreVMap, ArrayList<ArrayList<Integer>> e0List) {
        this.hypergraph = hypergraph;
        this.coreEMap = coreEMap;
        this.coreVMap = coreVMap;
        this.e0List = e0List;
    }

    public Result run() {
        long startTime = System.nanoTime();

        /*
        private properties of hypergraph
         */
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();

        /*
        temp data
         */
        HashMap<Integer, Integer> tempCoreVMap = new HashMap<>(coreVMap);
        HashMap<ArrayList<Integer>, Integer> tempCoreEMap = new HashMap<>(coreEMap);


        /*
         compute pre core and update graph
         1.update nodeList
         2.update edgeList
         3.update nodeToEdgesMap
         */
        for (ArrayList<Integer> e0 : e0List) {
            boolean newNodeFlag = false;
            int pre_core_e0 = Integer.MAX_VALUE;
            for (Integer v : e0) {
                if (nodeList.contains(v)) {
                    int core_v = tempCoreVMap.get(v);
                    pre_core_e0 = Math.min(core_v, pre_core_e0);

                    //3.update nodeToEdgesMap
                    ArrayList<ArrayList<Integer>> edgesContainV = nodeToEdgesMap.get(v);
                    edgesContainV.add(e0);
                    nodeToEdgesMap.put(v, edgesContainV);

                } else {
                    tempCoreVMap.put(v, 1);  // the core number of new node is 1
                    newNodeFlag = true;

                    nodeList.add(v); //1.update nodeList

                    //3.update nodeToEdgesMap
                    ArrayList<ArrayList<Integer>> edgesContainV = new ArrayList<>();
                    edgesContainV.add(e0);
                    nodeToEdgesMap.put(v, edgesContainV);
                }
            }
            if (newNodeFlag) {
                tempCoreEMap.put(e0, 1); // the core nubmer of new edge is 1
            } else {
                tempCoreEMap.put(e0, pre_core_e0);
            }
        }
        edgeList.addAll(e0List); //2.update edgeList

        /*
        traversal
        1.collect all initial nodes in e0List
        2.compute node support correlate with coreRoot
        3.shrink nodes cannot be (k+1)-core
        4.update core number of the nodes and edges in (k+1)-core
         */

        /*
        1.collect all initial nodes in e0List
         */
        HashMap<Integer, ArrayList<Integer>> coreRootMap = new HashMap<>();

        HashMap<Integer, Boolean> visitedNode = new HashMap<>();
        for (Integer v : nodeList) {
            visitedNode.put(v, false);
        }

        for (ArrayList<Integer> e0 : e0List) {
            int core_root = coreEMap.get(e0);

            for (Integer v : e0) {
                if (tempCoreVMap.get(v) == core_root) {

                    if (coreRootMap.containsKey(core_root)) {
                        ArrayList<Integer> vList = coreRootMap.get(core_root);
                        vList.add(v);
                        coreRootMap.put(core_root, vList);
                    } else {
                        ArrayList<Integer> vList = new ArrayList<>();
                        vList.add(v);
                        coreRootMap.put(core_root, vList);
                    }

                    visitedNode.put(v, true);//NEED!,the initialized value not only one
                }
            }
        }

        HashMap<Integer, Integer> supportMap = new HashMap<>();

        for (int core_root : coreRootMap.keySet()) {

            /**
             * 2.compute node support correlate with coreRoot
             */
            Stack<Integer> stack = new Stack<>();
            stack.addAll(coreRootMap.get(core_root));
            while (!stack.isEmpty()) {
                Integer v_stack = stack.pop();

                for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v_stack)) {
                    //compute support
                    int core_e_contain_v = tempCoreEMap.get(e_contain_v);
                    if (core_e_contain_v >= core_root) {
                        int support = supportMap.get(v_stack) == null ? 1 : (supportMap.get(v_stack) + 1);
                        supportMap.put(v_stack, support);
                    }
                    //traversal
                    if (core_e_contain_v == core_root) {
                        for (Integer u : e_contain_v) {
                            if (tempCoreVMap.get(u) == core_root && !visitedNode.get(u)) {
                                stack.push(u);
                                visitedNode.put(u, true);
                            }
                        }
                    }
                }


            }

            /*
            3.shrink nodes cannot be (k+1)-core
             */
            ArrayList<Integer> evictNodes = new ArrayList<>();

            //supportMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(supportMap, 1); //ascending sorted by value
            Stack<Integer> evictStack = new Stack<>();

            //initial stack
            for (Integer v : supportMap.keySet()) {
                if (supportMap.get(v) <= core_root) {
                    evictStack.push(v);
                    evictNodes.add(v);
                }
            }

            //iterate update
            while (!evictStack.isEmpty()) {
                Integer v = evictStack.pop();

                for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                    for (Integer u : e_contain_v) {
                        if (supportMap.containsKey(u) && !evictNodes.contains(u)) {
                            int support_u = supportMap.get(u) - 1;
                            supportMap.put(u, support_u);
                            if (support_u == core_root) {
                                evictStack.push(u);
                                evictNodes.add(u);
                            }
                        }
                    }
                }
            }

            /*
            4.update core number of the nodes and edges in (k+1)-core
             */
            for (Integer v : supportMap.keySet()) {
                if (!evictNodes.contains(v)) { //nodes not in evictNodes are increase core
                    tempCoreVMap.put(v, core_root + 1); //the core of each node in supportMap  is core_root

                    for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                        if (tempCoreEMap.get(e_contain_v) == core_root) { //only the core_root edges may be increase
                            int core_min = Integer.MAX_VALUE;
                            for (Integer u : e_contain_v) {
                                core_min = Math.min(core_min, tempCoreVMap.get(u)); //update the core of edge
                            }
                            tempCoreEMap.put(e_contain_v, core_min);
                        }
                    }
                }
            }

        }


        setCoreVMap(tempCoreVMap);
        setCoreEMap(tempCoreEMap);

        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.error(takenTime);

        return new Result(coreVMap, takenTime, this.getClass().getName(), "full");
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
