from PIL import Image as PImage
from simple.settings import MEDIA_URL
import urlparse
from forms import ProfileForm, PostForm, ForumForm, ThreadForm
from django.http import HttpResponseRedirect, HttpResponse
from django.shortcuts import render, get_object_or_404
from forum.models import Forum, Thread, Post, UserProfile
from django.core.urlresolvers import reverse
from django.contrib.auth import authenticate
from django.contrib.auth import login as auth_login
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from django.views.generic.edit import FormView
from django.template.loader import get_template

# Create your views here.
def login(request):
	username =request.POST['username']
	password = request.POST['password']
	user = authenticate(username=username, password=password)
	if user is not None:
		if user.is_active:
			auth_login(request, user)
			return HttpResponseRedirect(reverse("userProfile"))

def logout(request):
	logout(request)

def signUp(request):
	modelform = ProfileForm()
	if request.method == 'POST':
		modelform = ProfileForm(request.POST)
	if modelform.is_valid():
		first_name = request.POST['first_name']
		last_name = request.POST['last_name']
		username = request.POST['username']
		email = request.POST['email']
		password = request.POST['password']
		# We'll do the password check in a second
		u = User.objects.create_user(first_name = first_name, last_name = last_name, email = email, password = password, username=username)	
		u.save()
		user = authenticate(username=username, password=password)
		if user is not None:
			if user.is_active:
				auth_login(request, user)
				
		return HttpResponseRedirect(reverse("editProfile"))
	return render(request, 'signUp.html', {'modelform': modelform})

def index(request):
	return render(request, "index.html",)

def forumHome(request):
	all_forums = Forum.objects.all()
	return render(request, 'forums.html', {'all_forums': all_forums})

def documents(request):
	all_documents = Forum.objects.all()
	return render(request, "documents.html", {'all_documents': all_documents})

def newForum(request):
	modelform = ForumForm()
	if request.method == 'POST':
		modelform = ForumForm(request.POST)
	if modelform.is_valid():
		title = modelform.cleaned_data['title']
		newforum = Forum(title = title)
		newforum.save()
		return HttpResponseRedirect("/forum/forumHome/")
	return render(request, 'newForum.html', {'modelform': modelform})

def forumView(request,forum_id):
	forum = get_object_or_404(Forum, pk=forum_id)
	all_threads = Thread.objects.filter(forum=forum).all()
	return render(request, 'forumView.html', {'forum': forum, 'all_threads': all_threads})

def threadView(request,thread_id):
	thread = get_object_or_404(Thread, pk=thread_id)
	all_posts = Post.objects.filter(thread=thread).all()
	return render(request, 'threadView.html', {'thread': thread, 'all_posts': all_posts})

def newThread(request, forum_id):
	modelform = ThreadForm()
	if request.method == 'POST':
		modelform = ThreadForm(request.POST)
	if modelform.is_valid():
		title = modelform.cleaned_data['title']
		forum = get_object_or_404(Forum, pk=forum_id)
		newthread = Thread(title = title, forum = forum)
		newthread.save()
		return HttpResponseRedirect("/forum/forumView/%s/" % forum_id)
	return render(request, 'newThread.html', {'modelform': modelform})

def reply(request, thread_id):
	modelform = PostForm()
	if request.method == 'POST':
		modelform = PostForm(request.POST)
	if modelform.is_valid():
		title = modelform.cleaned_data['title']
		body = modelform.cleaned_data['body']
		thread = get_object_or_404(Thread, pk=thread_id)
		newpost = Post(title = title, body = body, creator = request.user, thread = thread )
		newpost.save()
		return HttpResponseRedirect("/forum/threadView/%s/" % thread_id)
	return render(request, 'reply.html', {'modelform': modelform})


def userProfile(request):
	user = get_object_or_404(User, pk=request.user.id)
	profile = UserProfile.objects.get_or_create(user=user)[0]
	return render(request, 'userProfile.html', {'profile': profile})


def editProfile(request):
	user = get_object_or_404(User, pk=request.user.id)
	profile = UserProfile.objects.filter(user=user).first()
	return render(request, 'profile.html', {'profile': profile})

	if request.method=="POST":
		img = UploadForm(request.POST, request.FILES)       
		if img.is_valid():
			img.save()
			return HttpResponseRedirect(reverse('imageupload'))
		else:
			img = UploadForm()
			images = Upload.objects.all()
			return render(request,'profile.html',{'form':img,'images':images})   


	# def modelform_valid(self, modelform):
		# """Resize and save profile image."""
		# remove old image if changed
		# name = modelform.cleaned_data.get("avatar")
		# pk   = self.kwargs.get("mfpk")
		# old  = UserProfile.obj.get(pk=pk).avatar

		# if old.name and old.name != name:
			# old.delete()

        # # save new image to disk & resize new image
        # self.modelform_object = modelform.save()
        # if self.modelform_object.avatar:
        # 	img = PImage.open(self.modelform_object.avatar.path)
        # 	img.thumbnail((160,160), PImage.ANTIALIAS)
        # 	img.save(img.filename, "JPEG")
        # return redir(self.success_url)


def forum_context(request):
    return dict(media_url=MEDIA_URL)


    
