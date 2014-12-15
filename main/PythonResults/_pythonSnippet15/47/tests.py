__author__ = '7times6'

import numpy as np

a = np.array([1, 2, 3, 4, 5])

print a, a.dtype

print a.cumsum()

print np.histogram(a, 2)