# -*- coding: utf-8 -*-
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from myproject.myapp.models import Document, Image
from myproject.myapp.forms import DocumentForm, ImageForm

# confirmation comment

def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        art = ImageForm(request.POST, request.FILES)

        if form.is_valid() and art.is_valid():
            print "poopoerz"
            newimg = Image(image = request.FILES['image'])
            newimg.save()
            print "image saved"
            newdoc = Document()

            newdoc.user = request.user.username
            print newdoc.user
            # newdoc = Document(docfile= request.FILES['docfile'], genre=request.POST['genre'], title=request.POST['title'], up=request.POST['up'], down=request.POST['down'], img = newimg)
            newdoc.docfile = request.FILES['docfile']
            newdoc.genre = request.POST['genre']
            newdoc.title = request.POST['title']
            newdoc.up = request.POST['up']
            newdoc.down = request.POST['down']
            newdoc.img = newimg
            newdoc.save()



            print "HERE"
            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('myproject.myapp.views.list'))
    else:
        form = DocumentForm() # A empty, unbound form
        art = ImageForm()
    # Load documents for the list page
    documents = Document.objects.all()
    images = Image.objects.all()


    # Render list page with the documents and the form
    return render_to_response(
        'myapp/list.html',
        {'documents': documents, 'images':images, 'form': form, 'art': art},
        context_instance=RequestContext(request)
    )
