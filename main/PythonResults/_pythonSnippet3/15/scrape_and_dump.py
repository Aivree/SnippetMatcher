import requests
import time

url = "http://appservices.citibikenyc.com/data2/stations.php"

def data_me_bro():
  name = str(int(time.time()))
  with open('/home/abe/citibike/data/' + name, 'w') as f:
    r = requests.get(url)
    f.write(r.content)

if __name__ == "__main__":
  data_me_bro()
