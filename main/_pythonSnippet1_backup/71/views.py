from django.shortcuts import render
from django.views.generic import ListView, DetailView
from blogengine.models import Category, Post


from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from blogengine.models import Document
from blogengine.forms import DocumentForm

# Create your views here.
class CategoryListView(ListView):
    def get_queryset(self):
        slug = self.kwargs['slug']
        try:
            category = Category.objects.get(slug=slug)
            return Post.objects.filter(category=category)
        except Category.DoesNotExist:
            return Post.objects.none()

class IndexView(ListView):

    def get_context_data(self, **kwargs):
        kwargs['categories'] = Category.objects.all()
        return super(IndexView, self).get_context_data(**kwargs)

class ExtendedDetailView(DetailView):

    def get_context_data(self, **kwargs):
        kwargs['categories'] = Category.objects.all()
        return super(ExtendedDetailView, self).get_context_data(**kwargs)

def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('blogengine.views.list'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'blogengine/list.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )
