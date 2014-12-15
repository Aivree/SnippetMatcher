'''NumLib Tensor Library'''

from _tensor import *
from _tensor_exp import *

# Conversion functions to/from numpy
# NOTE: These functions are a bit ad-hoc. More elegant
# interoperability between numlib and numpy types may be expored in
# the future.

import numpy as np

def numpyFromScalarField1D(scalarField1D):

	n = len(scalarField1D)
	numpyArray = np.empty((n,), dtype=float)
	
	for i in range(n):
		numpyArray[i] = scalarField1D[i]

	return numpyArray

def scalarField1DFromNumpy(numpyArray):

	n = numpyArray.shape[0]
	scalarField1D = ScalarField1D(n)

	for i in range(n):
		scalarField1D[i] = numpyArray[i]

	return scalarField1D

def numpyFromVectorField1D(vectorField1D):

	n = len(vectorField1D)
	numpyArray = np.empty((3,n), dtype=float)

	for i in range(n):

		u = vectorField1D[i]
		numpyArray[0,i] = u[1]
		numpyArray[1,i] = u[2]
		numpyArray[2,i] = u[3]

	return numpyArray

def vectorField1DFromNumpy(numpyArray):

	n = numpyArray.shape[1]
	vectorField1D = VectorField1D(n)

	for i in range(n):
		vectorField1D[i] = TensorR1(numpyArray[0,i], \
				   				  numpyArray[1,i], \
				                  numpyArray[2,i])

	return vectorField1D

def numpyFromTensorField1D(tensorField1D):

	n = len(tensorField1D)
	numpyArray = np.empty((3,3,n), dtype=float)

	for i in range(n):

		u = tensorField1D[i]

		numpyArray[0,0,i] = u[1,1]
		numpyArray[0,1,i] = u[1,2]
		numpyArray[0,2,i] = u[1,3]

		numpyArray[1,0,i] = u[2,1]
		numpyArray[1,1,i] = u[2,2]
		numpyArray[1,2,i] = u[2,3]

		numpyArray[2,0,i] = u[3,1]
		numpyArray[2,1,i] = u[3,2]
		numpyArray[2,2,i] = u[3,3]

	return numpyArray

def tensorField1DFromNumpy(numpyArray):

	n = numpyArray.shape[2]
	tensorField1D = TensorField1D(n)

	a = numpyArray #alias

	for i in range(n):

		tensorField1D[i] = TensorR2(a[0,0,i], a[0,1,i], a[0,2,i],\
									a[1,0,i], a[1,1,i], a[1,2,i],\
									a[2,0,i], a[2,1,i], a[2,2,i])

	return tensorField1D
