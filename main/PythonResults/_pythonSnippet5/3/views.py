from django.contrib.auth.models import User
from django.core.urlresolvers import reverse
from django.http.response import HttpResponseRedirect
from django.contrib.auth.decorators import login_required
from django.shortcuts import render_to_response
from histoconscalchaqui.models import Especialista
from django.shortcuts import render
from django.template.loader import get_template
#from histoconscalchaqui.forms import UploadForm
#from histoconscalchaqui.models import Document

def admin(request):
    return render_to_response('index.html')

#def upload_file(request):
 #   if request.method == 'POST':
  #      form = UploadForm(request.POST, request.FILES)
   #     if form.is_valid():
    #        newdoc = Document(filename = request.POST['filename'],docfile = request.FILES['docfile'])
     #       newdoc.save(form)
      #      return redirect("uploads")
    #else:
     #   form = UploadForm()