# encoding=UTF-8

from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect

from django.utils import timezone

from .models import ECG
from .forms import DocumentForm


def index(request, template=None):
    return render_to_response(template, RequestContext(request))


def list_ecg(request):
    ecgs = ECG.objects.all()
    context = RequestContext(request)
    return render_to_response(
        'list_ecg.html',
        {'ecgs': ecgs},
        context
    )


def upload_ecg(request):
    # Recebe o arquivo enviado pelo usuário
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            ecg_data = read_file(request.FILES['doc_file'])
            save_ecg(ecg_data)
            return HttpResponseRedirect('/listar/')
    else:
        form = DocumentForm()

    context = RequestContext(request)
    return render_to_response(
        'upload_ecg.html',
        {'form': form},
        context
    )


def read_file(file):
    data = ''
    if file.multiple_chunks():
        for chunk in file.chunks:
            data += chunk
    else:
        data += file.read()
    return data


def save_ecg(ecg_data):
    ecg = ECG(data=ecg_data, creation_date=timezone.now())
    ecg.save()


def view_chart(request, ecg_id):
    ecg = ECG.objects.get(id=ecg_id)

    context = RequestContext(request)
    return render_to_response(
        'view_chart.html',
        {'ecg_data': ecg.data},
        context
    )


