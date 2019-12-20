PERC = '1.1'
SETUP = 2
KERN = 3

if SETUP == 1:
    CONN = 'postgres://postgres:docker@0.0.0.0:5432/providentia'
elif SETUP == 2:
    CONN = 'postgres://postgres:docker@35.177.36.36:5432/providentia'
elif SETUP == 3:
    CONN = 'postgres://postgres:docker@hydra.cs.sun.ac.za:5432/providentia'
