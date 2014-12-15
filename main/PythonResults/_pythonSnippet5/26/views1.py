# Create your views here.
from django.shortcuts import render, get_object_or_404
from django.http import HttpResponseRedirect, HttpResponse
from django.core.urlresolvers import reverse
from django.utils import timezone
from gallery.models import File, Acceptance, Comment
from django.contrib.auth import authenticate, login
from django.contrib.auth.models import User
from .forms import UploadForm, UserForm
from django.contrib.auth.decorators import login_required 


def index(request):
	# get the blog posts that are published
	files = File.objects.all()
	# now return the rendered template
	return render(request, 'gallery/index.html', {'files':files})
	
	
def register(request):
	if request.method == 'POST':
		form = UserForm(request.POST)
		if form.is_valid():
			name = form.cleaned_data['username']
			pw = form.cleaned_data['password']
			newuser = User.objects.create_user(name, form.cleaned_data['email'], pw)
			newuser.save()
			user = authenticate(username=name, password=pw)
			login(request, user)
			return HttpResponseRedirect('/')
	else:
		form = UserForm()
	return render(request, 'registration/register.html', {'form':form})
	
	
def view(request, title):
	# get the object
	file = get_object_or_404(File, title=title)
	# now return the rendered template
	return render(request, 'gallery/view.html', {'file': file})

	
	
	
@login_required(login_url='/login')
def create(request):
	if request.method == 'POST':
		form = UploadForm(request.POST, request.FILES)
		if form.is_valid():
			newfile = File(title = form.cleaned_data['title'], description = form.cleaned_data['description'], create_time = timezone.now(), docfile = request.FILES['docfile'], money = form.cleaned_data['money'], year = form.cleaned_data['year'], owner = request.user, status = 1)
			newfile.save()
			return HttpResponseRedirect('/')
	else:
		form = UploadForm()
	return render(request, 'gallery/create.html', {'form':form,})
	
	
def handle_uploaded_file(path, f):
    with open(path, 'wb+') as destination:
        for chunk in f.chunks():
            destination.write(chunk)