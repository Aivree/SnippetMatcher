# -*- coding: utf-8 -*-
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from myproject.imageapp.models import Document
from myproject.imageapp.forms import DocumentForm

def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('myproject.imageapp.views.list'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    document = Document.objects.order_by('pk').reverse()[0]

    # Render list page with the documents and the form
    return render_to_response(
        'imageapp/list.html',
        {'document': document, 'form': form},
        context_instance=RequestContext(request)
    )

def thumbnails(request):
    # Load documents for the list page
    documents = Document.objects.order_by('pk').reverse()

    # Render list page with the documents and the form
    return render_to_response(
        'imageapp/thumbnails.html',
        {'documents': documents}
    )

def details(request, doc_id):
    try:
        doc = Document.objects.get(pk=doc_id)
    except Document.DoesNotExist:
        raise Http404
    return render_to_response('imageapp/details.html', {'doc': doc})
