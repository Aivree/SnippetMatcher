import requests

baseURL = "https://www.googleapis.com/youtube/v3/videos?id="
API_KEY = 'AIzaSyC9ymJowQNoLdgh1kW8aPwiL8wbLRLbKy0'
parameters = '&part=statistics'
fp = open('spoovideoStats.txt','a+')
f = open('spooyoutubeID.txt','r')
for line in f:
	finalURL = baseURL + line + "&key=" + API_KEY + parameters
	print finalURL
	r = requests.get(finalURL)
	fp.write(r.text)
	fp.write("$$$$\n")
