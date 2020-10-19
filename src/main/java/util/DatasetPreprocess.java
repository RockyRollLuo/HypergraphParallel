package util;

import model.Hypergraph;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DatasetPreprocess {
    private static final Logger LOGGER = Logger.getLogger(DatasetPreprocess.class);

    public static void hypergraphPreprocess() throws IOException {

        String datasetNameList[] = {"test", "congress-bills", "tags-stack-overflow", "coauth-DBLP", "threads-stack-overflow"};

        boolean constructTimeToEdgesMap = false;
        String dataset = datasetNameList[4];
        String datasetPath = "C:\\Users\\luoqi\\Desktop\\TemporalHypergraphs\\" + dataset;
        String file_nverts = dataset + "-nverts.txt";
        String file_simplices = dataset + "-simplices.txt";
        String file_times = dataset + "-times.txt";

        /*
        read file
         */
        ArrayList<Integer> edgesSizeList = readIntegerFile(datasetPath, file_nverts);
        ArrayList<Integer> nodesList = readIntegerFile(datasetPath, file_simplices);

        /*
        construct hyperedges
         */
        ArrayList<ArrayList<Integer>> tempEdgeList = new ArrayList<>();
        int index = 0;
        for (int edgeSize : edgesSizeList) {
            ArrayList<Integer> edge = new ArrayList<Integer>(nodesList.subList(index, index + edgeSize));
            tempEdgeList.add(edge);
            index += edgeSize;
        }
        ArrayList<ArrayList<Integer>> edgeList;
        if (!constructTimeToEdgesMap) {
            /*
            remove duplicates edges and write file
             */
            HashSet<ArrayList<Integer>> tempEdgeSet = new HashSet<>(tempEdgeList);
            edgeList = new ArrayList<>(tempEdgeSet);
            writeFile(edgeList, datasetPath, dataset);
        } else {
             /*
            construct timestamp hyperedges
            */
            edgeList = tempEdgeList;
            ArrayList<String> timesList = readStringFile(datasetPath, file_times);
            HashMap<String, ArrayList<ArrayList<Integer>>> timeToEdgesListMap = new HashMap<>();
            int edgeSize = edgeList.size();
            for (int i = 0; i < edgeSize; i++) {
                String time = timesList.get(i);
                ArrayList<Integer> edge = edgeList.get(i);

                if (timeToEdgesListMap.containsKey(time)) {
                    ArrayList<ArrayList<Integer>> edges = timeToEdgesListMap.get(time);
                    edges.add(edge);
                    timeToEdgesListMap.put(time, edges);
                } else {
                    ArrayList<ArrayList<Integer>> edges = new ArrayList<>();
                    edges.add(edge);
                    timeToEdgesListMap.put(time, edges);
                }
            }
            //System.out.println(timeToEdgesListMap.toString());
            //sort edges by time in map
            //TODO:split edges by time, batch insert edges in latter time
            //TODO:write the edges by time
        }
    }

    private static ArrayList<Integer> readIntegerFile(String datasetPath, String fileName) throws IOException {
        long startTime = System.nanoTime();

        String path = datasetPath + "\\" + fileName;
        ArrayList<Integer> list = new ArrayList<>();
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            Integer value = Integer.parseInt(line);
            list.add(value);
        }

        long endTime = System.nanoTime();
        LOGGER.info(fileName + " READ DONE!: " + (double) (endTime - startTime) / 1.0E9D);

        return list;
    }

    private static ArrayList<String> readStringFile(String datasetPath, String fileName) throws IOException {
        long startTime = System.nanoTime();

        String path = datasetPath + "\\" + fileName;
        ArrayList<String> list = new ArrayList<>();
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            list.add(line);
        }

        long endTime = System.nanoTime();
        LOGGER.info(fileName + " READ DONE!: " + (double) (endTime - startTime) / 1.0E9D);

        return list;
    }

    public static void writeFile(ArrayList<ArrayList<Integer>> edgeList, String datasetPath, String fileName) throws IOException {
        long startTime = System.nanoTime();

        String path = datasetPath + "\\" + fileName + "-hyperedges" + ".txt";

        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (ArrayList<Integer> edge : edgeList) {

            String line = edge.toString().replace("[", "").replace("]", "").replace(",", "");
            bw.write(line);
            bw.newLine();
        }
        bw.close();

        long endTime = System.nanoTime();
        LOGGER.info(fileName + " WRITE DONE!: " + (double) (endTime - startTime) / 1.0E9D);
    }

    public static void projectGraphPreprocess() throws IOException {

        String datasetNameList[] = {"test", "tags-math-sx", "DAWN", "tags-ask-ubuntu", "NDC-substances", "threads-ask-ubuntu", "threads-math-sx", "coauth-MAG-History", "coauth-MAG-Geology"};

        String dataset = datasetNameList[8];

        /*
        read file
         */
        long startTime1 = System.nanoTime();
        String path_read = "C:\\Users\\luoqi\\Desktop\\projectGraph\\" + dataset + "-proj-graph\\" + dataset + "-proj-graph.txt";
        ArrayList<ArrayList<Integer>> edgeList = new ArrayList<>();
        final BufferedReader br = new BufferedReader(new FileReader(path_read));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            String token[] = line.split(" ");
            Integer v = Integer.parseInt(token[0]);
            Integer u = Integer.parseInt(token[1]);
            ArrayList<Integer> edge = new ArrayList<>();
            edge.add(v);
            edge.add(u);
            edgeList.add(edge);
        }
        long endTime1 = System.nanoTime();
        LOGGER.info("READ DONE!: " + (double) (endTime1 - startTime1) / 1.0E9D);

        /*
        write file
         */
        long startTime2 = System.nanoTime();
        String path_write = "C:\\Users\\luoqi\\Desktop\\projectGraph\\" + dataset + "-proj-graph\\" + dataset + ".txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter(path_write));
        for (ArrayList<Integer> edge : edgeList) {
            String line = edge.toString().replace("[", "").replace("]", "").replace(",", "");
            bw.write(line);
            bw.newLine();
        }
        bw.close();
        long endTime2 = System.nanoTime();
        LOGGER.info("WRITE DONE!: " + (double) (endTime2 - startTime2) / 1.0E9D);
    }

    public static void statisticHypergraph() throws IOException {

        String datasetNameList[] = {"test", "tags-math", "DAWN", "tags-ask-ubuntu", "NDC-substances", "threads-ask-ubuntu", "threads-math", "coauth-History", "coauth-Geology"};

//        String datasetAll = datasetNameList[5];

        for (String dataset : datasetNameList) {
        /*
        read file
         */
            long startTime1 = System.nanoTime();
            String path_read = "datasets\\" + dataset + ".txt";

            ArrayList<ArrayList<Integer>> edgeList = new ArrayList<>();
            //read edges
            final BufferedReader br = new BufferedReader(new FileReader(path_read));
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                String[] tokens = line.split(" ");

                ArrayList<Integer> newEdge = new ArrayList<>();
                for (String token : tokens) {
                    int node = Integer.parseInt(token);
                    newEdge.add(node);
                }

                edgeList.add(newEdge);
            }

            int maxCardinality = 1;
            int sumCardinality = 0;
            for (ArrayList<Integer> e : edgeList) {
                int cardi = e.size();

                maxCardinality = Math.max(maxCardinality, cardi);
                sumCardinality += cardi;
            }

            System.out.println("=================" + dataset);
            System.out.println("maxCardinality:" + maxCardinality);
            System.out.println("avgCardinality:" + sumCardinality * 1.0 / edgeList.size());


            long endTime1 = System.nanoTime();
            LOGGER.info("WRITE DONE!: " + (double) (endTime1 - startTime1) / 1.0E9D);
        }
    }


    public static void storeNodeToEdgesFile() throws IOException {
        String datasetNameList[] = {"test", "tags-math", "DAWN", "tags-ask-ubuntu", "NDC-substances", "threads-ask-ubuntu", "threads-math", "coauth-History", "coauth-Geology"};


        String dataset = datasetNameList[8];
        long startTime1 = System.nanoTime();
        String path_read = dataset + ".txt";

        Hypergraph hypergraph = FileIOUtils.loadGraph(path_read, " ", true);


//        LOGGER.info("before:"+hypergraph.getNodeToEdgesMap().toString());

        FileIOUtils.writeNodeToEdgesMap(hypergraph.getNodeToEdgesMap(), dataset);

//        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap=FileIOUtils.loadNodeToEdgesMap(dataset);
//        LOGGER.info("after:"+nodeToEdgesMap.toString());

//        System.out.println(nodeToEdgesMap.get(1).toString());

        long endTime1 = System.nanoTime();
        LOGGER.info("WRITE DONE!: " + (double) (endTime1 - startTime1) / 1.0E9D);

    }

    public static void hypercoreDistribution() throws IOException {

        String datasetNameList[] = {"DAWN"};
//        String datasetNameList[] = {"tags-math", "DAWN" ,"tags-ask-ubuntu", "NDC-substances", "threads-ask-ubuntu", "threads-math", "coauth-History", "coauth-Geology"};

//        String dataset = datasetNameList[4];

        for (String dataset : datasetNameList) {
        /*
        read file
         */
            System.out.println("===" + dataset + "===============================");
            long startTime1 = System.nanoTime();
            String path_core = "corenumber\\Decomposition_" + dataset + ".txt_full";

            HashMap<Integer, Integer> coreVMap = new HashMap<>();

            //read core
            final BufferedReader br = new BufferedReader(new FileReader(path_core));
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("#")) continue;

                String[] tokens = line.split("\t");
                coreVMap.put(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
            }



            long endTime1 = System.nanoTime();
            LOGGER.info("WRITE DONE!: " + (double) (endTime1 - startTime1) / 1.0E9D);
        }
    }

    public static void main(String[] args) throws IOException {
//        hypergraphPreprocess();

//        projectGraphPreprocess();

//        statisticHypergraph();

//        storeNodeToEdgesFile();

        hypercoreDistribution();
    }
}
