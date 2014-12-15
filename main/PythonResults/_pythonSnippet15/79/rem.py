#!/usr/bin/env python

import numpy as NP

M = NP.array([[1,2,3,4,5],
              [2,3,2,5,3],
              [5,5,5,3,2]])

R = NP.array([[3,3,3,3,3],
             [3,3,3,3,3],
             [4,4,4,4,4]])

A = M - R
print A


B = NP.array([[-2.0/3, 0, 0, 2.0/3, 0],
            [-2.0/3, 0, 0, 2.0/3, 0],
            [-2.0/3, 0, 0, 2.0/3, 0]])

C = A - B
print C
