from django.shortcuts import render_to_response
from django.template import Template, Context, RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from django.template.loader import get_template
import datetime

from django import forms

from runner.forms import DocumentForm
from runner.models import Document

def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('runner.views.list'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'list.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )

def index(request):
    from runner.models import Software
    software_list = []
    for i in Software.objects.all():
        i = str(i).split("|")
        software_list.append(i)

    t = get_template("bootstrap3.html")
    html = t.render(Context({
            'bootstrap3_title': 'Run programs',
            'software_list': software_list,
            }))
    return HttpResponse(html)

def software(request, name):
    t = get_template("bootstrap3.html")
    html = t.render(RequestContext(request, {
            'bootstrap3_title': 'Running ' + name,
            }))
    return HttpResponse(html)

def current_datetime(request):
    now = datetime.datetime.now()
    t = get_template("bootstrap3.html")
    html = t.render(Context({'current_date': now}))
    return HttpResponse(html)


