package flink;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.graph.*;
import org.apache.flink.types.NullValue;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.graph.library.*;

/**
 * Class implementing triangle counting on a graph
 */
public class TriangleCounting {

	public static void main(String[] args) throws Exception {
		// set up the batch execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		ParameterTool params = ParameterTool.fromArgs(args);

		env.getConfig().setGlobalJobParameters(params); // Make params available to the web ui
		
		String edgeListFilePath = params.get("links", "Error");

		long toc = System.nanoTime();
		
		Graph<Long, NullValue, NullValue> simpleGraph = Graph.fromCsvReader(edgeListFilePath, env).keyType(Long.class);

		DataSet<Tuple3<Long,Long,Long>> result = simpleGraph.run(new TriangleEnumerator<Long, NullValue, NullValue>());

		System.out.println("Number of triangles: " + result.count());

		long tic = System.nanoTime();

		long totalNanos = tic-toc;
		double totalSeconds = (double) totalNanos / 1_000_000_000;

		System.out.println("Total runtime: " + totalSeconds +" seconds");

		// execute program
		// env.execute("Flink Triangle Counting");
	}
}
