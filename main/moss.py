__author__ = 'prateek'
import os
import urllib2


from urluse import generateGitHubMatchList

def download_files(repoList):
    os.makedirs("FilesBackup")
    for i in range(0,len(repoList)):
        os.makedirs("FilesBackup/"+str(i))
        repoInfo = repoList[i]
        file_name = repoInfo['file_name']
        file_raw_loc = repoInfo['file_raw_loc']
        repo_name = repoInfo['repos_link']

        print "downloading file"
        response = urllib2.urlopen(file_raw_loc)
        result = response.read()
        file = "FilesBackup/"+str(i)+"/"+file_name
        f= open(file,"w")
        f.write(result)


keywords=['Timezone','getAvailableIds','getTimeZone','getOffset','getRawOffset']
language= 'java'
repoList =generateGitHubMatchList(keywords,language)
download_files(repoList)


