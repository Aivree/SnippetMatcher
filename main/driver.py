__author__ = 'prateek'
import os
import urllib2


from repositoryFilter import generateGitHubMatchList
from mossResults import mossDiff
from percentageExtractor import getPercentage

def downloadAndProcess(repoList,dir,language):
    #os.makedirs("FilesBackup")
    for i in range(0,len(repoList)):
        os.makedirs(dir+"/"+str(i))
        repoInfo = repoList[i]
        file_name = repoInfo['file_name']
        file_raw_loc = repoInfo['file_raw_loc']
        repo_name = repoInfo['repos_link']

        print "Processing complete(%) :"+str(i*100/len(repoList))+"%"
        response = urllib2.urlopen(file_raw_loc)
        result = response.read()
        file = dir+"/"+str(i)+"/"+file_name
        f= open(file,"w")
        f.write(result)
        f.close()
        ##Replace the SubstractDate.java with the complete source snippet file.
        moss_result = mossDiff(language,dir+"/Source."+language,file)
        mossURL = moss_result.split("\n")[5]
        repoInfo['result']= mossURL
        repoInfo['percentage']=getPercentage(mossURL)[1:-2]
    return repoList



def executeMatching(snippetId,keywords,language,content):
    #dir = "_"+language+"/"+snippetId
    dir = "_"+snippetId
    os.makedirs(dir)
    source = open(dir+"/Source."+language,'w')
    source.write(content)
    source.close()
    repoList =generateGitHubMatchList(keywords,language)
    #returns repoList with result per repository
    repoList=downloadAndProcess(repoList,dir,language)
    return repoList

def snippetSpecificTasks(snippet):
        print "Searching for snippet on GitHub..."
        repoList =executeMatching(snippet['snippet_id'],snippet['keywords'],snippet['language'],snippet['content'])
        print "Results for the snippet "
        print "========================"
        #for repos in repoList:
        #    print repos['repos_link'],repos['percentage']+"%"
        newlist = sorted(repoList, key=lambda k: k['percentage'],reverse=True)
        file = open("_"+snippet['snippet_id']+"/_results",'w')
        for repos in newlist:
            print repos['repos_link'],repos['percentage']+"%"
            file.write(repos['repos_link']+" "+repos['file_name']+" "+repos['percentage']+"%\n")
        return newlist




def main():
    snippetInfo = open("Snippets/snippetsSourceInfo.txt")
    info = snippetInfo.readlines()
    id=0
    resultArray=[]
    for snip in info:
        id+=1
        language,src,keywords= snip.replace("\n","").split(':')
        snippet ={}
        snippet['src']=src
        snippet['keywords']= keywords.split(" ")
        snippet['githubkey']=language
        #add moss key as an attribute
        #have another property in config for githubkey
        snippet['language']= language
        snippet['content']=  open(src).read()
        snippet['snippet_id']= "Snippet"+str(id)
        # final result Array...Analyze results from this object
        resultArray.append(snippetSpecificTasks(snippet))

main()

