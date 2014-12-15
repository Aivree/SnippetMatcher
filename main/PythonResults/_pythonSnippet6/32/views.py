from django.http import HttpResponseRedirect
from django.shortcuts import render_to_response
from .forms import UploadFileForm
from OSFileHoster.libs.utils import SwiftHelper
from OSFileHoster.libs.utils import KeystoneHelper
from django.template import RequestContext

swifthelper = SwiftHelper()
kshelper = KeystoneHelper()

def session_auth_token(request):
    ksclient = kshelper.getKsadminFomTokenId(request.user.token.id)
    return HttpResponseRedirect('/')

def upload_file(request):
    if request.method == 'POST':

        for filename, file in request.FILES.iteritems():
            swifthelper.putFile(request.user.token.id, 'test', request.FILES[filename].name, request.FILES[filename].read())
        return HttpResponseRedirect('/upload_file/')
    else:
        form = UploadFileForm()
        return render_to_response('file/upload.html',  {'form' : form }, context_instance=RequestContext(request))
