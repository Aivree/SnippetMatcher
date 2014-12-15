import requests
from os import environ

def img_downloader(pst, picnum, subreddit=None):
    if ('jpeg' in pst['data']['url'] or 
        'jpg' in pst['data']['url'] or 
        'png' in pst['data']['url']):
        picnum += 1
        if subreddit:
            try:
                with open(environ['HOME'] + '/Pictures/%s' % picnum + subreddit, 'wb+') as f:
                    pic = requests.get(pst['data']['url'], timeout=2.0)
                    f.write(pic.content)
            except requests.exceptions.Timeout:
                print 'Connection Timed Out!'
        else:
            try:
                with open(environ['HOME'] + '/Pictures/%s' % picnum, 'wb+') as f:
                    pic = requests.get(pst['data']['url'], timeout=2.0)
                    f.write(pic.content)
            except requests.exceptions.Timeout:
                print 'Connection Timed Out!'
    return picnum
