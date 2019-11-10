SCALE = True
PERC = '20'
SETUP = 1

if SETUP == 1:
    CONN = 'postgres://postgres:docker@0.0.0.0:5432/providentia'
elif SETUP == 2:
    CONN = 'postgres://postgres:docker@hydra.cs.sun.ac.za:5432/providentia'

MEASURE = 'query_time'  # query_time, analysis_time

# QUERY = 'SELECT databases.name, AVG({}) as avg, STDDEV_SAMP({}) - AVG({}) as stddev ' \
#         'FROM benchmarks ' \
#         'JOIN databases ON database_id = databases.id ' \
#         'GROUP BY databases.name'.format(MEASURE, MEASURE, MEASURE)

# 'JOIN analysis ON analysis.id = benchmarks.id '
