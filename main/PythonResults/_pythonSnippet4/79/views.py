from django.shortcuts import render_to_response
from django.http import HttpResponse, HttpResponseRedirect
from django.http import HttpRequest
import ReadingDatabase
import CreateDatabase
from urllib2 import Request
from django.forms.models import ModelForm
from app1.models import DocumentsUploaded
from django.template.context import RequestContext
from app1.forms import DocumentForm
from django.core.urlresolvers import reverse

#from urllib.request import Request


# Create your views here.
def home(r) :
    CreateDatabase.create_images()
    return render_to_response("Header.html",{'photo':ReadingDatabase.getImage()})

def home1(self,form) :
    #newdoc = DocumentsUploaded(self.get_form_kwargs().get('files')['documents_uploaded'])
    #newdoc.save()
    #HttpRequest.read(self) 
    #form = ModelForm(HttpRequest)
    #CreateDatabase.upload_documents(HttpRequest.read(self))
    return HttpResponse("hello from backend!!!!!!!")

def createNewProject(request) :
    #newdoc = DocumentsUploaded(self.get_form_kwargs().get('files')['documents_uploaded'])
    form = DocumentForm(request.POST, request.FILES)
    if form.is_valid():
        newdoc = DocumentsUploaded(document_location = request.FILES['docfile'])
        newdoc.save()
        return HttpResponseRedirect(reverse('app1.views.createNewProject'))
    return render_to_response("CreateNewProject.html",{'form':DocumentForm}, context_instance=RequestContext(request))


#def photo_view(request):
#    return render_to_response('app_name/photos.html', {
#        'photos': Photo.objects.all()
#        })
    
#def query_database(Request):
#    d = ReadingDatabase.getImage()# Create your views here.
