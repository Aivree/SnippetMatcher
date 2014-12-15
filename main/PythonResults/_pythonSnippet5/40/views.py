from django.http import HttpResponse, Http404, HttpResponseRedirect
from django.shortcuts import render
from django.contrib.auth.models import User
from django.db import IntegrityError
from django.contrib.auth import authenticate,login,logout
from django.shortcuts import get_object_or_404
from django.contrib.auth.decorators import login_required
from django.core.urlresolvers import reverse

from appsud.forms import RegisterForm,UploadForm
from appsud.models import UserProfile,ApplicationDetails,AndroidVersion


def index(request):
    if request.user.is_authenticated():
        return HttpResponseRedirect(reverse(home))
    else:
        return render(request,'index.html')
        

def register(request):
    if request.method == 'POST': # If the form has been submitted...
        form = RegisterForm(request.POST,request.FILES) # A form bound to the POST data
        if form.is_valid(): 
            username = request.POST['username']
            password = request.POST['password']
            email = request.POST['email']
            country = request.POST['choice_field']
            phone = request.POST['phone']
            website = request.POST['website']
            propic = request.FILES['propic']
            try:
                user = get_object_or_404(User,email=email)
                return HttpResponse('username already exists') 
            except Http404:
                user = User.objects.create_user(username,email,password)
                p = UserProfile(user=user,phone=phone,country=country,website=website,profile_picture=propic)
                p.save()
                user = authenticate(username=username,password=password)
                login(request, user)
                return HttpResponseRedirect(reverse(home))
    else:
        form = RegisterForm() # An unbound form
    return render(request, 'register.html', {'form': form})

def authentication(request):
    username = request.POST['username']
    password = request.POST['password']
    user = authenticate(username=username,password=password)
    if user is not None:
        if user.is_authenticated():
            login(request, user)
            return HttpResponseRedirect('/home/')
    else:
        result = "invalid username and password"
        return HttpResponse(result)


@login_required
def home(request):
    propic = request.user.get_profile().profile_picture
    app_details = ApplicationDetails.objects.all()
    return render(request,'home.html', {'profile_picture':propic,'app_details':app_details})

def user_logout(request):
    logout(request)
    return HttpResponseRedirect(reverse(index))
    
@login_required
def upload_application(request):
    if request.method == 'POST':
        form = UploadForm(request.POST,request.FILES)
        if form.is_valid():
            app_name = request.POST['app_name']
            version = request.POST['version']
            category = request.POST['category']
            license = request.POST['license']
            description = request.POST['description']
            android_version = request.POST.getlist('android_version')
            banner = request.FILES['display_banner']
            screenshot1 = request.FILES['screenshot1']
            screenshot2 = request.FILES['screenshot2']
            screenshot3 = request.FILES['screenshot3']
            apk = request.FILES['apk']
            user = request.user
            p = ApplicationDetails(user=user,name=app_name,version=version,category=category,license=license,description=description,banner=banner,screenshot1=screenshot1,screenshot2=screenshot2,screenshot3=screenshot3,apk=apk)
            p.save()
            for android in android_version:
                t = AndroidVersion.objects.get(android_version=android)
                p.android_version.add(t)
            return HttpResponse('hello')
    else:
        form = UploadForm()
    return render(request,'upload.html',{'form':form})


@login_required
def edit_profile(request):
    user = request.user
    p = user.get_profile()
    if request.method == 'POST':
        phone = request.POST['phone']
        website = request.POST['website']
        try:
            propic = request.FILES['propic']
        except:
            p.phone = phone
            p.website = website
            p.profile_picture = propic
            p.save()
            return HttpResponse('hello')
    else:
        return render(request,'edit_profile.html',{'profile':p})

@login_required
def app_details(request,app_id):
    details = ApplicationDetails.objects.get(id=app_id)
    print details.name
    return HttpResponse('success')




    

