# Create your views here.
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.template.loader import get_template
from django.template import Context
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt



from businessapp.models import Document
from businessapp.forms import DocumentForm








def welcome(request):
    if request.method == 'GET':
        t = get_template('welcome.html')
	html = t.render(Context({}))
	return HttpResponse(html)


def welcomes(request):
    if request.method == 'GET':
        t = get_template('list.html')
	html = t.render(Context({}))
	return HttpResponse(html)






@csrf_exempt
def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('businessapp.views.list'))
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




def lists(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('businessapp.views.list'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()
    for	document in documents:
    	print document.docfile.name
    	

    # Render list page with the documents and the form
    return render_to_response(
        'list1.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )

#For handling disabling function 

def loadPage(request,file_name):
	if request.method=='GET':
		data=Document.objects.get(id=file_name)
		data.delete()
		print file_name
		t = get_template('list.html')
		html = t.render(Context({}))
		return HttpResponse(html)
