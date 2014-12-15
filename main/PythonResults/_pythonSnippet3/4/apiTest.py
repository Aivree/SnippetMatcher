#-*- coding:utf-8 -*-
import json
import requests


#返回二维码文件
r = requests.get('http://127.0.0.1:8000/api/getqr/')
f = open("./2.gif","wb")
f.write(r.content)

#print r


