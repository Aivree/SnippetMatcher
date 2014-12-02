__author__ = 'prateek'
import urllib2
import re
from BeautifulSoup import BeautifulSoup

def extractRepositoriesFromQueryPage(url_path):
    response = urllib2.urlopen(url_path)
    html= response.read()
    soup = BeautifulSoup(html)
    soup.prettify()
    searchResults = str(soup.find(id="code_search_results"))
    list = searchResults.split('class="title"')
    list.pop(0)
    repoList=[]
    print "Fetching data from github, please wait..."
    for l in list:
        useful = l.split("<br />")[0]
        #print useful
        searchForRepo = re.search(".*href=\"(.*)\"",useful)
        if searchForRepo:
            repoLink= searchForRepo.group(1)
            repoList.append("https://www.github.com"+repoLink)

    return repoList

    #print type(soup1)
    #links = soup1.find_all('a')
    #print links
def generateGitHubMatchList(keywords):
    repolist=[]
    query = keywords[0]
    keywords.pop(0)
    for key in keywords:
        query= query+"+"+key
    for k in range(1,10):
        url_path='https://github.com/search?p='+str(k)+'&q='+query+'+language%3Ajava&type=Code&utf8=%E2%9C%93'
        repolist =repolist+ extractRepositoriesFromQueryPage(url_path)
    for i in repolist:
        print i

keywords=['Timezone','getAvailableIds','getTimeZone','getOffset','getRawOffset']
generateGitHubMatchList(keywords)