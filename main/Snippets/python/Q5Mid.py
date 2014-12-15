from scipy import *
from operator import itemgetter

n = 5  # how many fingers are we looking for

d = loadtxt("paw.txt")
width, height = d.shape

# Create an array where every element is a sum of 2x2 squares.

fourSums = d[:-1,:-1] + d[1:,:-1] + d[1:,1:] + d[:-1,1:]

# Find positions of the fingers.

# Pair each sum with its position number (from 0 to width*height-1),

pairs = zip(arange(width*height), fourSums.flatten())

# Sort by descending sum value, filter overlapping squares

def drop_overlapping(pairs):
    no_overlaps = []
    def does_not_overlap(p1, p2):
        i1, i2 = p1[0], p2[0]
        r1, col1 = i1 / (width-1), i1 % (width-1)
        r2, col2 = i2 / (width-1), i2 % (width-1)
        return (max(abs(r1-r2),abs(col1-col2)) >= 2)
    for p in pairs:
        if all(map(lambda prev: does_not_overlap(p,prev), no_overlaps)):
            no_overlaps.append(p)
    return no_overlaps

pairs2 = drop_overlapping(sorted(pairs, key=itemgetter(1), reverse=True))

# Take the first n with the heighest values

positions = pairs2[:n]

# Print results

print d, "\n"

for i, val in positions:
    row = i / (width-1)
    column = i % (width-1)
    print "sum = %f @ %d,%d (%d)" % (val, row, column, i)
    print d[row:row+2,column:column+2], "\n"