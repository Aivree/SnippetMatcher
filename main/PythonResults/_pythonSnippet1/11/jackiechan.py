import requests
import shutil
import argparse
import sys
from bs4 import BeautifulSoup




requests.packages.urllib3.disable_warnings()

def parseargs():
	"""
		Parses arguments given to script.
	"""
	description="This script iterates over given list of IP addresses to find those that respond with HTTP 401 Authorization request. It then tries to use username:password pairs to pass the authentication."
	parser = argparse.ArgumentParser()
	parser.add_argument('-m', "--mute", action='store_true', default=False, help='Just shut up and download. Virtually silent.')
	parser.add_argument('-t', "--threads", action='store_true', default=False, help='Do not show threads urls while parsing.\n')
	parser.add_argument('-i', "--img", action='store_true', default=False, help='Do not show vertical bar for each image downloaded.\n')
	parser.add_argument('-s', "--summary", action='store_true', default=False, help='Do not show summary after download completes.')
	parser.add_argument('-w', "--writedirectory", metavar='destination', default=None, help='Override path to destination directory.\n')
	parser.add_argument('-d', "--dbase", metavar='dbase', default=None, help='Override path to database file.\n')
	parser.add_argument('-u', "--url", metavar='main_url', default=None, help='Override main url. Usefull when you want to download something else than /hr.\n')
	args = parser.parse_args()
	return args


def get_threads(url, suburls):
	"""Gets unique threads urls for further parsing."""
	threads = []
	if args.mute == False: print("[P] Collecting thread URLs.")
	for suburl in suburls:
		local_url = url + suburl + "/"
		if args.mute == False: print("[P] Page: [" + suburl + "] ")
		sys.stdout.flush()
		page = requests.get(local_url, verify=False)
		text = page.text
		soup = BeautifulSoup(text)
		for link in soup.find_all('a'):
			if "thread" in link.get('href')[:14] and "#" not in link.get('href'):
				t = link.get('href')[:14]
				if t not in threads:
					threads.extend([t])
	threads.sort()
	return threads

def get_links(url, threads):
	"""Gets unique links to observed images for final downloading."""
	links = []
	for thread in threads:
		thread_url = url + thread + "/"
		if args.threads == False and args.mute == False: print("[A] Searching thread:", thread.split("/")[1], end="\t")
		page = requests.get(thread_url, verify=False)
		text = page.text
		soup = BeautifulSoup(text)
		counter = 0
		for link in soup.find_all('a'):
			if "4cdn.org" in link.get('href') and link.get('href') not in links:
				links.extend([link.get('href')])
				counter+=1
		if args.threads == False and args.mute == False: print("Found", counter, "links." )
	links.sort()
	return links

def db_verify(links, db_file):
	"""Verifies database."""
	previous_links = []
	download_links = []
	db_read = open(db_file, "r")
	for link in db_read:
		previous_links.extend([link])
	if args.mute == False: print("[i] DB size: ", len(previous_links))
	for link in links:
		if link not in previous_links:
			download_links.extend([link])
	if args.mute == False: print("[i] New links: ", len(download_links))
	return download_links

def download_images(download_links, destination_directory):
	"""Download images."""
	counter = 0
	if args.img == False and args.mute == False: print("["+ chr(25) +"] ", end="")
	sys.stdout.flush()
	for image in download_links:
		i = requests.get("https:"+image, stream=True, verify=False)
		counter+=1
		if counter%10 == 0 and counter <= len(download_links) :
			if args.img == False and args.mute == False: print("|", end="\n["+ chr(25) +"] ")
			sys.stdout.flush()
		else:
			if args.img == False and args.mute == False: print("|", end="")
			sys.stdout.flush()
		if i.status_code == 200:
			with open(destination_directory + image.split("/")[4], 'wb') as f:
				i.raw.decode_content = True
				shutil.copyfileobj(i.raw, f)

def update_db(download_links, db_file):
	db_write = open(db_file, "a")
	for image in download_links:
		db_write.write(image + "\n")
	db_write.close()
	if args.mute == False: print("[i] DB Updated.")

def main():
	global args

	db_file = "jackiechan.db"
	main_url = "https://boards.4chan.org/hr/"
	#sub_urls = ["", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
	sub_urls = ["2"]

	destination_directory = ""

	# parsing arguments
	args = parseargs()

	#Namespace(dbase=None, img=False, mute=False, summary=False, threads=False, url=None, writedirectory=None)

	if args.dbase != None:
		db_file = args.dbase
	if args.url != None:
		main_url = args.url
	if args.writedirectory != None:
		destination_directory = args.writedirectory

	# get threads
	threads = get_threads(main_url, sub_urls)

	# get links to images
	links = get_links(main_url, threads)

	# verify with database
	download_links = db_verify(links, db_file)

	# download images
	download_images(download_links, destination_directory)

	# update database
	update_db(download_links, db_file)

if __name__ == "__main__":
	main()

