package util;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

        LOGGER.info("Start loading graph: " + path);

        ArrayList<ArrayList<Integer>> edgeList = new ArrayList<>();
        ArrayList<Integer> tempNodeList = new ArrayList<>();

        //read edges
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) { //comment
                continue;
            }

            String[] tokens = line.split(delim);
//            if (tokens.length == 1) {
//                continue;
//            }
            ArrayList<Integer> newEdge = new ArrayList<>();
            for (String token : tokens) {
                int node = Integer.parseInt(token);
                newEdge.add(node);
                tempNodeList.add(node);
            }

            edgeList.add(newEdge);
        }


        HashSet<Integer> nodeSet = new HashSet<>(tempNodeList);
        ArrayList<Integer> nodeList = new ArrayList<>(nodeSet);

        long endTime = System.nanoTime();
        LOGGER.info("TakenTime:" + (double) (endTime - startTime) / 1.0E9D);

        return new Hypergraph(nodeList, edgeList,constructStructure);
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

        LOGGER.info("Start loading core_number_file: " + path);

        HashMap<Integer, Integer> coreVMap = new HashMap<>();

        //read edges
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) {
                continue;
            }

            String[] tokens = line.split("\t");
            Integer node = Integer.parseInt(tokens[0]);
            int coreness = Integer.parseInt(tokens[1]);
            coreVMap.put(node, coreness);
        }

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);

        return coreVMap;
    }

    /**
     * write the core number of nodes
     *
     * @param result coreMap
     * @throws IOException
     */
    public static void writeCoreNumber(Result result, int printResult) throws IOException {
        long startTime = System.nanoTime();

        HashMap<Integer, Integer> output = result.getOutput();
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

        bw.write("# takenTime:" + takenTime + "us");
        bw.newLine();

        //whether print the core nubmer
        if (printResult == 1) {
            for (Integer key : output.keySet()) {
                bw.write(key.toString() + "\t" + output.get(key));
                bw.newLine();
            }
        }
        bw.close();

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);
    }

    /**
     *  write the nodeToEdgesMap to file
     * @param nodeToEdgesMap data structure
     * @param datasetName dataset name
     * @throws IOException io
     */
    public static void writeNodeToEdgesMap(HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap, String datasetName) throws IOException {
        long startTime = System.nanoTime();
        LOGGER.info("Start writing nodeToEdgesMap... ");

        //Operate System
        String pathSeparator = "\\";
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            pathSeparator = "/";
        }
        String fileName = "datasets/nodeToEdgesMap/" + datasetName;

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        bw.write("# dataset: " + datasetName );
        bw.newLine();

        //print
        for (Integer node : nodeToEdgesMap.keySet()) {
            ArrayList<ArrayList<Integer>> edges = nodeToEdgesMap.get(node);


            bw.write(node.toString() + "\t" + edges.size());
            bw.newLine();

            for (ArrayList<Integer> e : edges) {
                bw.write(e.toString().replace("[","").replace("]","").replace(" ",""));
                bw.newLine();
            }
        }
        bw.close();

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);
    }

    /**
     *  read the file of nodeToEdgesMap
     * @param datasetName dataset name
     * @return nodeToEdgesMap
     * @throws IOException io
     */
    public static HashMap<Integer, ArrayList<ArrayList<Integer>>> loadNodeToEdgesMap(String datasetName) throws IOException {
        long startTime = System.nanoTime();

        String path = "datasets/nodeToEdgesMap/" + datasetName;

        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = new HashMap<>();

        //read
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) {
                continue;
            }

            //node and edge size
            String[] nodeAndSize = line.split("\t");
            Integer node = Integer.parseInt(nodeAndSize[0]);
            int edgeSize = Integer.parseInt(nodeAndSize[1]);


            //read edges
            ArrayList<ArrayList<Integer>> edges = new ArrayList<>();
            for (int i = 0; i < edgeSize; i++) {
                String edgeLine = br.readLine();

                ArrayList<Integer> e = new ArrayList<>();
                String[] tokens=edgeLine.split(",");
                for (String token : tokens) {
                    int nodeInEdge = Integer.parseInt(token);
                    e.add(nodeInEdge);
                }
                edges.add(e);
            }
            nodeToEdgesMap.put(node, edges);
        }

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);

        return nodeToEdgesMap;
    }
}
