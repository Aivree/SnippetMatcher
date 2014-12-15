from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from django.shortcuts import render, get_object_or_404, redirect,render,render_to_response
from forms import CreateUserForm,UploadForm
from models import FileUpload

from django.conf import settings

from datetime import date,datetime
import os
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.contrib import messages
# for superuser
# from django.contrib.auth.decorators import user_passes_test
# @user_passes_test(lambda u: u.is_superuser)

@login_required
def dashboard(request):
	return render(request,'dashboard.html')

@login_required
def password_change_done(request):
	return HttpResponseRedirect(reverse('dashboard'))


@login_required
def add_user(request):
	if request.POST:
		form = CreateUserForm(request.POST)
		if form.is_valid():
			username = form.cleaned_data['username']
			password = form.cleaned_data['password1']
			user = User.objects.create_user(username)
			user.set_password(password)
			user.save()
			messages.add_message(request, messages.INFO, "User . "+username+" Addeded Sucessfully")
			return HttpResponseRedirect(reverse('dashboard'))
	else:
		form = CreateUserForm()
	base_template = 'dashboard.html'
	return render(request,'fileupload/add_user.html',{'form':form, 'base_template':base_template })




@login_required
def add_user_fancy(request):
	if request.POST:
		form = CreateUserForm(request.POST)
		if form.is_valid():
			username = form.cleaned_data['username']
			password = form.cleaned_data['password1']
			user = User.objects.create_user(username)
			user.set_password(password)
			user.save()
			form = CreateUserForm()
			base_template = 'fancy.html'
			return render(request,'fileupload/add_user_fancy.html',
			{'form':form, 'base_template':base_template ,'message':"User "+user.username+" Added Sucessfully"})
	else:
		form = CreateUserForm()
	base_template = 'fancy.html'
	return render(request,'fileupload/add_user_fancy.html',{'form':form, 'base_template':base_template })

@login_required
def save_user(request):
	return render(request,'fileupload/add_user_sucess.html')


def handle_uploads(file_obj, filename):
	upload_filename=file_obj.name
	ext = "."+upload_filename.split('.')[-1]
	filename = datetime.now().strftime("%Y-%m-%d-%H-%M-%S")+filename+ext
	upload_full_path = os.path.join(settings.MEDIA_ROOT, 'documents')
	if not os.path.exists(upload_full_path):
		os.makedirs(upload_full_path)
	saved_file = open(os.path.join(upload_full_path, filename), 'wb')
	with saved_file:
		for chunk in file_obj.chunks():
			saved_file.write(chunk)
	return os.path.join(settings.MEDIA_URL, 'documents', filename)

@login_required
def upload_files(request):
	if request.method == 'POST':
		form = UploadForm(request.POST, request.FILES)
		if form.is_valid():
			print "form is valid"
			form_data = form.cleaned_data
			saved_file = handle_uploads(form_data['uploaded_file'], form_data['filename'])
			newdoc = FileUpload()
			newdoc.uploaded_file_url = saved_file
			newdoc.description = form_data['description']
			newdoc.filename = form_data['filename']
			newdoc.save()
			newdoc.allowed_users = form_data['allowed_users']
			newdoc.save()
			messages.add_message(request, messages.INFO, "File "+newdoc.filename+" Uploaded Sucessfully")
			return HttpResponseRedirect(reverse('uploaded_files'))
	form = UploadForm()
	base_template = 'dashboard.html'
	return render(request,'fileupload/upload_file.html',{'form':form, 'base_template':base_template })


@login_required
def upload_files_saved(request):
	return render(request,'fileupload/upload_file_sucess.html')

@login_required
def uploaded_files_without_pagination(request):
	if request.user.is_superuser:
		filelist = FileUpload.objects.filter(is_deleted=False).order_by('-uploaded_date')[:50]
	else:
		filelist = FileUpload.objects.filter(is_deleted=False,allowed_users = (request.user)).order_by('-uploaded_date')[:30]
		# filelist =request.user.files.all()#filter(is_deleted=False).order_by('-uploaded_date')[:30]
		# import pdb
		# pdb.set_trace() 

	# import pdb
	# pdb.set_trace()
	return render(request,'fileupload/uploaded_files.html',{'documents':filelist})


def delete_file(request,file_id):
	f = FileUpload.objects.get(pk =file_id)
	# upload_full_path = os.path.join(settings.MEDIA_ROOT, 'documents')
	# filename = f.uploaded_date.strftime("%Y-%m-%d-%H-%M-%S")+f.filename
	# del_file=os.path.join(upload_full_path,filename)
	# os.remove(del_file)
	f.is_deleted = True
	f.save()
	return HttpResponseRedirect(reverse('uploaded_files'))



from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger

@login_required
def uploaded_files(request):
	if request.user.is_superuser:
		filelist = FileUpload.objects.filter(is_deleted=False).order_by('-uploaded_date')[:50]
	else:
		filelist = FileUpload.objects.filter(is_deleted=False,allowed_users = (request.user)).order_by('-uploaded_date')[:30]

	paginator = Paginator(filelist, 10) # Show 2 contacts per page

	page = request.GET.get('page')
	try:
		filelist = paginator.page(page)
	except PageNotAnInteger:
		# If page is not an integer, deliver first page.
		filelist = paginator.page(1)
	except EmptyPage:
		# If page is out of range (e.g. 9999), deliver last page of results.
		filelist = paginator.page(paginator.num_pages)

	return render(request,'fileupload/uploaded_files.html', {"documents": filelist})

def list_user(request):
	user_list = User.objects.filter(is_active=True).exclude(is_superuser=True)
	paginator = Paginator(user_list, 10) # Show 2 contacts per page

	page = request.GET.get('page')
	try:
		user_list = paginator.page(page)
	except PageNotAnInteger:
		# If page is not an integer, deliver first page.
		user_list = paginator.page(1)
	except EmptyPage:
		# If page is out of range (e.g. 9999), deliver last page of results.
		user_list = paginator.page(paginator.num_pages)

	base_template = 'dashboard.html'
	return render(request,'fileupload/list_user.html',{'user_list':user_list, 'base_template':base_template })

def delete_user(request,user_id):
	u = User.objects.get(pk =user_id)
	u.is_active = False
	u.save()
	return HttpResponseRedirect(reverse('list_user'))
