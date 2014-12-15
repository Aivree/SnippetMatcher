# -*- encoding: utf-8 -*-
from django.core.exceptions import PermissionDenied
from django.http import HttpResponseRedirect, HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.core.urlresolvers import reverse

from pages.models import Document
from pages.forms import DocumentForm

import json

footer = "© 2013 Shogun Toolbox Foundation"


def index(request):
    """
    Renders homepage
    """
    c = {'title': "Homepage", 'footer': footer, 'request': request}

    return render_to_response('main.html', c, context_instance=RequestContext(request))


def upload(request):
    """
    File upload view
    """
    if not request.user.is_authenticated():
        raise PermissionDenied

    if request.is_ajax():
        return _upload_ajax(request)
    elif request.method == 'POST':
        # Handle file upload
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document.create(docfile=request.FILES['docfile'], user=request.user)
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('pages.views.upload'))
    else:
        form = DocumentForm()   # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'upload.html',
        {'request': request, 'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )


def _upload_ajax(request):
    """
    File upload ajax method
    """
    doc_id = json.loads(request.POST['id'])

    # Delete specified document
    doc = Document.objects.get(pk=doc_id)
    doc.delete()

    # Return id of deleted document
    message = json.dumps({'id': doc_id})
    return HttpResponse(message)
