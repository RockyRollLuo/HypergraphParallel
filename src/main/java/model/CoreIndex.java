package model;

import java.util.ArrayList;
import java.util.HashMap;

public class CoreIndex {

    private HashMap<Integer, ArrayList<Integer>> indexMap; //core,edgeId


    /*
    getter and setter
     */
    public HashMap<Integer, ArrayList<Integer>> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(HashMap<Integer, ArrayList<Integer>> indexMap) {
        this.indexMap = indexMap;
    }

    /*
    constructor
    */
    public CoreIndex() {
    }
    public CoreIndex(HashMap<Integer, ArrayList<Integer>> indexMap) {
        this.indexMap = indexMap;
    }
}
