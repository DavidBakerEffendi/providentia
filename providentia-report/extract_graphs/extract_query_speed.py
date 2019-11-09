import psycopg2
import numpy as np
import matplotlib.pyplot as plt
import sklearn.preprocessing as preprocessing
import config

if __name__ == "__main__":
    try:
        sql_conn = psycopg2.connect(config.CONN)
        cur = sql_conn.cursor()
        cur.execute(config.QUERYA1.format(config.MEASURE, config.MEASURE, config.MEASURE))
        rows = cur.fetchall()
        print(rows)
    except Exception as e:
        print(str(e))
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

    if config.SCALE is True:
        avgs = np.array(avgs).reshape(-1, 1)
        devs = np.array(devs).reshape(-1, 1)

        scaler = preprocessing.MinMaxScaler()
        scaler.fit(avgs)
        avgs = scaler.transform(avgs)
        devs = scaler.transform(devs)

        plt_avgs = []
        plt_dv = []

        for av, dvs in zip(avgs, devs):
            plt_avgs.append(av[0])
            plt_dv.append(dvs[0])
    else:
        plt_avgs = avgs
        plt_dv = devs
    
    x = np.arange(len(labels))  # the label locations
    width = 0.5  # the width of the bars

    fig, ax = plt.subplots()
    rects1 = ax.bar(x, plt_avgs, width, align='center', yerr=plt_dv, ecolor='red', capsize=7)

    # Add some text for labels, title and custom x-axis tick labels, etc.
    ax.set_ylabel('Query Speeds (Scaled)')
    ax.set_title('Query Speeds by Database')
    ax.set_xlabel('Databases')
    ax.set_xticks(x)
    ax.set_xticklabels(labels)
    ax.yaxis.grid(True)

    fig.tight_layout()
    plt.savefig('query_speeds_{}.png'.format(config.SUFFIX))
    plt.show()
