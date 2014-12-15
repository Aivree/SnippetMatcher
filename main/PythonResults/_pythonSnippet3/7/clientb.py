#downloading and formatting a webpage
#webpagedl.py

import requests

r=requests.get("http://www.python.org/")

with open("test_requests.html","wb") as code:
	code.write(r.content)