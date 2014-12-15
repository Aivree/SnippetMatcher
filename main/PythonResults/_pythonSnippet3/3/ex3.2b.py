import requests

r = requests.get("http://www.python.org/")

with open("tests_requests.html", "wb") as code:
	code.write(r.content)

	