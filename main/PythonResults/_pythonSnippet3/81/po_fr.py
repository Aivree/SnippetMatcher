import requests
import sys

lang = sys.argv[1]

payload = {'api_token': 'b682d7334f35c54b905c6de0c32c46eb', 'action': 'export', 'id':'3553', 'language': lang, 'type':'mo'}
r = requests.post("http://poeditor.com/api/", data=payload)
rr = requests.get(r.json['item'])

with open('/home/farciarz/dynares/wsgi/openshift/locale/%s/LC_MESSAGES/django.mo'%lang, 'wb') as f:
    f.write(rr.content)
print 'done mo'

payload = {'api_token': 'b682d7334f35c54b905c6de0c32c46eb', 'action': 'export', 'id':'3553', 'language': lang, 'type':'po'}
r = requests.post("http://poeditor.com/api/", data=payload)
rr = requests.get(r.json['item'])


with open('/home/piotr/dynares/wsgi/openshift/locale/%s/LC_MESSAGES/django.po'%lang, 'wb') as f:
    f.write(rr.content)
print 'done po'


