PERC = '7'
SETUP = 1
KERN = 1

if SETUP == 1:
    CONN = 'postgres://postgres:docker@0.0.0.0:5432/providentia'
elif SETUP == 2:
    CONN = 'postgres://postgres:docker@hydra.cs.sun.ac.za:5432/providentia'
