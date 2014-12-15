# -*- coding: utf-8 -*-

import os
import urllib

from settings import MEDIA_ROOT

from django.core.files.uploadedfile import SimpleUploadedFile
from django.http import HttpResponse, QueryDict
from django.shortcuts import render_to_response
from django.template.loader import render_to_string
from django.utils import simplejson

from django.middleware.csrf import get_token
from django.template import RequestContext

from req_proc.models import ImgMarked, CertCreated
from req_proc.forms import CertCreatedForm

def index( request ):
    file_list = []
    path = os.path.join( __file__, os.pardir, MEDIA_ROOT, 'smigik/users' )
    for root, dirs, files in os.walk( path ):
        if os.path.basename( root ) == 'requests':
            req_nums = [os.path.splitext( req )[0] for req in files]
            file_list.append( req_nums )
    flat_file_list = sum( file_list, [] )

    form = CertCreatedForm( request.POST )

    if form.is_valid():
        cd = form.cleaned_data
        print( cd )

    return render_to_response( 'req_proc/base_index.html', {'req_list': flat_file_list, 'form': form} )

def upload_img( request ):
    if request.method == 'POST' and request.is_ajax():
        filename = request.GET['qqfile']
        filedata = request.read()

        uploaded_img = SimpleUploadedFile( filename, filedata )
        try:
            img = ImgMarked( img=uploaded_img )
            img.save()

            notice = 'Изображение {0} загружено'.format( filename )
            success = 'True'
        except:
            notice = 'Изображение {0} не удалось загрузить'.format( filename )
            success = 'False'
        response = simplejson.dumps( {'success': success, 'notice': notice} )
    else:
        html = render_to_string( 'req_proc/_upload_img.html' )
        response = simplejson.dumps( {'success': 'True', 'html': html} )

    if request.is_ajax():
        return HttpResponse( response, content_type='application/javascript' )

def upload_cert( request ):
    if request.method == 'POST':
        response = simplejson.dumps( {'success': 'True', 'html': 'hi'} )
    else:
        form = CertCreatedForm()
        html = render_to_string( '_form.html', {'id': 'upload_cert', 'form': form,
            'submit_val': 'Сертификат сформирован'} )
        response = simplejson.dumps( {'success': 'True', 'html': html} )

    if request.is_ajax():
        return HttpResponse( response, content_type='application/javascript' )

def uploaded( request ):
    print( request.method )
    print( request.FILES )
    if request.method == 'POST':
        print( request.POST )
        print( request.POST['speed'] )
        return render_to_response( 'req_proc/_uploaded.html', request.POST )
