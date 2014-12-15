import requests

payload = {
    'session_key': username,
    'session_password': Password
}

with requests.Session() as s:
    s.post('https://www.linkedin.com/', data=payload)
    r=s.get('https://www.linkedin.com/vsearch/p?f_CC=3003796')
    html= open('testtest.html', 'w')
    html.write(r.content)
    html.close()