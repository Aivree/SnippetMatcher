# -*- coding: utf-8 -*-
import os.path

from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
import codecs
import numpy as np
from pandas import read_csv, DataFrame, ExcelFile
import pandas as pd


from myproject.myapp.models import Document
from myproject.myapp.forms import DocumentForm


def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()



            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('myproject.myapp.views.list'))

    else:
        form = DocumentForm() # A empty, unbound form


    # Load documents for the list page
    documents = Document.objects.all()[4:]

    a = Document.objects.last()
    url = 'myproject' + a.docfile.url

    if os.path.isfile(url):
        vic = 'TRUE'

        v = ExcelFile(url).parse("Sheet1", parse_cols=[0, 18, 26, 25, 23])
        v.columns = ['Request','Product', 'Paid', 'PaidDate', 'Type']
        # it is right!.to_datetime,
        v.PaidDate = pd.to_datetime(v.PaidDate, format='%d.%m.%Y' )
        df = pd.pivot_table(v, values='Paid', rows='PaidDate', cols=['Type', 'Product'], aggfunc=[np.sum, np.count_nonzero])
        df = df.resample('M', how='sum')
        df = df.fillna(value=0)

        # Render list page with the documents and the form
        return render_to_response(
            'myapp/list.html',
            {'documents': documents, 'form': form, 'df': df.to_html(classes="table-condensed"),'url':url, 'vic': vic},
            context_instance=RequestContext(request)
        )
    else:
        vic = 'False'
        return render_to_response(
            'myapp/list.html',
            {'documents': documents, 'form': form, 'url': url, 'vic': vic},
            context_instance=RequestContext(request)
        )