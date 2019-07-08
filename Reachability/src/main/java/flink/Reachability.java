package flink;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.graph.library.SingleSourceShortestPaths;
import org.apache.flink.types.NullValue;
import org.apache.flink.api.*;
import org.apache.flink.graph.*;
import java.util.List;

/**
 * Class that finds whether a target vertex is reachable from a given source vertex
 */
public class Reachability {

	static Graph<Long, NullValue, NullValue> graph;
	static DataSet<Edge<Long, NullValue>> nthNeighborhoodEdgeList;

	public static boolean isReachable(Long sourceVertex, Long targetVertex, int maxDepth) throws Exception {

		// Filter graph on edges to restrict neighborhoods
		Graph<Long, NullValue, NullValue> subGraph = graph.filterOnEdges(
			new FilterFunction<Edge<Long, NullValue>>() {
				public boolean filter(Edge<Long, NullValue> edge) {
					// Keep only edges where source is the sourceVertex
					return (edge.getSource().equals(sourceVertex));
				}
			}
		);
		List<Edge<Long, NullValue>> edgeList = subGraph.getEdges().collect();

		// Construct wanted edge to find through "contains"
		Edge<Long, NullValue> wantedEdge = new Edge<>(sourceVertex, targetVertex, new NullValue());

		if (edgeList.contains(wantedEdge)) {
			return true;
		}
		else {
			if (maxDepth > 1) {
				for (Edge<Long, NullValue> edge : edgeList) {
					// If child method has found the target vertex return true
					if (isReachable(edge.getTarget(), targetVertex,  maxDepth-1)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		// set up the batch execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		ParameterTool params = ParameterTool.fromArgs(args);

		env.getConfig().setGlobalJobParameters(params); // Make params available to the web ui
		
		String edgeListFilePath = params.get("links", "Error");

		long toc = System.nanoTime();
		

		// Graph<Long, NullValue, NullValue> graph = Graph.fromCsvReader(edgeListFilePath, env).keyType(Long.class);
		graph = Graph.fromCsvReader(edgeListFilePath, env).keyType(Long.class);

		Long source = new Long(1004); // Source vertex
		Long target = new Long(3658); // Target vertex
		int maxDepth = 2; // Maximum search depth

		System.out.println("vertex "+ target +" is reachable from vertex "+ source +" = "+ isReachable(source, target, maxDepth));

		long tic = System.nanoTime();
		long totalNanos = tic-toc;
		double totalSeconds = (double) totalNanos / 1_000_000_000;

		System.out.println("Total runtime: " + totalSeconds +" seconds");

		// execute program
		// env.execute("Flink Reachability");
	}
}
