# -*- coding: utf-8 -*-
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from myproject.myapp.models import Document, Image, Demo
from myproject.myapp.forms import DocumentForm, ImageForm

# confirmation comment

def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        art = ImageForm(request.POST, request.FILES)
        if form.is_valid() and art.is_valid():
            print "poopoerz"
            newdoc = Document(docfile = request.FILES['docfile'], genre=request.POST['genre'], title=request.POST['title'])
            newimg = Image(image = request.FILES['image'])
            newdoc.save()
            newimg.save()
            newdemo = Demo(doc = newdoc, img = newimg)
            newdemo.save()
            print  "HERE"
            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('myproject.myapp.views.list'))
    else:
        form = DocumentForm() # A empty, unbound form
        art = ImageForm()
    # Load documents for the list page
    documents = Document.objects.all()
    images =  Image.objects.all()


    # Render list page with the documents and the form
    return render_to_response(
        'myapp/list.html',
        {'documents': documents, 'images':images, 'form': form, 'art': art},
        context_instance=RequestContext(request)
    )
