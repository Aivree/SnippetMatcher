from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from models import Document
from forms import DocumentForm

def list(request):
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            return HttpResponseRedirect(reverse('list'))
    else:
        form = DocumentForm()

    documents = Document.objects.all()

    return render_to_response(
        'uploader/list.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )