__author__ = 'prateek'

import subprocess

#result = subprocess.check_output(["perl","moss.pl -l java SubstractDate.java TimeZoneTest.java"])
var = " "
var ="./moss.pl -l java TimeZoneTest.java SubstractDate.java"
args = var.split()

pipe = subprocess.Popen(args,stdout=subprocess.PIPE).communicate()[0]
print pipe
