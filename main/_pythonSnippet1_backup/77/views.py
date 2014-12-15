from django.shortcuts import render
# Create your views here.
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from pmu_jira_update.models import Document
from pmu_jira_update.forms import DocumentForm
from rest_update import rest_test

from pmu_jira_update.ticket import Ticket

import shutil
import os
import time

def update(request):
    # Render list page with the documents and the form
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            # newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('pmu_jira_update.views.update'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()
    documents.delete()

    # Render list page with the documents and the form
    return render_to_response(
        'update.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )

def upload(request):

    upload_loc = '/home/ewang/django/ireport/media/upload'
    if os.path.exists(upload_loc):
        shutil.rmtree(upload_loc)

    # Render list page with the documents and the form
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        newdoc = Document(docfile = request.FILES['docfile'])
        newdoc.save()

        # Redirect to the document list after POST
    else:
        form = DocumentForm() # A empty, unbound form


    data = rest_test('https://jira.successfactors.com', 'ewang', 'passw0rd', 'project=pmu+and+type+in+("enhancement","Sub-task")+order+by+key+asc', newdoc.docfile.name)
    tickets = data.iterkeys()
    jira = []
    for t in tickets:
        tmp = Ticket(id=t, origin=data[t]['origin'], status=data[t]['status'], type=data[t]['type'])
        jira.append(tmp)
    documents = Document.objects.all()
    documents.delete()
    # Render list page with the documents and the form
    return render_to_response(
        'list.html',
        {'jira': jira},
        context_instance=RequestContext(request)
    )



