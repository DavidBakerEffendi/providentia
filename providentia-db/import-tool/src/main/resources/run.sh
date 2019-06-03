PROJPATH=./target
LIBPATH=./target/lib

java -cp "$PROJPATH/import-janusgraph-1.0-SNAPSHOT.jar:$LIBPATH/slf4j-log4j12-1.7.12.jar:$LIBPATH/*"  za.ac.sun.cs.providentia.import_tool.ImportTool  ./janus-graph.properties