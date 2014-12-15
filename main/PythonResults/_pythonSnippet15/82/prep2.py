import numpy as np

a = np.array([[1, 2, 3], [4, 5, 6]])
b = np.array([[1, 2], [3, 4], [5, 6]])
c = np.dot(a, b)
print(" c = ",c)
d = np.dot(b, a)
print(" d = ",d)
e = d * 2
print("e = ",e)
f = np.diag(c)
g = np.diag(c).sum()
print("trace of c:", g)
