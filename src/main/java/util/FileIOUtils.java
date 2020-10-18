package util;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FileIOUtils {

    private static final Logger LOGGER = Logger.getLogger(FileIOUtils.class);

    /**
     * load an input graph in memory
     *
     * @param datasetName dataset name
     * @param delim       seperate sybolm
     * @return a graph
     * @throws IOException io
     */
    public static Hypergraph loadGraph(String datasetName, String delim, boolean constructStructure) throws IOException {
        long startTime = System.nanoTime();
        //Operate System
        String pathSeparator = "\\";
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            pathSeparator = "/";
        }
        String path = "datasets" + pathSeparator + datasetName;

        HashMap<Integer, ArrayList<Integer>> edgeMap = new HashMap<>();
        ArrayList<Integer> tempNodeList = new ArrayList<>();

        //read edges
        final BufferedReader br = new BufferedReader(new FileReader(path));

        int edgeIndex=0;
        while (true) {

            final String line = br.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) { //comment
                continue;
            }

            String[] tokens = line.split(delim);
            ArrayList<Integer> newEdge = new ArrayList<>();
            for (String token : tokens) {
                int node = Integer.parseInt(token);
                newEdge.add(node);
                tempNodeList.add(node);
            }

            edgeMap.put(edgeIndex,newEdge);
            edgeIndex++;
        }

        //remove duplicate nodes
        HashSet<Integer> nodeSet = new HashSet<>(tempNodeList);
        ArrayList<Integer> nodeList = new ArrayList<>(nodeSet);

        long endTime = System.nanoTime();
        LOGGER.info("TakenTime:" + (double) (endTime - startTime) / 1.0E9D);

        return new Hypergraph(nodeList, edgeMap, constructStructure);
    }


    /**
     * write the core number of nodes
     *
     * @param result coreMap
     * @throws IOException IO
     */
    public static void writeCoreNumber(Result result, int printResult) throws IOException {
        long startTime = System.nanoTime();

        HashMap<Integer, Integer> output = result.getCoreVMap();
        double takenTime = result.getTakenTime();
        String algorithmName = result.getAlgorithmName();
        String datasetName = result.getDatasetName();
        String type = result.getType();

        //Operate System
        String pathSeparator = "\\";
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            pathSeparator = "/";
        }
        String fileName = "corenumber" + pathSeparator + algorithmName + "_" + datasetName + "_" + type;

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        bw.write("# takenTime:" + takenTime + "ms");
        bw.newLine();

        //whether print the core nubmer
        if (printResult == 1) {
            for (Integer key : output.keySet()) {
                bw.write(key.toString() + " " + output.get(key));
                bw.newLine();
            }
        }
        bw.close();

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);
    }

    /**
     * read a core number file
     *
     * @param coreFile filename of core number
     * @return coreVMap
     * @throws IOException
     */
    public static HashMap<Integer, Integer> loadCoreFile(String coreFile) throws IOException {
        long startTime = System.nanoTime();
        //Operate System
        String pathSeparator = "\\";
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            pathSeparator = "/";
        }
        String path = "corenumber" + pathSeparator + coreFile;

        HashMap<Integer, Integer> coreVMap = new HashMap<>();

        //read edges
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("#")) {
                continue;
            }

            String[] tokens = line.split(" ");
            Integer node = Integer.parseInt(tokens[0]);
            int coreness = Integer.parseInt(tokens[1]);
            coreVMap.put(node, coreness);
        }

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);

        return coreVMap;
    }

    /**
     * write the nodeToEdgesMap to file
     *
     * @param nodeToEdgesMap data structure
     * @param datasetName    dataset name
     * @throws IOException io
     */
    public static void writeNodeToEdgesMap(HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap, String datasetName) throws IOException {
        long startTime = System.nanoTime();

        //Operate System
        String pathSeparator = "\\";
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            pathSeparator = "/";
        }
        String fileName = "datasets/nodeToEdgesMap/" + datasetName+".txt";

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        //write
        for (Map.Entry<Integer, ArrayList<Integer>> entry : nodeToEdgesMap.entrySet()) {

            bw.write(entry.getKey().toString());
            bw.newLine();

            bw.write(entry.getValue().toString().replace("[", "").replace("]", "").replace(" ", ""));
            bw.newLine();
        }
        bw.close();

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);
    }

    /**
     * read the file of nodeToEdgesMap
     *
     * @param datasetName dataset name
     * @return nodeToEdgesMap
     * @throws IOException io
     */
    public static HashMap<Integer, ArrayList<Integer>> loadNodeToEdgesMap(String datasetName) throws IOException {
        long startTime = System.nanoTime();

        String path = "datasets/nodeToEdgesMap/" + datasetName;

        HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap = new HashMap<>();

        //read
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String nodeIdLine = br.readLine();
            if (nodeIdLine == null) {
                break;
            }

            //node
            Integer node = Integer.parseInt(nodeIdLine);

            //read edges
            String edgesIdLine = br.readLine();
            String[] tokens = edgesIdLine.split(",");

            ArrayList<Integer> edgesIdList = new ArrayList<>();
            for (String token : tokens) {
                int nodeInEdge = Integer.parseInt(token);
                edgesIdList.add(nodeInEdge);
            }
            nodeToEdgesMap.put(node, edgesIdList);
        }

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);

        return nodeToEdgesMap;
    }
}
