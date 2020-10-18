package model;

import java.util.HashMap;

public class Result {
    private HashMap<Integer, Integer> coreVMap;
    private HashMap<Integer, Integer> coreEMap;
    private double takenTime;
    private String algorithmName;
    private String datasetName;
    private String type;
    
    /**
     * constructor
     */
    public Result(HashMap<Integer, Integer> coreVMap, HashMap<Integer, Integer> coreEMap,double takenTime, String algorithmName) {
        this.coreVMap = coreVMap;
        this.coreEMap = coreEMap;
        this.takenTime = takenTime;
        this.algorithmName = algorithmName;
    }

    public Result(HashMap<Integer, Integer> coreVMap, HashMap<Integer, Integer> coreEMap, double takenTime, String algorithmName, String type) {
        this.coreVMap = coreVMap;
        this.coreEMap = coreEMap;
        this.takenTime = takenTime;
        this.algorithmName = algorithmName;
        this.type = type;
    }

    public Result(HashMap<Integer, Integer> coreVMap, HashMap<Integer, Integer> coreEMap,double takenTime, String algorithmName, String datasetName, String type) {
        this.coreVMap = coreVMap;
        this.coreEMap = coreEMap;
        this.takenTime = takenTime;
        this.algorithmName = algorithmName;
        this.datasetName = datasetName;
        this.type = type;
    }


    /**
     * Getter() and Setter()
     */

    public HashMap<Integer, Integer> getCoreVMap() {
        return coreVMap;
    }

    public void setCoreVMap(HashMap<Integer, Integer> coreVMap) {
        this.coreVMap = coreVMap;
    }

    public HashMap<Integer, Integer> getCoreEMap() {
        return coreEMap;
    }

    public void setCoreEMap(HashMap<Integer, Integer> coreEMap) {
        this.coreEMap = coreEMap;
    }

    public double getTakenTime() {
        return takenTime;
    }

    public void setTakenTime(double takenTime) {
        this.takenTime = takenTime;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
