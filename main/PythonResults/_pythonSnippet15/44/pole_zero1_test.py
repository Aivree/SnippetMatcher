import numpy as np
from pole_zero1 import zplane
b = np.array([6])
a = np.array([1,-1/5., -1/6.])
zplane(b, a)