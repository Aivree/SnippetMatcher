import requests


f=open("soundcloud.txt","w+")
n=1
url1= 'http://api.soundcloud.com/tracks/'
url2='.json?client_id=69990771536e3a491fb7933cc8d7c949'

while 1:
	try:
		url=url1+str(n)+url2
		print "user fetch - ID -",n
		r = requests.get(url)
		f.write(r.text)
		n+=1
	except:
		print "exception occured"
		n+=1
		
f.close()
