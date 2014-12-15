#! /usr/bin/python

import numpy as np

a = [1,2,3,4,5]

b = np.array([1,2,3,4,5])

for i in range(5):
	print np.log(a[i]), np.log(b[i])
