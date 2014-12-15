from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.conf import settings
import re
import subprocess
import time

from automatic_money.models import Money
from automatic_money.forms import DocumentForm

def index(request):
    # Handle file upload
    errors = []
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Money.create(
                                request.FILES['docfile'],
                                request.FILES['docfile'],
                                request.POST['title'])
            newdoc.save()
            time.sleep(1)
            newdoc.make_money_analysis()
            #            subprocess.call(newdoc.make_r_picture(), shell=True)
            
            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('automatic_money:index'))
        else:
            errors.append('Incorrect file type')
            errors.append('Allowed: .xls, .xlsx')
    else:
        form = DocumentForm() # A empty, unbound form
    
    # Load documents for the list page
    moneys = Money.objects.order_by('-money_create_date')[:50]
    
    # Render list page with the documents and the form
    return render_to_response(
                              'automatic_money/index.html',
                              {'errors': errors, 'moneys': moneys, 'form': form},
                              context_instance=RequestContext(request)
                              )
