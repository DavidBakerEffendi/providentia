import psycopg2
import numpy as np
import matplotlib.pyplot as plt
import sklearn.preprocessing as preprocessing
import config
import os

analysis_list = {
    "kate": "81c1ab05-bb06-47ab-8a37-b9aeee625d0f",  # Kate
    "rev_trends": "b540a4dd-f010-423b-9644-aef4e9b754a9",  # Rev Trends
    "julie": "05c2c642-32c0-4e6a-a0e5-c53028035fc8"  # Julies
}

QUERY = 'SELECT databases.name, AVG({}) as avg, STDDEV_SAMP({}) as stddev ' \
        'FROM benchmarks ' \
        'JOIN databases ON database_id = databases.id ' \
        'WHERE benchmarks.analysis_id = $$%s$$ ' \
        'GROUP BY databases.name ORDER BY databases.name'.format(config.MEASURE, config.MEASURE, config.MEASURE)


def produce_graph_from_query(analysis_name, analysis_id):
    file_name = 'setup{}_query_speeds_{}perc'.format(config.SETUP, config.PERC)
    try:
        sql_conn = psycopg2.connect(config.CONN)
        cur = sql_conn.cursor()
        cur.execute(QUERY % analysis_id)
        rows = cur.fetchall()
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
    for n, av, sd in rows:
        labels.append(n)
        avgs.append(av)
        devs.append(sd)

    # Save results
    save_results(file_name, analysis_name, avgs, devs)

    # if config.SCALE is True:
    #     avgs = np.array(avgs).reshape(-1, 1)
    #     devs = np.array(devs).reshape(-1, 1)
    #
    #     scaler = preprocessing.MinMaxScaler()
    #     scaler.fit(avgs)
    #     avgs = scaler.transform(avgs)
    #     devs = scaler.transform(devs)
    #
    #     plt_avgs = []
    #     plt_dv = []
    #
    #     for av, dvs in zip(avgs, devs):
    #         plt_avgs.append(av[0])
    #         plt_dv.append(dvs[0])
    # else:
    #     plt_avgs = avgs
    #     plt_dv = devs
    #
    # x = np.arange(len(labels))  # the label locations
    # width = 0.5  # the width of the bars
    #
    # fig, ax = plt.subplots()
    # ax.bar(x, plt_avgs, width, align='center', yerr=plt_dv, ecolor='red', capsize=7)
    #
    # # Add some text for labels, title and custom x-axis tick labels, etc.
    # ax.set_ylabel('Query Speeds (Scaled)')
    # ax.set_title('Setup {}: Query speeds by database with {}% of dataset'.format(config.SETUP, config.PERC))
    # ax.set_xlabel('Databases')
    # ax.set_xticks(x)
    # ax.set_xticklabels(labels)
    # ax.yaxis.grid(True)
    #
    # fig.tight_layout()
    # plt.savefig('%s.png' % file_name)


def save_results(file_pref, analysis, avgs, devs):
    file_name = "%s.csv" % file_pref
    with open(file_name, 'a+') as f:
        for a, d in zip(avgs, devs):
            f.write('%s,%f,%f,%s\n' % (analysis, a, d, config.PERC))


if __name__ == "__main__":
    for n, a in analysis_list.items():
        produce_graph_from_query(n, a)
