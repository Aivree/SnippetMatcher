# ex3.2b.py - Downloading  web page

import requests

r = requests.get("http://www.python.org/")

# write the content to tesr_request.htm
with open("test_request.html","wb") as code:
	code.write(r.content)