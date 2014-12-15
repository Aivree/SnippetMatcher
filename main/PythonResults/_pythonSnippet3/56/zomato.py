import requests
from lxml import html
import requests 
url = 'https://www.zomato.com/ncr/restaurants?page='

for i in range(143,500):

	page = requests.get(url+str(i),verify= True)
	tree = html.fromstring(page.text)
	f = open("urls.txt","a", encoding= 'utf8')
	res = tree.xpath("//a[@class='result-title']/@href")
	print(str(i) + " ----- " + str(len(res)))
	for each in res: 
		f.write(each)
		f.write('\n')
	f.close()
