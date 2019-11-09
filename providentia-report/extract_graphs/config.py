#CONN = 'postgres://postgres:docker@146.232.213.102:5432/providentia'
CONN = 'postgres://postgres:docker@0.0.0.0:5432/providentia'

SCALE = True
SUFFIX = '10perc'
MEASURE = 'query_time' # query_time, analysis_time

QUERY = 'SELECT databases.name, AVG({}) as avg, STDDEV_SAMP({}) - AVG({}) as stddev FROM benchmarks JOIN databases ON database_id = databases.id GROUP BY databases.name'
QUERYA1  = 'SELECT databases.name, AVG({}) as avg, STDDEV_SAMP({}) - AVG({}) as stddev FROM benchmarks JOIN databases ON database_id = databases.id JOIN analysis ON analysis.id = benchmarks.id WHERE analysis.id = $$81c1ab05-bb06-47ab-8a37-b9aeee625d0f$$ GROUP BY databases.name'