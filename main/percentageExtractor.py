__author__ = 'prateek'
import urllib2
import re
from BeautifulSoup import BeautifulSoup

def getPercentage(url):
    response = urllib2.urlopen(url)
    result =response.read()
    soup= BeautifulSoup(result)
    soup.prettify()
    k=str(soup.findAll('table'))
    #print k
    soup =BeautifulSoup(k)
    soup.prettify()
    kDest = soup.findAll('a')
    if len(kDest)>0:
        #print kDest[0].contents[0].split(" ")[1]
        return kDest[0].contents[0].split(" ")[1]
    else:
        return "(0%)"

#getPercentage("http://moss.stanford.edu/results/17994894/")
#getPercentage("http://moss.stanford.edu/results/532102490/")
#match1 = re.search("<a href=\"(.*)\"(.*)")
#if match1:
#    source
#list =k.split("a href=\"")
#print list[1].split("\"")[0]
#print list[2].split("\"")[2]
