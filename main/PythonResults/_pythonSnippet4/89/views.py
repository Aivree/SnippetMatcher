from django.http import HttpResponse, HttpResponseRedirect
from django.shortcuts import render, render_to_response
from django.core.files.storage import FileSystemStorage
from django.template import RequestContext, loader
from django.core.urlresolvers import reverse
from csvtottl.csvtordfconverter import convertIt
from csvtottl.models import Document
from csvtottl.forms import DocumentForm
from django import forms
from csvtottl.csvtordfconverter import convertIt
import os
import csv
import rdflib
import json

class UploadFileForm(forms.Form):
    title = forms.CharField(max_length=50)
    file = forms.FileField()

# Handle file upload
def csvtordf(request):
    
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            try:
                Document.objects.all().delete()
            except:
                pass
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.id = 1
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('csvtottl:csvtordf'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'csvtottl/csvtordf.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )

def deleteFile(request):
    Document.objects.all().delete()
    return HttpResponseRedirect(reverse('csvtottl:csvtordf'))

def printline(file, line_list, line):
    line_list.append(line)
    file.write(line+'\n');

def convertFile(request):
    if request.is_ajax():
        separator = request.POST.get('separator')
        headers = request.POST.get('headers')
        headers = (headers == "true")
        print(headers)

        filename = Document.objects.all()[:1].get().docfile.name.rpartition('.')[0]
        
        if ((not separator == ",") and 
                (not separator == ";") and
                (not separator == ":") and
                (not separator == "|")):
            if (not separator == "space"):
                separator = "\t"
            else:
                separator = " "

        line_list = convertIt(separator,headers,filename)

        context = {'line_list': line_list}
        
        data = {}
        data['filename'] = filename
        return HttpResponse(json.dumps(data), content_type = "application/json")
        