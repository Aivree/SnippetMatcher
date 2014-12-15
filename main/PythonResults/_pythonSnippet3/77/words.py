import requests
import simplejson 
word_site = "http://svnweb.freebsd.org/csrg/share/dict/words?view=co&content-type=text/plain"

response = requests.get(word_site)
words = response.content.splitlines()
print words

f=open('list.txt','w')
x=len(words)/2
for i in range(x):
    f.write(words[i]+'\n')
f.close()
f=open('list2.txt','w')
for i in range(x,len(words)):
    f.write(words[i]+'\n')
f.close()
