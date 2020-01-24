import matplotlib.pyplot as plt
import numpy as np
import csv
import config

sim1 = {
    'name': 'sim1',
    'avg': [],
    'dev': [],
    'g': []
}
sim2 = {
    'name': 'sim2',
    'avg': [],
    'dev': [],
    'g': []
}
sim3 = {
    'name': 'sim3',
    'avg': [],
    'dev': [],
    'g': []
}

sims = [sim1, sim2, sim3]

all_val = np.array([])

with open('./%s_results/setup%d_query_speeds.csv' % (config.DS, config.SETUP)) as csvfile:
    reader = csv.reader(csvfile, delimiter=",")
    for row in reader:
        if row[0] == 'sim1':
            sim1['avg'].append(float(row[1]))
            sim1['dev'].append(float(row[1]) - float(row[2]))
            sim1['g'].append(row[3])
        elif row[0] == 'sim2':
            sim2['avg'].append(float(row[1]))
            sim2['dev'].append(float(row[1]) - float(row[2]))
            sim2['g'].append(row[3])
        elif row[0] == 'sim3':
            sim3['avg'].append(float(row[1]))
            sim3['dev'].append(float(row[1]) - float(row[2]))
            sim3['g'].append(row[3])
    print(sim1)
    print(sim2)
    print(sim3)

ind = np.arange(3)

i = 0
for s in sims:
    i += 1
    plt.title("Setup %d | Kernel %d: Database Response Times Per Database" % (config.SETUP, i))
    plt.xlabel("Databases")
    plt.ylabel("Response time (ms)")
    plt.bar(x=ind, height=list(reversed(s['avg'])), yerr=list(reversed(s['dev'])))
    plt.xticks(ticks=ind, labels=list(reversed(s['g'])))
    plt.savefig('./%s_graphs/%sPlotSetup%s.pdf' % (config.DS, s['name'], config.SETUP))

    plt.clf()
