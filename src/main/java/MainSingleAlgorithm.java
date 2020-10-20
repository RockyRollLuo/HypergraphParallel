import algorithm.Decomposition;
import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.FileIOUtils;
import util.GetUtils;
import util.SetOpt;
import util.SetOpt.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainSingleAlgorithm {
    private static final Logger LOGGER = Logger.getLogger(MainSingleAlgorithm.class);

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'p', usage = "whether print the core number in result, 0:no,1:yes")
    public static int printResult = 1;

    @Option(abbr = 'c', usage = "whether to constructe nodeToEdgesMap, false:no, true:yes")
    public static boolean constructStructure = true;


    public static void main(String[] args) throws IOException {
        /*
        read parameters
         */
        MainSingleAlgorithm main = new MainSingleAlgorithm();
        args = SetOpt.setOpt(main, args);

        /*
        graph information
         */
        String datasetName = args[0];
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, GetUtils.getDelim(delimType),constructStructure);
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        HashMap<Integer,ArrayList<Integer>> edgeMap = hypergraph.getEdgeMap();

        System.out.println("dataset:" + datasetName);
        System.out.println("node size:" + nodeList.size());
        System.out.println("edge size:" + edgeMap.size());

        Decomposition decomposition = new Decomposition(hypergraph);
        Result result_decomposition = decomposition.run();
        result_decomposition.setDatasetName(datasetName);
        result_decomposition.setType("full");
        FileIOUtils.writeCoreNumber(result_decomposition,printResult);




    }
}
