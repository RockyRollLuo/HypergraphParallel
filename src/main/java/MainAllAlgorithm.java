import algorithm.Decremental;
import algorithm.Incremental;
import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.ComputeUtils;
import util.FileIOUtils;
import util.SetOpt;
import util.SetOpt.Option;
import util.ToolUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainAllAlgorithm {
    private static final Logger LOGGER = Logger.getLogger(MainAllAlgorithm.class);

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'p', usage = "whether print the core number in result, 0:not print, 1:print")
    public static int printResult = 0;

    @Option(abbr = 'm', usage = "the method of choosing dynamic edge, 0:degree choose, 1:cardinality choose, 2:core choose")
    public static int method = 2;

    @Option(abbr = 'd', usage = "degree position, 0:low,1:avg,2:high")
    public static int degreePosition = 1;

    @Option(abbr = 'c', usage = "cardinality of dynamic edge")
    public static int cardinality = 3;

    @Option(abbr = 'e', usage = "number of dynamic edge")
    public static int coreE = 20;


    public static void main(String[] args) throws IOException {
        /*
        read parameters
         */
        MainAllAlgorithm main = new MainAllAlgorithm();
        args = SetOpt.setOpt(main, args);
        LOGGER.info("Run information:");
        System.out.println("print core number in result :" + printResult);
        System.out.println("method of choosing dynamic edge :" + method);
        if (method == 0) {
            System.out.println("degreeposition of e0 :" + ToolUtils.getDegreePosition(degreePosition));
        } else if (method == 1) {
            System.out.println("cardinality of e0 :" + cardinality);
        } else if (method == 2) {
            System.out.println("corenumber of e0 :" + coreE);
        }

        /*
        graph information
         */
        String datasetName = args[0];
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, ToolUtils.getDelim(delimType), false);
        HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap = FileIOUtils.loadNodeToEdgesMap(datasetName);

        hypergraph.setNodeToEdgesMap(nodeToEdgesMap);

        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        HashMap<Integer,ArrayList<Integer>> edgeMap = hypergraph.getEdgeMap();
        System.out.println("dataset:" + datasetName);
        System.out.println("node size:" + nodeList.size());
        System.out.println("edge size:" + edgeMap.size());

        /*
        read decomposition full core file and compute coreEMap
         */
        String coreFile = "Decomposition_" + datasetName + "_full";
        HashMap<Integer, Integer> coreVMap = FileIOUtils.loadCoreFile(coreFile);
        HashMap<Integer, Integer> coreEMap = ComputeUtils.computeCoreEMapByCoreVMap(edgeMap, coreVMap);

         /*
        choose dynamic edge
         */
        ArrayList<Integer> e0 = new ArrayList<>();
        if (method == 0) {
            //1.degree distribution choose
            HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();
            degreeMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(degreeMap, 0); //sorted nodes by degree descending
            int index = ToolUtils.getNodeIndexRand(degreePosition, nodeList.size());
            Integer node = (new ArrayList<Integer>(degreeMap.keySet())).get(index);
            e0 = ToolUtils.getRandomElement(nodeToEdgesMap.get(node));
        }
        if (method == 1) {
            //2.cardinality choose
            HashMap<Integer, ArrayList<ArrayList<Integer>>> cariToEdgesMap = ToolUtils.getCardiToEdgesMap(edgeList);
            ArrayList<ArrayList<Integer>> cardiEdges = cariToEdgesMap.get(cardinality);
            int index = ToolUtils.getRandomInt(cardiEdges.size());
            e0 = cardiEdges.get(index);
        } else if (method == 2) {
            //3.core number choose
            HashMap<Integer, ArrayList<ArrayList<Integer>>> coreToEdgesMap = ToolUtils.getCoreToEdgesMap(coreEMap);
            ArrayList<ArrayList<Integer>> coreEdges = coreToEdgesMap.get(coreE);
            int index = ToolUtils.getRandomInt(coreEdges.size());
            e0 = coreEdges.get(index);
        }
        LOGGER.info("e0:" + e0.toString() + " core(e0):" + coreEMap.get(e0));

        /*
        decremental
         */
        Decremental decremental = new Decremental(hypergraph, coreEMap, coreVMap, e0);
        Result result_dcremental = decremental.run();
        result_dcremental.setDatasetName(datasetName);
        FileIOUtils.writeCoreNumber(result_dcremental, printResult);

        /*
        decomposition rest graph
         */
//        Decomposition decomposition_rest = new Decomposition(hypergraph); //the hypergraph is already the REST by decremental
//        Result result_decomposition_rest = decomposition_rest.run();
//        result_decomposition_rest.setDatasetName(datasetName);
//        result_decomposition_rest.setType("rest");
//        FileIOUtils.writeCoreNumber(result_decomposition_rest, printResult);
        hypergraph.deleteEdge(e0);
        /*
        incremental
         */
        Incremental incremental = new Incremental(hypergraph, decremental.getCoreEMap(), decremental.getCoreVMap(), e0);
        Result result_incremental = incremental.run();
        result_incremental.setDatasetName(datasetName);
        FileIOUtils.writeCoreNumber(result_incremental, printResult);


    }
}
