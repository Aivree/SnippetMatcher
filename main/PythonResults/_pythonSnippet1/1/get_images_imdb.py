#!/usr/bin/python
import json, requests
import pickle
import shutil

def saveCover(id, url):
  r = requests.get(url, stream=True)
  result = None
  if r.status_code == 200:
    with open('../images/'+id+'.jpg', 'wb') as f:
        r.raw.decode_content = True
        shutil.copyfileobj(r.raw, f)
    print id

  else:
    print "There's something wrong! Expected code 200, got " + str(r.status_code) + "."

  return result

def main():
  "get images from all imdb_movies.pickle coverlinks"

  movies = {}

  f = open('imdb_movies.pickle','r')
  src_movies = pickle.load(f)
  f.close()

  keys = src_movies.keys()
  keys.sort()

  for i in keys:
    m = src_movies[i]
    url = m['Cover_Url']
    saveCover(i,url)

if __name__ == "__main__":
  main()
