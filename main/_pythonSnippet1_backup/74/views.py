# -*- coding: utf-8 -*-
from django.shortcuts import render_to_response
from django.template import RequestContext,loader
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.http import HttpResponse
from django import template

from models import Document
from forms import DocumentForm

import re


def resume_parser(request):
    """This function will get request as input, 
    in which we will have all request parameters including file uploaded

    request.FILES['docfile'] give us file object, using read method over that object we get file content

    That file content is parsed for required fields
    """
    # Handle file upload
    dic = {}
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            #import pdb;pdb.set_trace()
            newdoc = Document(docfile = request.FILES['docfile'])
            file_pointer = request.FILES['docfile'].read()
            for line in file_pointer:
                phone_re = re.compile(r'(\d{3})\D*(\d{3})\D*(\d{4})\D*(\d*)$', re.VERBOSE)
                phone_num = prog.match(line)

                email_re = re.search(r'\w+@\w+', line)
                if email_re:
                     email = match.group()
                

            print phone_num, email
            # Redirect to the document list after POST
            # return HttpResponseRedirect(reverse('s2.views.list'))
    else:
       form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()
    #import pdb;pdb.set_trace()    
    #print dir(documents)
    # Render list page with the documents and the form
    return render_to_response('list.html',{'documents': documents, 'form': form},context_instance=RequestContext(request))


