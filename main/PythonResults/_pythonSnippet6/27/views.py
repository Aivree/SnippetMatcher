# Create your views here.
from django.http import HttpResponseRedirect
from django.shortcuts import render_to_response
from .forms import UploadFileForm
from models import FileUpload
from django.template import RequestContext
from django.http import HttpResponse

# Imaginary function to handle an uploaded file.

def my_courses(request):
    return render_to_response('repository/my_courses.html', locals(), context_instance=RequestContext(request))

def course_profile(request):
    return render_to_response('repository/course_profile.html', locals(), context_instance=RequestContext(request))

def course_profile_alternate(request):
    return render_to_response('repository/course_profile_alternate.html', locals(), context_instance=RequestContext(request))

def upload_file(request):
    if request.method == 'POST':
        form = UploadFileForm(request.POST, request.FILES)
        if form.is_valid():
            instance = FileUpload(uploaded_by=request.user, file_type=FileUpload.SYLLABUS, file_field=request.FILES['file'])
            instance.save()
            return HttpResponse(instance.file_field.read())
        else:
            return HttpResponse("hahahahhahaha")
    else:
        form = UploadFileForm()
        return render_to_response('repository/upload.html', {'form': form}, context_instance=RequestContext(request))

def search_results(request):
    return render_to_response('repository/search_page_draft.html', locals(), context_instance=RequestContext(request))