import requests
import time
import os

def get_urls_ab():
    open_file = open('fn.txt', 'w')
    for i in xrange(1,78):
        name = 'http://www.foodnetwork.com/chefs/alton-brown/recipes.mostpopular.page-'+str(i)+'.html'
        r = requests.get(name)
        open_file.write(r.text+"\n")
    
        time.sleep(30)

    os.system("./extractrecipeurls.scr")


def main():
        get_urls_ab()

if __name__=="__main__":
    main()

