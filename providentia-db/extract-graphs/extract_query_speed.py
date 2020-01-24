import psycopg2
import numpy as np
import matplotlib.pyplot as plt
import sklearn.preprocessing as preprocessing
import config
import os

analysis_list = {
    "kate": "81c1ab05-bb06-47ab-8a37-b9aeee625d0f",  # Kate
    "rev_trends": "b540a4dd-f010-423b-9644-aef4e9b754a9",  # Rev Trends
    "julie": "05c2c642-32c0-4e6a-a0e5-c53028035fc8",  # Julies
    "sim1": "899760bd-417e-431c-bac1-d5e4a8e16462",  # Sim1
    "sim2": "34a6d0e2-ca77-4615-a873-9a0d0b92559b",  # Sim2
    "sim3": "2d8ca3c7-ab16-4567-a821-1d480ce19bfa"  # Sim3
}

QUERY = 'SELECT databases.name, AVG(query_time) as avg, STDDEV_SAMP(query_time) as stddev ' \
        'FROM benchmarks ' \
        'JOIN databases ON database_id = databases.id ' \
        'WHERE benchmarks.analysis_id = $$%s$$ ' \
        'GROUP BY databases.name ORDER BY databases.name'


def produce_graph_from_query(analysis_name, analysis_id):
    file_name = 'setup{}_query_speeds'.format(config.SETUP)
    if config.DS == 'yelp':
        file_name = '{}_{}perc'.format(file_name, config.PERC)
    try:
        sql_conn = psycopg2.connect(config.CONN)
        cur = sql_conn.cursor()
        cur.execute(QUERY % analysis_id)
        rows = cur.fetchall()
        if len(rows) == 0:
            return
        print(rows)
    except Exception as e:
        print(str(e))
        exit(1)
    finally:
        if sql_conn is not None:
            sql_conn.close()

    # Process results
    labels = []
    avgs = []
    devs = []
    dbs = []
    for n, av, sd in rows:
        labels.append(n)
        avgs.append(av)
        devs.append(sd)
        dbs.append(n)

    # Save results
    save_results(file_name, dbs, analysis_name, avgs, devs)

def save_results(file_pref, dbs, analysis, avgs, devs):
    file_name = "%s.csv" % file_pref
    with open(file_name, 'a+') as f:
        for db, a, d in zip(dbs, avgs, devs):
            if config.DS == 'sim':
                f.write('%s,%f,%f,%s\n' % (analysis, a, d, db))
            else:
                f.write('%s,%f,%f,%s,%s\n' % (analysis, a, d, config.PERC, db))


if __name__ == "__main__":
    for n, a in analysis_list.items():
        produce_graph_from_query(n, a)
