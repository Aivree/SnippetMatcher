# Create your views here.
import os
from django.shortcuts import render_to_response, render
from django.template import RequestContext
from django.http import HttpResponseRedirect, HttpResponse, Http404
from django.core.urlresolvers import reverse

from logviewer.models import ORLog
from logviewer.forms import DocumentForm
from orlogviewer import settings
import datetime


def index(request):
    # Handle file upload
    error = ''
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = ORLog(logFile=request.FILES['docfile'],
                           dateSubmitted=datetime.datetime.now(),
                           comment=request.POST.get('comment', ''),
                           displayName=request.POST.get('displayName', ''))
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('logviewer.views.index'))
        else:
            error = "Invalid file type."

    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = ORLog.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'logviewer/index.html',
        {'documents': documents, 'form': form, 'error': error},
        context_instance=RequestContext(request)
    )

def detail(request, log_id):
    try:
        orlog = ORLog.objects.get(pk=log_id)
    except ORLog.DoesNotExist:
        raise Http404
    return render(request, 'logviewer/detail.html', {'orlog_id': orlog.id, 'entries': orlog.entries()})

def errors(request, log_id):
    try:
        orlog = ORLog.objects.get(pk=log_id)
    except ORLog.DoesNotExist:
        raise Http404
    return render(request, 'logviewer/detail.html', {'orlog_id': orlog.id, 'entries': orlog.errors()})

def remove(request):
    #try:
    #    orlog = ORLog.objects.get(pk=log_id)
    #except ORLog.DoesNotExist:
    #    raise Http404
    #filepath = os.path.join(settings.MEDIA_ROOT, orlog.logFile.name)
    #os.remove(filepath)
    #orlog.delete()
    #return HttpResponseRedirect(reverse('logviewer.views.index'))
    if request.method == 'POST':
        logidlist = request.POST.getlist('logfiles')
        for id in logidlist:
            try:
                orlog = ORLog.objects.get(pk=id)
            except ORLog.DoesNotExist:
                raise Http404
            filepath = os.path.join(settings.MEDIA_ROOT, orlog.logFile.name)
            os.remove(filepath)
            orlog.delete()
        return HttpResponseRedirect(reverse('logviewer.views.index'))