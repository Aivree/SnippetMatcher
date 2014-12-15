import requests
url="http://www.issilissinew.com/Downloads/Legend%20(2014)%20~320Kbps.zip"

r=requests.get(url)
output=open('Legend.zip','wb')
output.write(r.content)
output.close()


#function to download largefile
def download_file(url):
    local_filename = url.split('/')[-1]
    # NOTE the stream=True parameter
    r = requests.get(url, stream=True)
    with open(local_filename, 'wb') as f:
        for chunk in r.iter_content(chunk_size=1024): 
            if chunk: # filter out keep-alive new chunks
                f.write(chunk)
                f.flush()
    return local_filename
