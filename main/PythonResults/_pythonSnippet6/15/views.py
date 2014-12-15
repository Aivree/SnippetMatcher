#-*- encoding: utf-8 -*-
from django.shortcuts import render, get_object_or_404

from django.http import HttpResponse, HttpResponseRedirect
from django.template import RequestContext, loader
from django.core.urlresolvers import reverse
from django.views import generic


from .forms import UploadFileForm
from django.shortcuts import render_to_response

from django.contrib.auth.decorators import login_required
# Create your views here.

@login_required
def index(request):
    user = request.user
    if not user.has_perm('signtool.use_signtool'):
        print 'error'
        return render_to_response('error.html', {
            'error_message' : 'no permission!',
            },
            context_instance=RequestContext(request)
            )
    if request.method == 'POST':
        form = UploadFileForm(request.POST, request.FILES)
        if form.is_valid():
            f = request.FILES['file']
            # 获取上传文件内容和文件名称
            # 签名然后返回给前端用户下载
            data = f.read()

            response = HttpResponse(mimetype="application/octet-stream")
            response["Content-Disposition"] = 'attachment; filename="log.exe"'
            response["Content-Length"] = len(data)
            response.write(data)
 
            return response
    else:
        form = UploadFileForm()
    return render_to_response('upload.html', {
        'form': form,
        },
        context_instance=RequestContext(request)
        )
