from django.shortcuts import render_to_response
from django.http import HttpResponseRedirect
from django.template import RequestContext
from django.core.urlresolvers import reverse
from django.conf import settings
import os

from cloudyHome.shareFolder.models import Document
from cloudyHome.shareFolder.forms import DocumentForm

import os.path

def folder_content(request, url):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            if 'all_files' == url:
                path = ''
            else:
                path = url
            save_file(request.FILES['docfile'],path)
            return HttpResponseRedirect('/sharefolder/all_files/all_files/media/%s' % url)
    else:
        form = DocumentForm() 
    folder = url + '/'

    pa = settings.MEDIA_ROOT + folder
    dir_list = {'folders':[],'files':[]}
    for item in os.listdir(pa):
        if os.path.isdir(pa + item):
            dir_list['folders'].append((("/media/" + folder + item),item ))
        else:
            dir_list['files'].append((("/media/" + folder + item),item ))
   # Render list page with the documents and the form
    return render_to_response(
            'shareFolder/other_folder.html',
            { 'dir_list': dir_list, 'form': form, 'url': url },
            context_instance = RequestContext(request)
            ) 
    
def display_content(request, url):
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            if 'all_files' == url:
                path = ''
            else:
                path = url
            save_file(request.FILES['docfile'],path)
            return HttpResponseRedirect('/sharefolder/%s/' % url)
    else:
        form = DocumentForm() 
    if 'all_files' == url:
        folder = ''
    else:
        folder = url + '/'
    pa = settings.MEDIA_ROOT + folder
    dir_list = {'folders':[],'files':[]}
    for item in os.listdir(pa):
        if os.path.isdir(pa + item):
            dir_list['folders'].append((("/media/" + folder + item),item ))
        else:
            dir_list['files'].append((("/media/" + folder + item),item ))
   # Render list page with the documents and the form
    return render_to_response(
            'shareFolder/%s.html' % url,
            { 'dir_list': dir_list, 'form': form },
            context_instance = RequestContext(request)
            ) 

def save_file(file, path):
    filename = file._get_name()
    fd = open('%s/%s' % (settings.MEDIA_ROOT+path, str(path) + str(filename)), 'wb')
    for chunk in file.chunks():
        fd.write(chunk)
    fd.close()
