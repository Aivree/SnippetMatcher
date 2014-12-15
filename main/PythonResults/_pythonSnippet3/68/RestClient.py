import requests
from requests.auth import HTTPBasicAuth


class RestClient:

    def doGet(self, connURL, respFile, user, password):

        r = requests.get(connURL, auth=HTTPBasicAuth(user, password))
        html = r.content
        respFile.write(html)
        respFile.seek(0)
        return respFile

    def doPost(self, connURL, params, requestFile, respFile):
        if(requestFile != ""):
            files = {"file": open(requestFile, 'rb+')}
        r = requests.post(connURL, data=params, files=files)
        respFile.write(r.content)
        respFile.seek(0)
        return respFile




