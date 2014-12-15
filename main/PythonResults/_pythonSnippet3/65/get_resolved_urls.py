# Access the urls in tweets and get the real urls

import requests

f = open("urls_fromsql_part3.txt", 'r')
fw = open("realurls_part3.txt", 'w+')
counter = 0

for line in f:
	l = line.split(',')
	id = int(l[0])
	url = l[1]
	if id % 100 == 0:
		print id
	try:
		r = requests.get(url)
		output = str(id) + ',' + r.url +'\n'
		fw.write(output)
	except Exception, e:
		output = str(id) + ', None ,' + str(e) +'\n'
		fw.write(output)
		print e
		continue
	
con.close()
