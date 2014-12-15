#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Create your views here.

import os, re
from settings import MEDIA_ROOT
from django.db import models

__author__ = 'johnb'

from django.shortcuts import render
from apps.searcher.forms import UploadForm,DocumentForm,InputMergeChoiceForm
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from apps.searcher.models import File

# Create your views here.
def get_api_endpoints(apiroot='', apiname='', endpoint='', objects='', fmt=''):
    import requests
    hostname = 'http://prodimages.ny.bluefly.com/'
    if not apiroot:
        apiroot = 'api/v1/'
    if not apiname:
        apiname = ''
    if not objects:
        objects = ''
    if not fmt:
        fmt = '?format=json'
    elif fmt:
        fmt = '?format={0}'.format(fmt)

    url = os.path.join(hostname,apiroot,apiname,endpoint,objects,fmt)
    r = requests.get(url).json()
    endpoints = r.keys()
    return endpoints, r


def upload_home(request):

    if request.method == "POST":
        form = UploadForm(request.POST, request.FILES)
        choiceform = DocumentForm(request.POST, request.FILES)

        if form.is_valid():
            form.save()
            return HttpResponseRedirect(reverse('fileupload'))
    else:
        form = UploadForm()
        choiceform = DocumentForm()


    files = File.objects.all()
    return render(request,'upload_home.html',{'form':form,
                                              'files':files,
                                              'choiceform':choiceform})



