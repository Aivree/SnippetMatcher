from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.template import Context, loader
from django.http import HttpResponse
from django.http import Http404
from django.shortcuts import render_to_response
from django.shortcuts import render_to_response, get_object_or_404
from django.template import RequestContext
from django.shortcuts import get_object_or_404, render_to_response
from django.http import HttpResponseRedirect, HttpResponse
from django.core.urlresolvers import reverse
from django.template import RequestContext
from django.utils import timezone
from FamilialSharing.platform.models import Document, Person
from FamilialSharing.platform.forms import DocumentForm, DocumentsForm
from django.utils.encoding import smart_str, smart_unicode
from django.utils import simplejson
import simplejson as json
import datetime
from django.template import TemplateDoesNotExist
import socket

address=str(socket.gethostbyname(socket.gethostname()))

def list(request):

    if request.method == 'POST':
        a=request.FILES.getlist('myfiles')
        author=request.POST["author"]
        

        if author=="":
            messageError="Please say who you are :)"
            form = DocumentForm() # A empty, unbound form  
            documents = Document.objects.all()
            persons=Person.objects.all()
            return render_to_response('platform/list2.html',{'address':address,'messageAuthor':messageError,'persons':persons,'documents': documents, 'form': form}, context_instance=RequestContext(request))
        
        if len(a)==0:
            messageError="Please choose a file :)"
            form = DocumentForm() # A empty, unbound form                                                                        
            documents = Document.objects.all()
            persons=Person.objects.all()
            return render_to_response('platform/list.html',{'address':address,'messageAuthor':messageError,'persons':persons,'documents': documents, 'form': form}, context_instance=RequestContext(request))


        person=Person.objects.filter(name=author)
        if not person:
            person=Person(name=author)
            person.save()
        else:
            person=person[0]
            
        form=DocumentForm(request.POST,request.FILES.getlist('myfiles')[0])
        for i in a:
            newdoc = Document(docfile = i)
            #be careful: for the same author, not adding the same image: "_".join(a[:-1]) with a=b.split("_")
            b=newdoc.docfile.url.split("/")[-1]
            newdoc.name=b[max(0,len(b)-10):]
            newdoc.author=person
            newdoc.save()
        form = DocumentForm() # A empty, unbound form
        documents = Document.objects.all()
        persons=Person.objects.all()
        return render_to_response('platform/list.html',{'persons':persons,'address':address,'documents': documents, 'form': form}, context_instance=RequestContext(request)
    )
    else:
        form=DocumentsForm()
        documents = Document.objects.all()
        persons=Person.objects.all()
        return render_to_response('platform/list.html',{'persons':persons,'documents': documents,'address':address, 'form': form} ,context_instance=RequestContext(request)
    )

def download(request,document_id):
    import os
    a=Document.objects.get(id=document_id)
    #Here, put the path of the files: 
    data = open(os.path.join("/Your/Path/until/....../FamilialSharing/media",a.docfile.name),'r').read()
    resp = HttpResponse(data, mimetype='application/x-download')
    n=a.docfile.name.split("/")[-1]
    resp['Content-Disposition'] = 'attachment;filename='+str(n)
    return resp


def delete(request):
    return render_to_response('platform/delete.html',context_instance=RequestContext(request))


def deleteSure(request):
    Document.objects.all().delete() 
    form=DocumentsForm()
    documents = Document.objects.all()
    persons=Person.objects.all()
    return render_to_response('platform/list.html',{'address':address,'persons':persons,'documents': documents, 'form': form} ,context_instance=RequestContext(request))


def loadDocs(request):
    author=request.GET["author"]
    person=Person.objects.filter(name=author)
    response="Documents loaded by: "+author+"\n"
    if (author=="Everyone"):
        docs = Document.objects.all()
    else:
        try:
            docs=Document.objects.filter(author=person)
        except:
            docs=[]
    for f in docs:
        string=""
        string+='<div id="file">'
        string+='<a href="'+f.docfile.url+'"><img border="0" src="'+f.docfile.url+'"  alt="'+f.docfile.name+'"></a>'
        string+='<a href="http://'+address+':8080/document/1'+str(f.id)+'"> By '+f.author.name+' </a>'
        string+='</div>'
        response+=string
    return HttpResponse(response)
