import urllib2
import requests
import json

my_file = raw_input('Nama File nya : ')

api = {
	'api_key' : '54fb7f112acac8ff2f987db648f729f5',
}

filenya = {'file' : open(my_file, 'rb')}
r = requests.post('http://www.unphp.net/api/v2/post', files=filenya, data=api)
r.text

data = r.json()

out = data['output']

r = requests.get(out)
r.content

outfile = open('hasil.txt', 'w')
outfile.write(r.text)
outfile.close()

print " Silahkan Buka file : hasil.txt"