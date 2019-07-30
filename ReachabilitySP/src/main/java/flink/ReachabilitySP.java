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
public class ReachabilitySP {

	public static void main(String[] args) throws Exception {
		
		// set up the batch execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		ParameterTool params = ParameterTool.fromArgs(args);

		env.getConfig().setGlobalJobParameters(params); // Make params available to the web ui
		
		String edgeListFilePath = params.get("links", "Error");
		long source = Long.parseLong(params.get("source", "Error"));
		long target = Long.parseLong(params.get("target", "Error"));
		int iters = 20; // Maximum number of iterations

		long toc = System.nanoTime();
		
		Graph<Long, NullValue, NullValue> graph = Graph.fromCsvReader(edgeListFilePath, env).keyType(Long.class);

		// Adding weights to graph edges for the shortest path algorithm
		Graph<Long, NullValue, Double> weightedGraph = graph.mapEdges(new MapFunction<Edge<Long, NullValue>, Double>(){
			@Override
			public Double map(Edge<Long, NullValue> edge) throws Exception {
				return 1.0;
			}
		});

		SingleSourceShortestPaths<Long, NullValue> singleSourceShortestPaths = new SingleSourceShortestPaths<>(source,iters);
		DataSet<Vertex<Long, Double>> result = singleSourceShortestPaths.run(weightedGraph);

		DataSet<Vertex<Long, Double>> filteredResult = result.filter(vertex -> vertex.getId().equals(target));

		System.out.println("Distance between "+ source +" and "+ target +" is: "+ filteredResult.first(1).collect().get(0).getValue());

		long tic = System.nanoTime();

		long totalNanos = tic-toc;
		double totalSeconds = (double) totalNanos / 1_000_000_000;

		System.out.println("Total runtime: " + totalSeconds +" seconds");

		// execute program
		// env.execute("Flink ReachabilitySP");
	}
}
