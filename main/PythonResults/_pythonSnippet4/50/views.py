from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.utils import simplejson
from shopaholic.photos.models import Document
from shopaholic.photos.forms import DocumentForm

from extract_features import *


def upload(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile=request.FILES['docfile'])
            newdoc.save()
            x = form.cleaned_data['x']
            y = form.cleaned_data['y']
            hn = form.cleaned_data['hn']
            wn = form.cleaned_data['wn']
            fn = 'media/uploads/' + request.FILES['docfile'].name
            response = get_result(fn, x, y, hn, wn)
            json_data = simplejson.dumps(response)
            return HttpResponse(json_data, mimetype='application/json')
    else:
        # A empty, unbound form
        form = DocumentForm()

    return render_to_response(
    'photos/list.html',
    {'form': form},
    context_instance=RequestContext(request)
    )



"""
def upload(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile=request.FILES['docfile'])
            newdoc.save()

            prueba()
            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('photos.views.upload'))
    else:
        # A empty, unbound form
        form = DocumentForm()

    # Render list page with the documents and the form

    response = {'1': 'bu', '2': 'bu'}
    json = simplejson.dumps(response)
    return HttpResponse(json, mimetype='application/json')
    return render_to_response(
        'photos/list.html',
        {'form': form},
        context_instance=RequestContext(request)
    )
"""
