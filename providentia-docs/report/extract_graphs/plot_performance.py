import matplotlib.pyplot as plt
import numpy as np
import csv
import config

ANALYSIS = ""

if config.KERN == 1:
    ANALYSIS = "kate"
elif config.KERN == 2:
    ANALYSIS = "reviews"
elif config.KERN == 3:
    ANALYSIS = "city"

t_avg = np.array([])
t_dev = np.array([])
p_avg = np.array([])
p_dev = np.array([])
j_avg = np.array([])
j_dev = np.array([])
x = []

import matplotlib.pyplot as plt
import sklearn.preprocessing as preprocessing
scaler = preprocessing.MinMaxScaler()

all_val = np.array([])

with open('./setup%d_results/setup%d_%s.csv' % (config.SETUP, config.SETUP, ANALYSIS)) as csvfile:
    reader =  csv.reader(csvfile, delimiter=",")
    for row in reader:
        avg = float(row[0])
        dev = float(row[1])
        perc = float(row[2])
        
        g = row[3]
        if g == "JanusGraph":
            j_avg = np.append(j_avg, avg)
            j_dev = np.append(j_dev, dev)
            x.append(perc)
        elif g == "TigerGraph":
            t_avg = np.append(t_avg, avg)
            t_dev = np.append(t_dev, dev)
        elif g == "PostgreSQL":
            p_avg = np.append(p_avg, avg)
            p_dev = np.append(p_dev, dev)

        all_val = np.concatenate((all_val, [avg]))

scaler.fit(all_val.reshape(-1, 1))
j_avg = scaler.transform(j_avg.reshape(-1, 1))
t_avg = scaler.transform(t_avg.reshape(-1, 1))
p_avg = scaler.transform(p_avg.reshape(-1, 1))
j_dev = scaler.transform(j_dev.reshape(-1, 1))
t_dev = scaler.transform(t_dev.reshape(-1, 1))
p_dev = scaler.transform(p_dev.reshape(-1, 1))

print(j_dev)
print(j_avg)
print(x)
plt.errorbar(x, j_avg, yerr=j_dev, label="JanusGraph", ecolor='purple', color="green")
plt.errorbar(x, t_avg, yerr=t_dev, label="TigerGraph", ecolor='red', color="orange")
plt.errorbar(x, p_avg, yerr=p_dev, label="PostgreSQL", ecolor='black', color="blue")

plt.title("Setup %d | Kernel %d: Database Response Times Over Percentage Data" % (config.SETUP, config.KERN))
plt.xlabel("Percentage of the dataset")
plt.ylabel("Response time (Scaled)")
plt.legend()
plt.savefig('%sPlotSetup%s.pdf' % (ANALYSIS, config.SETUP))
