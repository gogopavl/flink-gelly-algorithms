# Flink Gelly Algorithms
Repository containing various graph algorithm implementations using Flink's Gelly Graph API

## Installation

All jobs were run on [Flink 1.8.0](https://flink.apache.org/downloads.html#apache-flink-180).

Information about setting up Flink can be found in [Flink's Official Documentation](https://ci.apache.org/projects/flink/flink-docs-release-1.8/).

## Building JARs

1. `cd` into an algorithm implementation directory (e.g. `cd PageRank`)
2. Run `mvn clean package` in order to build the project
3. The JAR is located within the `target` folder

## Configuring the cluster

To configure the system edit the `conf/flink-conf.yaml` file

- `jobmanager.heap.size: Nm` (the heap size for the JobManager JVM - e.g. 4096m)

- `taskmanager.heap.size: Nm` (the heap size for the TaskManager JVM - e.g. 4096m)

- `taskmanager.numberOfTaskSlots: N` (the number of parallel operator or user function instances that a single TaskManager can run (DEFAULT: 1). If this value is larger than 1, a single TaskManager takes multiple instances of a function or operator. That way, the TaskManager can utilize multiple CPU cores, but at the same time, the  available memory is divided between the different operator or function instances. This value is typically proportional to the number of physical CPU cores that the TaskManager’s machine has - e.g. equal to the number of cores, or half the number of cores)

## Starting/Monitoring/Stopping the cluster

- To start the (local) Flink cluster run:

```
./Flink-1.8.0/bin/start-cluster.sh
```

- You can check jobs through the web UI (default: [http://localhost:8081/#/overview](http://localhost:8081/#/overview))

- To stop the cluster run:
```
./Flink-1.8.0/bin/stop-cluster.sh
```

## Running a job

To run a Flink job via the [Flink CLI](https://ci.apache.org/projects/flink/flink-docs-stable/ops/cli.html) execute the following command:

```
./Flink-1.8.0/bin/flink run path/to/JAR --links path/to/edgelist/csv/file
```

## SW Versions

- Scala 2.12.8
- Java 1.8
- Maven 3.6.0

## License

As stated in [LICENSE](https://github.com/pgogousis/flink-gelly-algorithms/blob/master/LICENSE).