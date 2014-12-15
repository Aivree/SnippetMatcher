#views usercsuf
from django.shortcuts import get_object_or_404, render_to_response, render, redirect
from django.http import HttpResponse, HttpResponseRedirect,Http404
from django.template import Context, RequestContext

from django.contrib.auth import logout as auth_logout
from django.contrib.auth.decorators import login_required
from django.contrib.messages.api import get_messages

from csuf_user.models import CSUF_UserProfile as Profile

#for file uploads
from django.views.generic import DeleteView
from csuf_user.models import UploadedDocument as Document
from csuf_user.forms import DocumentForm
from django.core.urlresolvers import reverse, reverse_lazy


@login_required()
def upload(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'],user = request.user)
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('csuf_user:upload'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = request.user.uploadeddocument_set.all()
    #try:
    #except(KeyError, Document.DoesNotExist):    
        #documents = []

    # Render list page with the documents and the form
    return render_to_response(
        'csuf_user/upload.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )

class DocumentDelete(DeleteView):
    model = Document
    success_url = reverse_lazy('csuf_user:upload')
    def get_object(self, queryset=None):
        """ Hook to ensure object is owned by request.user. """
        doc = super(DocumentDelete, self).get_object()
        if not doc.user == self.request.user:
            raise Http404
        return doc

def index(request):
    #Home view, displays login mechanism
    context = {}
    if request.user.is_authenticated():
        return HttpResponseRedirect('/') 
    else:
        return render(request, 'csuf_user/index.html', context)
        
@login_required()
def profile(request):
    #Home view, displays login mechanism
    context = {}
    if request.user.is_authenticated():
        u_profile,created = Profile.objects.get_or_create(user = request.user)
        if created:
            #if the profile is new, collect additional data
            return render_to_response('csuf_user/data_form.html', {}, RequestContext(request))
        context = { 'profile': u_profile}
        return render(request, 'csuf_user/profile.html', context)
    else:
        return render(request, 'csuf_user/index.html', context)
        
#should probably put some javascript for in page validation of fields   
def data_collect(request):
    if request.method == 'POST':
        profile,created = Profile.objects.get_or_create(user = request.user)
        profile.first_name = request.POST['first_name']
        profile.last_name = request.POST['last_name']
        profile.school_email = request.POST['email']
        profile.cwid = request.POST['cwid']
        profile.save()
    #save the data, send them to the profile
    return HttpResponseRedirect(reverse('csuf_user:profile'))
        
def logout(request):
    """Logs out user"""
    auth_logout(request)
    return HttpResponseRedirect('/')        

