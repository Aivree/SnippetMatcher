# -*- coding: utf-8 -*-
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from myproject.myapp.models import Document
from myproject.myapp.forms import DocumentForm

from myproject.myapp.models import Image
from myproject.myapp.forms import ImageForm

import time

def listDoc(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'], upload_date = "2008-05-13")
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('myproject.myapp.views.listDoc'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'myapp/list.html',
        {'documents': documents, 'form': DocumentForm},
        context_instance=RequestContext(request)
    )
 
def listImages(request):
    # Handle file upload
    if request.method == 'POST':
        form = ImageForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Image(imagefile = request.FILES['imagefile'], upload_date = "2008-05-14")
            newdoc.save()

            return HttpResponseRedirect(reverse('myproject.myapp.views.listImages'))
    else:
        form = ImageForm() # A empty, unbound form

    # Load documents for the list page
    images = Image.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'myapp/list.html',
        {'images': images, 'form': ImageForm},
        context_instance=RequestContext(request)
    )
