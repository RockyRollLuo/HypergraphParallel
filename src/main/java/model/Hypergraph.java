package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Hypergraph {

    private ArrayList<Integer> nodeList;
    private HashMap<Integer, ArrayList<Integer>> edgeMap; //<eId,edge>

    private HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap;

    //constructor
    public Hypergraph(ArrayList<Integer> nodeList, HashMap<Integer, ArrayList<Integer>> edgeMap, boolean constructStructure) {
        this.nodeList = nodeList;
        this.edgeMap = edgeMap;

        if (constructStructure) {
            constructNodeToEdgesMap();
        }
    }

    /**
     * construct nodeToEdgesMap
     */
    private void constructNodeToEdgesMap() {
        long startTime = System.nanoTime();

        HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : edgeMap.entrySet()) {
            Integer eId = entry.getKey(); //edge id
            ArrayList<Integer> edge = entry.getValue(); //nodes in edge

            for (Integer node : edge) {
                ArrayList<Integer> edgeList = nodeToEdgesMap.get(node) == null ? new ArrayList<>() : nodeToEdgesMap.get(node);
                edgeList.add(eId);
                nodeToEdgesMap.put(node, edgeList);
            }
        }

        this.nodeToEdgesMap = nodeToEdgesMap;
        long endTime = System.nanoTime();
        System.out.println("TakenTime:" + (double) (endTime - startTime) / 1.0E9D);
    }

    /**
     * compute degree of each node
     *
     * @return degreeMap
     */
    public HashMap<Integer, Integer> getDegreeMap() {
        long startTime = System.nanoTime();

        HashMap<Integer, Integer> degreeMap = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : nodeToEdgesMap.entrySet()) {
            Integer node = entry.getKey();
            int degree = entry.getValue().size();

            degreeMap.put(node, degree);
        }

        long endTime = System.nanoTime();
        System.out.println("TakenTime:" + (double) (endTime - startTime) / 1.0E9D);
        return degreeMap;
    }

    /**
     * get the node of the minimum degree by degreeMap
     *
     * @return node ID
     */
    public Integer getMinDegreeNode(HashMap<Integer, Integer> degreeMap) {
        int minDegree = Integer.MAX_VALUE;
        Integer tempNode = null;

        for (Integer node : degreeMap.keySet()) {
            int degree = degreeMap.get(node);
            if (degree < minDegree) {
                minDegree = degree;
                tempNode = node;
            }
        }
        return tempNode;
    }


    /**
     * get node neighbors
     *
     * @return neighborNodesMap
     */
    public HashMap<Integer, ArrayList<Integer>> getNeighborsMap() {
        long startTime = System.nanoTime();
        HashMap<Integer, ArrayList<Integer>> neighborsMap = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : nodeToEdgesMap.entrySet()) {
            Integer node = entry.getKey();
            ArrayList<Integer> edgeList = entry.getValue();

            ArrayList<Integer> tempNeighborList = new ArrayList<>();

            for (Integer eId : edgeList) {
                tempNeighborList.addAll(edgeMap.get(eId));
            }

            HashSet<Integer> tempSet = new HashSet<>(tempNeighborList);
            ArrayList<Integer> neighborList = new ArrayList<>(tempSet);
            neighborList.remove(node);

            neighborsMap.put(node, neighborList);
        }

        long endTime = System.nanoTime();
        System.out.println("TakenTime:" + (double) (endTime - startTime) / 1.0E9D);
        return neighborsMap;
    }

    /*
    getter and setter
     */

    public ArrayList<Integer> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<Integer> nodeList) {
        this.nodeList = nodeList;
    }

    public HashMap<Integer, ArrayList<Integer>> getEdgeMap() {
        return edgeMap;
    }

    public void setEdgeMap(HashMap<Integer, ArrayList<Integer>> edgeMap) {
        this.edgeMap = edgeMap;
    }

    public HashMap<Integer, ArrayList<Integer>> getNodeToEdgesMap() {
        return nodeToEdgesMap;
    }

    public void setNodeToEdgesMap(HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap) {
        this.nodeToEdgesMap = nodeToEdgesMap;
    }
}
