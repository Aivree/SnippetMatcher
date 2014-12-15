from __future__ import with_statement
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
from django.contrib.auth.decorators import login_required
from django import forms
from rooibos.data.models import Collection, FieldValue, standardfield, CollectionItem, Record
from rooibos.access.functions import check_access
from rooibos.storage.models import Storage, Media
from rooibos.pdfviewer.viewers import pdfviewer
import random


def main(request):
    collection = Collection.objects.get(name='the-breeze')

    coverage = standardfield('coverage')
    date = standardfield('date')

    volumes = sorted(map(int, FieldValue.objects.filter(
        record__collection=collection,
        field=coverage,
        label='Volume',
        ).values_list('value', flat=True).distinct()))

    try:
        volume = int(request.GET.get('v'))
    except (ValueError, TypeError):
        volume = None
    if not volume in volumes:
        volume = volumes[0]


    record_ids = FieldValue.objects.filter(
        record__collection=collection,
        field=coverage,
        label='Volume',
        value=str(volume),
        ).values_list('record', flat=True)


    issues = sorted(FieldValue.objects.filter(
        record__in=record_ids,
        field=coverage,
        label='Issue',
        ).values_list('record', 'value'))

    dates = sorted(FieldValue.objects.filter(
        record__in=record_ids,
        field=date,
        ).values_list('record', 'value'))


    combined = sorted((int(i[1]), d[1], i[0]) for i, d in zip(issues, dates))


    try:
        record_id = int(request.GET.get('r'))
    except (ValueError, TypeError):
        record_id = None
    if not record_id in (r for i, d, r in combined):
        record_id = combined[0][2]

    for i, d, r in combined:
        if record_id == r:
            issue = i
            break

    viewer = pdfviewer(None, request, record_id)


    return render_to_response('thebreeze.html',
                              {'collection': collection,
                               'breezelogo': 'breeze_logo_%s.png' %
                                    random.choice('00 01 02 03 04'.split()),
                               'volume': volume,
                               'volumes': volumes,
                               'issue': issue,
                               'issues': combined,
                               'record': record_id,
                               'viewer': viewer,
                               'embedcode': viewer.embed_code(request, None) if viewer else None,
                               },
                              context_instance=RequestContext(request))



class UploadForm(forms.Form):
    publication = forms.CharField(initial='The Breeze')
    volume = forms.IntegerField(min_value=1)
    issue = forms.IntegerField(min_value=1)
    date = forms.DateField()
    pages = forms.IntegerField(min_value=1)
    pdf = forms.FileField()


@login_required
def upload(request):
    collection = Collection.objects.get(name='the-breeze')
    storage = Storage.objects.get(name='the-breeze')

    check_access(request.user, collection, write=True, fail_if_denied=True)
    check_access(request.user, storage, write=True, fail_if_denied=True)

    fcoverage = standardfield('coverage')
    fdate = standardfield('date')
    ftitle = standardfield('title')
    fdescription = standardfield('description')
    fidentifier = standardfield('identifier')

    if request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)
        if form.is_valid():

            volume = str(form.cleaned_data['volume'])
            issue = str(form.cleaned_data['issue'])
            date = str(form.cleaned_data['date'])
            pages = str(form.cleaned_data['pages'])
            publication = form.cleaned_data['publication']

            title = '%s %s Volume %s Issue %s' % (
                publication,
                date,
                volume,
                issue,
            )

            record = Record.objects.create()
            CollectionItem.objects.create(record=record, collection=collection)
            record.fieldvalue_set.create(
                field=ftitle,
                label=None,
                order=1,
                value=title,
            )
            record.fieldvalue_set.create(
                field=fcoverage,
                label='Volume',
                order=2,
                value=volume,
            )
            record.fieldvalue_set.create(
                field=fcoverage,
                label='Issue',
                order=3,
                value=issue,
            )
            record.fieldvalue_set.create(
                field=fdate,
                label=None,
                order=4,
                value=date,
            )
            record.fieldvalue_set.create(
                field=fdescription,
                label='Pages',
                order=5,
                value=pages,
            )
            record.fieldvalue_set.create(
                field=fidentifier,
                label=None,
                order=6,
                value=title,
                hidden=True,
            )

            import re
            filename = re.sub(r'[^a-z0-9]+', '-', title.lower()) + '.pdf'

            media = Media.objects.create(
                record=record,
                storage=storage,
                mimetype='application/pdf',
            )
            media.save_file(filename, request.FILES['pdf'])

            return HttpResponseRedirect(reverse('thebreeze-main'))
    else:
        form = UploadForm()


    return render_to_response('thebreeze-upload.html',
                              {'breezelogo': 'breeze_logo_%s.png' %
                                    random.choice('00 01 02 03 04'.split()),
                                'form': form,
                                    },
                              context_instance=RequestContext(request))

