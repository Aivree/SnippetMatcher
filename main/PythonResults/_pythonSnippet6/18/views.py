# -*- coding: utf-8 -*-

from django.http import HttpResponse
from django.utils.simplejson import dumps, loads
from django.core.context_processors import csrf
from djangomako.shortcuts import render_to_response
from django.core.files.images import ImageFile

def drag2upload(request):
    c = {}
    c.update(csrf(request))
    return render_to_response('fun/drag2upload.html', c)

def j_upload(request):
    res = {'r':1}
    if request.method == 'POST':
        #picfile = request.FILES.get('picfile')
        picfile = request.POST.get('picfile', '')
        name = request.POST.get('name', '')
        from cStringIO import StringIO
        # from PIL import Image
        # im = Image.open(StringIO(picfile))
        # im.save('public/upload/%s', 'PNG')
        from django.core.files.images import ImageFile
        dest = open('public/upload/%s' % name, 'wb')
        # import urllib2
        import base64
        output = base64.b64decode(picfile)
        assert False, output
        # import binascii
        # bfile = bin(int(binascii.hexlify(StringIO(picfile).read()), 16))
        #dest.write(StringIO(try_str).read())
        dest.close()
    return HttpResponse(dumps(res), mimetype='application/json')

def index(request):
    return render_to_response('fun/index.html', {})

def guess(request):
    return render_to_response('fun/guess.html', {})

def guess1(request):
    return render_to_response('fun/guess_1.html', {})

def guess2(request):
    return render_to_response('fun/guess_2.html', {})

def guess3(request):
    return render_to_response('fun/guess_3.html', {})

def dart(request):
    return render_to_response('fun/dart_index.html', {})

def dart_notwork(request):
    return render_to_response('fun/dart_notwork.html', {})

def dart_sunflower(request):
    return render_to_response('fun/dart_sunflower.html', {})

def dart_slider(request):
    return render_to_response('fun/dart_slider.html', {})

def hd(request):
    """happy day work"""
    return render_to_response('fun/hd.html', {})

def har_view(request):
    return render_to_response('fun/har_view.html', {})

