import numpy as np

a = np.array([1, 2, 3])
b = np.array([2, 3, 4])

c = np.subtract(a,b)

c = np.ndarray(shape = (2,3))
c[0] = a
c[1] = b

print c