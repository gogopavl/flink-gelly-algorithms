package flink;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.graph.library.linkanalysis.PageRank;
import org.apache.flink.types.NullValue;

/**
 * Class implementing the PageRank algorithm on a graph
 */
public class PR {

	public static void main(String[] args) throws Exception {
		// set up the batch execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		ParameterTool params = ParameterTool.fromArgs(args);

		env.getConfig().setGlobalJobParameters(params); // Make params available to the web ui
		
		String edgeListFilePath = params.get("links", "Error");

		long toc = System.nanoTime();
		
		Graph<Long, NullValue, NullValue> graph = Graph.fromCsvReader(edgeListFilePath, env).keyType(Long.class);

		PageRank<Long, NullValue, NullValue> pageRank = new PageRank<>(0.85, 20, 0.0001);
		DataSet<PageRank.Result<Long>> result = pageRank.runInternal(graph);

		result.print();

		long tic = System.nanoTime();

		long totalNanos = tic-toc;
		double totalSeconds = (double) totalNanos / 1_000_000_000;

		System.out.println("Total runtime: " + totalSeconds +" seconds");

		// execute program
		// env.execute("Flink PageRank");
	}
}
