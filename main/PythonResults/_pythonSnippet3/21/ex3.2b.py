import requests

r = requests.get("http://www.python.org/")

# write requests.get() output to file test_requests.html
# 'wb' stands for write binary, which downloads the raw bytes of the file

with open("test_requests.html", "wb") as code:
    code.write(r.content)
