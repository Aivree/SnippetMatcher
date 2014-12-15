from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.conf import settings
import re
import subprocess

from automatic_mapping.models import Map, Choicemapcolour, Choicemapdisplaytitle
from automatic_mapping.forms import DocumentForm

def index(request):
    # Handle file upload
    errors = []
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Map.create(
                                request.FILES['docfile'],
                                request.FILES['docfile'],
                                request.POST['title'],
                                request.POST['display_title'],
                                request.POST['colour_scheme'])
            newdoc.save()
            newdoc.make_r_picture()
            #            subprocess.call(newdoc.make_r_picture(), shell=True)
            
            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('automatic_mapping:index'))
        else:
            errors.append('Incorrect file type')
            errors.append('Allowed: .csv, .xls, .xlsx')
    else:
        form = DocumentForm() # A empty, unbound form
    
    # Load documents for the list page
    maps = Map.objects.order_by('-map_create_date')[:50]
    
    # Render list page with the documents and the form
    return render_to_response(
                              'automatic_mapping/index.html',
                              {'errors': errors, 'maps': maps, 'form': form},
                              context_instance=RequestContext(request)
                              )
