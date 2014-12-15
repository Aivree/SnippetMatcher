

import numpy as np

def t(data):
    return np.array([data[:,0], np.sqrt((data[:, 1:]**2).sum(1))]).transpose()

