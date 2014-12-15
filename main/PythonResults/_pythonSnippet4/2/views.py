# Create your views here.
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from upload.models import Document
from upload.forms import DocumentForm

def list(request):
	if request.method == 'POST':
		form = DocumentForm(request.POST, request.FILES)
		if form.is_valid():
			form.save()

			return HttpResponseRedirect(reverse('upload.views.list'))
	else:
		form = DocumentForm()

	documents = Document.objects.all()

	return render_to_response(
		'upload/list.html',
		{'documents': documents, 'form': form},
		context_instance=RequestContext(request)
	)