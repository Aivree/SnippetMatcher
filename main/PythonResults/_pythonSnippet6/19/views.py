# -*- encoding: utf-8 -*-

# django
from django.core.context_processors import csrf
from django.http import *
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.core.files.base import ContentFile
from django.core.files import File 



# 3rd Party
from PIL import Image

# project
from guest.models import *
from guest.forms import *


def list_picture(request):
	pictures = Picture.objects.all()
	variables = RequestContext(request, {
		'pictures': pictures
	})
	return render_to_response('guest/list.html', variables)

def add_picture(request):
	if request.method == 'POST':
		form = AddPictureForm(request.POST, request.FILES, label_suffix='')
		if form.is_valid():
			data = form.clean()

			picture = Picture.objects.create(
				picture_data=data['picture_data'],
			)
			picture.make_thumbnail()
	else:
		form = AddPictureForm(label_suffix='')

	variables = RequestContext(request, {
		'form': form,
	})
	return render_to_response('guest/add.html', variables)

import zipfile

def fileiterator(zipf):
	with zipfile.ZipFile(zipf, "r", zipfile.ZIP_STORED) as openzip:
		filelist = openzip.infolist()
		for f in filelist:
			yield(f.filename, openzip.read(f))


def add_picture_zip(request):
	if request.method == 'POST':
		form = UploadFileForm(request.POST, request.FILES, label_suffix='')
		if form.is_valid():
			for filename,content in fileiterator(request.FILES['file']):
				cf = ContentFile(content)
				picture = Picture()
				picture.picture_data.save(filename, cf)
				picture.make_thumbnail()
	else: 
		form = UploadFileForm(label_suffix='')

	variables = RequestContext(request, {
		'form': form,
	})
	return render_to_response('guest/add_zip.html', variables)
