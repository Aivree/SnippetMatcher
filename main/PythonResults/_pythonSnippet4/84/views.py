from django.contrib.auth.models import User
from django.http import HttpResponse, HttpResponseRedirect
from django.shortcuts import render_to_response, get_object_or_404
from django.template import RequestContext
from django.contrib.auth.forms import UserCreationForm, AuthenticationForm
from django.template import RequestContext
from django.core.urlresolvers import reverse
from django.utils import simplejson

from app.models import Document

from app.forms import DocumentForm
from django.conf import settings

import functions


def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()
            # Redirect to the document list after POST
            print '**********************************'
            #print newdoc
            dir_name= str(newdoc).split('/')
            name_file= str(dir_name[1])
            #print settings.MEDIA_ROOT
            filepath = str(settings.MEDIA_ROOT) +'/' + str(dir_name[0]) + '/'

            #functions.xlsx2geojson(filepath,name_file)
            request.session['name_file'] = name_file
            request.session['filepath'] = filepath
           # return HttpResponseRedirect(reverse('app.views.listdetails'))

            array_headers= functions.readheader_xlsx(filepath,name_file)
           
            print array_headers
            variable = {'array_headers':simplejson.dumps(array_headers)}          

            return render_to_response('listdetails.html',variable,context_instance=RequestContext(request))


    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'list.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )



def listdetails(request):

    array_headers=request.session.get('array_headers')
    variable = {'array_headers':simplejson.dumps(array_headers)}
    print array_headers
    #del request.session['array_headers']
    #dato = get_object_or_404(Document, pk=id_file)
    #print request.session.get('array_headers')
    #context = {'favorite': settings.MEDIA_ROOT}
    return render_to_response('listdetails.html',variable,context_instance=RequestContext(request))



def mapping(request):
    if request.method == 'POST':
        fields = request.POST.getlist('tittle') 
        field_id = request.POST.get('tittle_id')   
        print field_id     
        #print fields
        lat=request.POST.get('latitud') 
        #print lat
        lng=request.POST.get('longitud') 
        #print lng

        filepath = request.session.get('filepath')
        name_file = request.session.get('name_file')

        functions.xlsx2geojson_parameters(filepath,name_file,fields,field_id,lat,lng)
        name_dir=name_file.split(".")
        name_file=name_dir[0]
        context_url = {'url': settings.MEDIA_ROOT}
        #print context_url
        context_file = {'name_file': name_file}
        return render_to_response('mapping.html',context_file,context_instance=RequestContext(request))
    else:
        return render_to_response('mapping.html',context_instance=RequestContext(request))
