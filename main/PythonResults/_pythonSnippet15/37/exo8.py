import numpy as np

a = np.arange(3)
b = np.array([-1., 1., 2.])
a[:, np.newaxis]*b
