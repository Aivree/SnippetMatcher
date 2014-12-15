# -*- coding: utf-8 -*-
from django.shortcuts import render_to_response
from django.template import RequestContext
from contact.forms import ContactForm, DocumentForm
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from contact.models import Document
from random import randint
import json


def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('con:list'))
    else:
        form = DocumentForm() # A empty, unbound form

    print request.revision
    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'contact/list.html',
        {'documents': documents, 'form': form,'REVISION':request.revision},
        context_instance=RequestContext(request),
    )




def contact2(request): 
    cntform = ContactForm()
    c = {}
    c['form'] = cntform
    c = RequestContext(request, c)

    template_name = 'contact/contact_form.html'
    return render_to_response(template_name, c)

def contact(request):
    # if this is a POST request we need to process the form data
    if request.method == 'POST':
        # create a form instance and populate it with data from the request:
        form = ContactForm(request.POST)
        # check whether it's valid:
        if form.is_valid():
            # process the data in form.cleaned_data as required
            # ...
            # redirect to a new URL:
            print 'envia email'
            return HttpResponseRedirect('/bug/thanks')

    # if a GET (or any other method) we'll create a blank form
    else:
        form = ContactForm()
    template_name = 'contact/contact_form.html'
    c = {}
    c['form'] = form
    c = RequestContext(request, c)

    return render_to_response(template_name, c)


def thanks(request):
    template_name = 'contact/thanks.html'
    d = {}
    return render_to_response(template_name, d)

def view(request):
    return request.revision

def chart(request):
    template_name = "contact/un.html"
    resultado = []
    count = 1
    for i in range(4):
        serie = []
        for i in range(5):
            serie.append(randint(0, 100))
        resultado.append(serie)
        serie = []
        count += 1
    c = {}
    c['res'] = json.dumps(resultado)
    c = RequestContext(request, c)

    return render_to_response(template_name, c)


def chart1(request):
    template_name = "contact/un1.html"
    resultado = []
    count = 1
    for i in range(4):
        serie = []
        for i in range(5):
            serie.append(randint(0, 100))
        resultado.append(serie)
        serie = []
        count += 1
    c = {}
    c['res'] = json.dumps(resultado)
    c = RequestContext(request, c)

    return render_to_response(template_name, c)


def chart2(request):
    template_name = "contact/un2.html"
    resultado = []
    count = 1
    for i in range(4):
        serie = []
        for i in range(5):
            serie.append(randint(0, 100))
        resultado.append(serie)
        serie = []
        count += 1
    c = {}
    c['res'] = json.dumps(resultado)
    c = RequestContext(request, c)

    return render_to_response(template_name, c)


def chart3(request):
    template_name = "contact/un3.html"
    resultado = []
    count = 1
    for i in range(4):
        serie = []
        for i in range(5):
            serie.append(randint(0, 100))
        resultado.append(serie)
        serie = []
        count += 1
    c = {}
    c['res'] = json.dumps(resultado)
    c = RequestContext(request, c)

    return render_to_response(template_name, c)
