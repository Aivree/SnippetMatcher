"""Hacky asset retrieval"""

import os
import requests

def get_resource(url):
    fname = url.replace('/','_')
    if os.path.exists(fname):
        return
    else:
        result = requests.get(url.content)
        f = open(fname, 'w')
        f.write(result)
        f.close()

