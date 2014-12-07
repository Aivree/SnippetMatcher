__author__ = 'prateek'

import subprocess


def mossDiff(language,source,dest):

    cmd ="./moss.pl -l "+language+" "+source+" "+dest
    print "Moss Executing: "+cmd
    args = cmd.split()

    pipe = subprocess.Popen(args,stdout=subprocess.PIPE).communicate()[0]
    return pipe

#ans =mossDiff('java','SubstractDate.java','TimeZoneTest.java')


