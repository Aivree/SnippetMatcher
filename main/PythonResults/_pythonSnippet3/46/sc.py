# -*- coding: UTF-8 -*-
__author__ = 'dubu9'
import requests

def fetch_page(url):
    r = requests.get(url)
    return r.text

with open("sample.html", 'w') as f:
    html = fetch_page("http://www.ted.com/talks/browse?page=2")
    f.write(html.encode('utf - 8'))



