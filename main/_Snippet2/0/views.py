from django.shortcuts import render,render_to_response
from django.template import RequestContext
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect,HttpResponse

from fileupload.models import Document
from fileupload.forms import DocumentForm

# Create your views here.

def index(request):
    return HttpResponse('welcome to fileupload application')

def list(request):
    if request.method=='POST':
        form=DocumentForm(request.POST,request.FILES)
        if form.is_valid():
            newdoc=Document(docfile=request.FILES['docfile'])
            newdoc.save()
            return HttpResponseRedirect(reverse('fileupload.views.list'))

    else:
        form=DocumentForm()

    documents=Document.objects.all()
    return render_to_response(
        'list.html',
        {'documents':documents,'form':form},
        context_instance=RequestContext(request)
    )
