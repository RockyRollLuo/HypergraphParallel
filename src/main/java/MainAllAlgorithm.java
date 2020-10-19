import algorithm.Decremental;
import algorithm.DecrementalBatchEdges;
import algorithm.Incremental;
import algorithm.IncrementalBatchEdges;
import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.ComputeUtils;
import util.FileIOUtils;
import util.SetOpt;
import util.SetOpt.Option;
import util.GetUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainAllAlgorithm {
    private static final Logger LOGGER = Logger.getLogger(MainAllAlgorithm.class);

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'p', usage = "whether print the core number in result, 0:not print, 1:print")
    public static int printResult = 0;

    @Option(abbr = 'c', usage = "cardinality of dynamic edges, 0:low,1:avg,2:high")
    public static int cardi = 1;

    @Option(abbr = 'n', usage = "number of dynamic edge")
    public static int dynamicNums = 1000;


    public static void main(String[] args) throws IOException {
        /*
        read parameters
         */
        MainAllAlgorithm main = new MainAllAlgorithm();
        args = SetOpt.setOpt(main, args);
        LOGGER.info("Run information:");
        System.out.println("print core number in result :" + printResult);
        System.out.println("cardinality of dynamic edges :" + GetUtils.getCardiDistribution(cardi));

        /*
        graph information
         */
        String datasetName = args[0];
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, GetUtils.getDelim(delimType), false); //read hypergraph
        HashMap<Integer, ArrayList<Integer>> nodeToEdgesMap = FileIOUtils.loadNodeToEdgesMap(datasetName); //read nodeToEdge
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
        System.out.println("dynamic edges choose cardinality distribution:"+ GetUtils.getCardiDistribution(cardi));
        ArrayList<Integer> dynamicEdges = ComputeUtils.computeDynamicEdges(edgeMap,dynamicNums, cardi);

        /*
        decremental
         */
        DecrementalBatchEdges decremental = new DecrementalBatchEdges(hypergraph, coreVMap, coreEMap, dynamicEdges);
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

//        hypergraph.deleteEdge(dynamicEdges);
        /*
        incremental
         */
        IncrementalBatchEdges incremental = new IncrementalBatchEdges(hypergraph, decremental.getCoreVMap(), decremental.getCoreEMap(), dynamicEdges);
        Result result_incremental = incremental.run();
        result_incremental.setDatasetName(datasetName);
        FileIOUtils.writeCoreNumber(result_incremental, printResult);


    }
}
