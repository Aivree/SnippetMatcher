from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from upload.models import Upload
from upload.forms import UploadForm

def index(request):
    # Handle file upload
    if request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Upload(
		username = 'heather',
                filename = request.FILES['filename'],
            )
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect('/upload/success')
    else:
        form = UploadForm() # A empty, unbound form

    # Load documents for the list page
    # documents = Upload.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'upload/index.html',
        #{'documents': documents, 'form': form},
        {'form': form},
        context_instance=RequestContext(request)
    )
