from django.template import RequestContext
from django.shortcuts import render_to_response
from django.contrib.auth import authenticate, login, logout
from django.http import HttpResponseRedirect, HttpResponse
from django.contrib.auth.decorators import login_required
from django.core.urlresolvers import reverse

from panda.forms import UserForm, UserProfileForm
from panda.models import Document
from panda.forms import DocumentForm

from os.path import join, abspath
from subprocess import Popen


def index(request):
	context = RequestContext(request)
	if request.method == 'POST':
		username = request.POST['username']
		password = request.POST['password']
		user = authenticate(username=username, password=password)
		
		if user:
			if user.is_active:
				login(request, user)
				return HttpResponseRedirect('')
			else:
				return HttpResponse("Your account is disabled.")
		else:
			print "Invalid login details: {0}, {1}".format(username, password)
			return HttpResponse("Invalid login details supplied.")
				
	else:
		return render_to_response('index.html', {}, context)

	
def register(request):
	context = RequestContext(request)
	registered = False

    # If it's a HTTP POST, we're interested in processing form data.
	if request.method == 'POST':
		user_form = UserForm(data=request.POST)
		user_form.fields['username'].help_text = None
		
		if user_form.is_valid():
			user = user_form.save()
			user.set_password(user.password)
			user.save()
			
			registered = True

		else:
			print user_form.errors

	else:
		user_form = UserForm()
		user_form.fields['username'].help_text = None

	return render_to_response(
			'register.html',
			{'user_form': user_form, 'registered': registered},
			context)

			
def user_login(request):
	context = RequestContext(request)
	
	if request.method == 'POST':
		username = request.POST['username']
		password = request.POST['password']
		user = authenticate(username=username, password=password)
		
		if user:
			if user.is_active:
				login(request, user)
				return HttpResponseRedirect(reverse('panda.views.index'))
			else:
				return HttpResponse("Your account is disabled.")
		else:
			print "Invalid login details: {0}, {1}".format(username, password)
			return HttpResponse("Invalid login details supplied.")
				
	else:
		return render_to_response('login.html', {}, context)
		
		
@login_required
def upload(request):
	context = RequestContext(request)
	if request.method == 'POST':
            form = DocumentForm(request.POST, request.FILES)
            if form.is_valid():
				for afile in request.FILES.getlist('docfile'):
					if afile.name[:17].lower() == 'prk daily extract':
						dir = 'extracts'
					else:
						dir = 'visits' 
					with open(join('files', dir, afile.name), 'wb+') as destination:
						destination.write(afile.read())
								
				return render_to_response('upload.html', {'message' : 'done'}, context)
				# return HttpResponseRedirect(reverse('panda.views.upload'))
        else:
            form = DocumentForm()
			
        documents = Document.objects.all()

	return render_to_response('upload.html', 
            {'document' : documents, 'form' : form}, 
            context)
			

@login_required
def run_query(request):
	context = RequestContext(request)
	print 'message'
	if request.method == 'POST':
		program_path = join('scripts', 'main.py')
		p = Popen('python ' + program_path)
		exit_code = p.wait()
        return render_to_response('query.html', {'message' : 256}, context)
	
	return render_to_response('query.html', {'message' : 0}, context)
	

@login_required
def view_data(request):
	context = RequestContext(request)
	if request.method == 'POST':
		# user selected the table they want to view (and subsetted)
		pass
	
	return render_to_response('view_data.html', {}, context)

		
@login_required
def user_logout(request):
    logout(request)
    return HttpResponseRedirect(reverse('panda.views.index'))
