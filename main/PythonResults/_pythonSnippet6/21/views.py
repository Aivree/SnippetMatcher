from django.http import HttpResponseRedirect
from django.shortcuts import render_to_response
from forms import UploadFileForm
from HandleFile import handle_uploaded_file
from django.http import HttpResponse

def upload_file(request):
    if request.method == 'POST':
        form = UploadFileForm(request.POST, request.FILES)
        if form.is_valid():
            handle_uploaded_file(request.FILES['file'], request.user)
            return HttpResponseRedirect('/success/url/')
    else:
        form = UploadFileForm()
    return render_to_response('filetest.html', {'form': form})

def get_file(request, user_name, file_name):
    open_file = open(user_name+"/"+file_name, "rb").read()
    return HttpResponse (open_file)


