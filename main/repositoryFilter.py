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
        repoInfo={}
        links = l.split("a href=\"")
        useful = links[1]
        #print useful
        searchForRepo = re.search("(.*)\"",useful)
        fileRegEx = re.search("(.*)\" title=(.*)>(.*)</a>",links[2])
        if fileRegEx:
            file_blob_location= fileRegEx.group(1)
            index= file_blob_location.find("/blob")
            #generate the raw content URL by removing the blob
            file_raw_loc = "https://raw.githubusercontent.com"+file_blob_location[:index]+file_blob_location[index+5:]
            #print "raw file location",file_raw_loc
            file_name= fileRegEx.group(3)
            repoInfo['file_raw_loc']=file_raw_loc
            repoInfo['file_name']=file_name
        if searchForRepo:
            repoLink= searchForRepo.group(1)
            #print "name of the repo:", repoLink
            repoInfo['repos_link']="https://www.github.com"+repoLink
            #repoList.append("https://www.github.com"+repoLink)
        repoList.append(repoInfo)
    return repoList

    #print type(soup1)
    #links = soup1.find_all('a')
    #print links
def generateGitHubMatchList(keywords,language):
    repolist=[]
    query = keywords[0]
    keywords.pop(0)
    for key in keywords:
        query= query+"+"+key
    for k in range(1,2):
        url_path='https://github.com/search?p='+str(k)+'&q='+query+'+language%3A'+language+'&type=Code&utf8=%E2%9C%93'
        print url_path
        repolist =repolist+ extractRepositoriesFromQueryPage(url_path)
    for i in repolist:
        print i['file_raw_loc']
    return repolist


