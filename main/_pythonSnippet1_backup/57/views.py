from django.http import HttpResponse, Http404
from django.shortcuts import render_to_response, get_object_or_404
from django.template import RequestContext
from django.http import HttpResponseRedirect
from inception.ctf.models import Document,task,categoryTask
from inception.ctf.forms import DocumentForm,check_form

def index(request):
    return render_to_response('start.html', context_instance=RequestContext(request))

def show_cat(request):
    return render_to_response(
        'ctf/cat.html',
        {'s':categoryTask.objects.all()},
        context_instance=RequestContext(request)
    )
def show_task(request):
    return render_to_response('ctf/task.html',
            {'s':task.objects.all().order_by('category')},
        context_instance=RequestContext(request))

def form_flag(request):
    task_list = task.objects.all()
    return render_to_response('ctf/post.html',{'list_task':task_list},
    context_instance=RequestContext(request))


def check_flag(request):
    if request.method == 'POST':
        if request.POST.get('flag',''):
            flag = request.POST['flag']
        else:
            return HttpResponse('Input the flag, push back')
        if request.POST.get('title',''):
            title = request.POST['title']

        try:
            ls = answers.objects.get(task=flag)
            return render_to_response('ctf/result.html',
                    {'msg':flag,'task':title,'result':u'true - you right'},
                    context_instance=RequestContext(request)
            )
        except answers.DoesNotExist or task.DoesNotExist:
            return render_to_response('ctf/result.html',
                    {'msg':flag,'task':title,'result':u'false - try again'},
                context_instance=RequestContext(request)
            )

    else:
        raise Http404


def wait(request):
    return HttpResponse(request.path)






def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(('ctf.views.list'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'ctf/task.html',
            {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )
