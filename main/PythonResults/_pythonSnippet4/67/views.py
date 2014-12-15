from django.shortcuts import render_to_response
from models import School
from django.http import HttpResponseRedirect

from django.template import RequestContext
from django.core.urlresolvers import reverse

from models import Document
from forms import DocumentForm

def index(request):

    schools_list = School.objects.all()

    extra_context = {"schools_list": schools_list}

    return render_to_response("testMongo/index.html", extra_context)

    """
    # saving document to School collection
  school = School(name = 'University of Toronto', address = 'Toronto')
  school.save()

  return HttpResponse("<h1>Saved!</h1>")
    """

def upload_file(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document upload view after POST
            return HttpResponseRedirect('testMongo.testMongo.views.upload_file')
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'testMongo/upload.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )