import numpy as np

G = np.array([[1, 2, 3], [4, 5, 6], [4, 5, 6]])

A = G.transpose()


print np.linalg.det(np.cross(A,G))
