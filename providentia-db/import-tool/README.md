# Import Tool
Import Yelp (round 13's) dataset into various databases. This code is designed to be used with JanusGraph 0.3.1 / Hadoop 2 with Cassandra 2.1.x.

The following are the steps on how to get started:

0) Download the Yelp Academic Dataset (JSON) [here](https://www.yelp.com/dataset/download).
1) Download JanusGraph 0.3.1.
   You can download a prebuilt zip [here](https://github.com/JanusGraph/janusgraph/releases/tag/v0.3.1).
2) Download this package and configure the properties under `src/main/resources/import-tool.properties`.
3) Install dependencies and package the application with Maven via:
    ```
    $ mvn clean install
    ```
4) Once everything is packaged and ready to run, then execute the bash script to launch the import tool with:
   ```
   $ ./bin/run.sh
   ```

## Yelp JanusGraph Schema

![Yelp JanusGraph Schema](../../providentia-assets/ProvidentiaJanus.png "Yelp JanusGraph Schema")

The above diagram illustrates the schema created by this application. Indices created on vertices holding custom unique 
IDs from the data filtered by vertex labels. What is indexed in the graph is written in italics on the diagram.

## Importing Data Into TigerGraph

TigerGraph data is imported using the batch offline import utility. See ``../docker-containers/tigergraph/``. This is 
due to insert speeds being much slower than this solution.

## Existing data

The import tool will be able to handle existing data for all businesses and reviews but not for anything user or friend 
related. This is due to the enormous increase in time complexity when existence is checked before insertion.

## Import Tool Configuration Reference
This application's properties can be modified via `import-tool.properties`. The following are the configurations native 
to this tool with a description for each:

|Configuration|Description|Default Value|
|----|----|----|
|`import.drop-and-load-schema`|This drops the whole graph if it exists and loads thew new schema. This should only be set to false if the import process was paused for some reason.|false|
|`import.queue-size`|Configures the maximum number of lines to add to a transaction before committing it to the DBMS. This is used to exploit the bulk-loading option.  This is kept fairly low to prevent ghost vertices/edges being created. Once can play with this setting to find optimal balance where self-dependent data is not affected, e.g. businesses.|100|
|`import.sector-size`|The sector size of the file being read where a sector defines a fraction of the datafile. The way this is used is that if the number records read exceed the size of the current sector in which number of records processed lie then the tool stops creating new jobs to prevent GC memory overflow.|0.10|
