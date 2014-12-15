__author__ = 'prateek'
import os
import urllib2
import re


from repositoryFilter import generateGitHubMatchList
from mossResults import mossDiff
from percentageExtractor import getPercentage

def downloadAndProcess(repoList,dir,language):
    #os.makedirs("FilesBackup")
    for i in range(0,len(repoList)):
        os.makedirs(dir+"/"+str(i))
        repoInfo = repoList[i]
        file_name = repoInfo['file_name']
        file_name = re.sub('[!@#$~]','',file_name)
        file_name = file_name.replace(" ","")
        file_raw_loc = repoInfo['file_raw_loc']
        repo_name = repoInfo['repos_link']

        print "Processing complete(%) :"+str(i*100/len(repoList))+"%"
        response = urllib2.urlopen(file_raw_loc)
        result = response.read()
        response.close()
        file = dir+"/"+str(i)+"/"+file_name
        f= open(file,"w")
        f.write(result)
        f.close()
        ##Replace the SubstractDate.java with the complete source snippet file.
        moss_result = mossDiff(language,dir+"/Source."+language,file)
        mossURL = moss_result.split("\n")[5]
        repoInfo['result']= mossURL
        repoInfo['percentage']=int(getPercentage(mossURL)[1:-2])
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
        print "=================================="
        print "Searching for snippet on GitHub..."
        print "=================================="

        repoList =executeMatching(snippet['snippet_id'],snippet['keywords'],snippet['language'],snippet['content'])
        print "Results for the snippet "
        print "========================"
        result = {}
        #for repos in repoList:
        #    print repos['repos_link'],repos['percentage']+"%"
        newlist = sorted(repoList, key=lambda k: k['percentage'],reverse=True)
        file = open("_"+snippet['snippet_id']+"/_results",'w')
        match_count=0;
        percentageRange= [0 for i in range(0,10)]
        for repos in newlist:
            if int(repos['percentage'])!=0:
                match_count+=1
            print repos['repos_link'],str(repos['percentage'])+"%"
            percentageRange[repos['percentage']/10]+=1
            file.write(str(repos['percentage'])+"% "+repos['repos_link']+" "+repos['file_name']+"\n")

        result['repos']= newlist
        result['match_count']=match_count
        result['total_repos']= len(newlist)
        file.write("match count "+str(match_count)+" ")
        file.write("\ntotal repos "+str(len(newlist))+"\n\n")
        for i in range(0,len(percentageRange)):
            file.write("Repos with percentage match in range"+ str(i*10)+"-"+str(i*10+10)+"% "+ str(percentageRange[i])+"\n")

        file.close()
        return result


def analytics(resultArray):
    percentageRange= [0 for i in range(0,10)]

    for result in resultArray:
        match_count= result['match_count']
        for rep in result['repos']:
            percentageRange[rep['percentage']/10]+=1

    for i in range(0,len(percentageRange)):
        print "Repos with percentage match in range", i*10,"-",i*10+10,"%", percentageRange[i]
        


def main():
    snippetInfo = open("Snippets/snippetsSourceInfo.txt")
    info = snippetInfo.readlines()
    id=0
    resultArray=[]
    for snip in info:
        id,language,src,keywords= snip.replace("\n","").split(':')
        snippet ={}
        snippet['src']=src
        snippet['keywords']= keywords.split(" ")
        #add moss key as an attribute
        #have another property in config for githubkey
        snippet['language']= language
        snippet['content']=  open(src).read()
        snippet['snippet_id']= language+"Snippet"+str(id)
        # final result Array...Analyze results from this object
        resultArray.append(snippetSpecificTasks(snippet))
        analytics(resultArray)

main()

