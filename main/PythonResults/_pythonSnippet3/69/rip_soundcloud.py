import requests, re, sys

def rip_soundcloud(url):
  request = requests.get(url)
  streamurl = re.search('"streamUrl":"([^"]+)"',request.text).group(1)
  title = re.search('"title":"([^"]+)"',request.text).group(1)
  f = open(title + ".mp3","w")
  request = requests.get(streamurl)
  f.write(request.content)
  f.close()

for arg in sys.argv:
  if not "http" in arg: continue
  try:
    rip_soundcloud(arg)
  except:
    print "Failed to rip input \"%s\"" % arg