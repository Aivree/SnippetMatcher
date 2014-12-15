from celery import task
import requests
from django.conf import settings

@task
def download_image(image):
    """Downloads the image asynchronously and saves it into the 'images' folder"""
    local = settings.DOWNLOAD_IMAGE_ROOT + '/'
    url = image.url
    thumbnail_url = image.thumbnail_url
    # download image
    local_image = open(local+image.hash+image.ext, 'wb')
    local_image.write(requests.get(url).content)
    local_image.close()
    # download thumbnail_image
    local_image = open(local+image.hash+'b.jpg', 'wb')
    local_image.write(requests.get(thumbnail_url).content)
    local_image.close()

