import requests
r=requests.get("http://python.org")
with open("test_requests.html","wb") as code:
	code.write(r.content)