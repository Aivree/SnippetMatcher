#!/usr/bin/python

#demo of requests library - save an image file

import requests

r = requests.get('http://code.google.com/edu/languages/google-python-class/images/puzzle/a-baaa.jpg')

f = open('img0.jpg', 'w')

f.write(r.content)

f.close()
