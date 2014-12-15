# -*- coding: utf-8 -*- 
'''
Created on 2013-3-20
@author: feiyuliu
在指定的网页上爬取图片
有点小问题，容易出现Caused by <class 'socket.error'>: [Errno 10061]
'''
import requests
import lxml.html
#取得html源文件
page = requests.get('http://tieba.baidu.com/p/2222403226').text
#获得DOM
doc = lxml.html.document_fromstring(page)
for idx, el in enumerate(doc.cssselect('img.BDE_Image')):
    with open('%03d.jpg' % idx, 'wb') as f:
        f.write(requests.get(el.attrib['src']).content)