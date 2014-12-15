from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from p2_homework.models import Document
from p2_homework.forms import DocumentForm

import json
import os

# Create your views here.
def list(request):
	if request.method == 'POST':
		form = DocumentForm(request.POST, request.FILES)
		if form.is_valid():
			newdoc = Document(docfile = request.FILES['docfile'])
			newdoc.save()
		
		return HttpResponseRedirect(reverse('p2_homework.views.list'))
	else:
		form = DocumentForm()
		
	documents = Document.objects.all()
	
	
	return render_to_response(
	    'list.html',
	    {'documents': documents, 'form': form},
	    context_instance=RequestContext(request))



def specific_document(request, document_id=1):
	with open('/home/bartek/Pulpit/new_project/files/'+str(Document.objects.get(id=document_id).docfile.name), 'r') as json_file:
		mydata = json.loads(json_file.read())
	documents = Document.objects.all()
	return render_to_response('specific_document.html', {"mydata": mydata, "documents": documents},)
