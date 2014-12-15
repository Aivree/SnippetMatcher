#/usr/bin/python
import requests,sys
def DownImage(url,filename):
    r = requests.get(url)
    try:
        ImageFile = open(filename,'w')
        ImageFile.write(r.content)
        ImageFile.close()
        return (0,"Success")
    except:
        return (1,"Write imagefile Error!")
