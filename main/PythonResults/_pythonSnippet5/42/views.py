from django.shortcuts import render,render_to_response
from django.contrib.auth import login,authenticate
from django.http import HttpResponseRedirect,HttpResponse
from django.contrib.auth import REDIRECT_FIELD_NAME, login as auth_login, logout, get_user_model
from .models import MyUser,notice
from forms import SignupForm,LoginForm,UploadForm,PostForm,SearchForm
from django.core.urlresolvers import reverse
from django.template import RequestContext
from django.contrib import messages
from django.db.models import Q
# Create your views here.
# def home(request):
#     if request.user.is_authenticated():
#         current_user= request.user
#         notices = notice.objects.order_by('-time')
#         users=MyUser.objects.all()
#         count=MyUser.objects.count()
#         return render(request,'welcome.html',{'current_user':current_user,'notices':notices,'users':users,'count':count})
#     else:
#         form = LoginForm
#         text="Hello, anonymous"
#         return render(request,'index.html',{'text':text,'form':form})

def home(request):
    if request.user.is_authenticated():
        current_user= request.user
        notices = notice.objects.order_by('-time')
        count=MyUser.objects.count()
        searchform=SearchForm()
        if request.method=="POST":
            #form= SearchForm(request.POST)
            query=request.POST['searchname']
            results = MyUser.objects.filter(Q(firstname__icontains=query) | Q(lastname__icontains=query) | Q(mobile__icontains=query)).order_by('date_of_birth')
            return render(request,'welcome.html',{'current_user':current_user,'notices':notices,'users':results,'count':count,'searchform':searchform})
        else:

            users=MyUser.objects.all()
            return render(request,'welcome.html',{'current_user':current_user,'notices':notices,'users':users,'count':count,'searchform':searchform})

    else:
        form = LoginForm
        text="Hello, anonymous"
        return render(request,'index.html',{'text':text,'form':form})

def signup(request):
    if not request.user.is_authenticated():
        if request.method=="POST":
            form= SignupForm(request.POST,request.FILES)
            if form.is_valid():
                password=request.POST['password']
                password1=request.POST['password1']
                if(password==password1):
                    #new_user = MyUser.objects.create_user(**form.cleaned_data)
                    email=request.POST['email']
                    firstname=request.POST['firstname']
                    lastname=request.POST['lastname']
                    mobile=request.POST['mobile']
                    date_of_birth=request.POST['date_of_birth']
                    new_user = MyUser.objects.create_user(email,firstname,lastname,mobile,date_of_birth,password)
                    MyUser.backend='django.contrib.auth.backends.ModelBackend'
                    authenticate()
                    login(request,new_user)
                    return HttpResponseRedirect('/')
                else:
                    return HttpResponse("Your password don't match please try again")
            else:
                return render(request,'adduser.html',{'form':form})
        else:
            form = SignupForm()
        return render(request,'adduser.html',{'form':form})
    else:
        return HttpResponseRedirect('/')    

def user_login(request):
    if not request.user.is_authenticated():
        context = RequestContext(request)
        if request.method == "POST":
            email = request.POST['email']
            password = request.POST['password']
            user = authenticate(email=email, password=password)
            if user:
                if user.is_active:
                    authenticate()
                    login(request,user)
                    return HttpResponseRedirect('/')
                else:
                    return HttpResponse("Your account is disabled")
            else:
                return HttpResponse("Invalid login")
        else:	
            return render_to_response("login.html",{},context)
    else:
        return HttpResponseRedirect('/')

def user_logout(request):
	if request.user.is_authenticated():
		logout(request)
		return HttpResponseRedirect("/")
	else:
		return HttpResponse("You have to be logged in before getting to logout.")

def profile(request):
    if request.user.is_authenticated():
        current_user= request.user
        if request.method == "POST":
            img = UploadForm(request.POST, request.FILES) 
            if img.is_valid():
                request.user.image=img.cleaned_data['image']
                request.user.save()
                messages.success(request, 'Profile details updated.')
        else:
            img=UploadForm()
        return render(request,'profile.html',{'current_user':current_user,'form':img})
    else:
        text="Hello, anonymous"
        return render(request,'index.html',{'text':text})


def about(request):
    return render(request,'about.html',{})

# def imageupload(request):
#     if request.method=="POST":
#         img = UploadForm(request.POST, request.FILES)       
#         if img.is_valid():
#             request.user.image=img.cleaned_data['image']
#             request.user.save()
#             return HttpResponse('image upload success')
#     else:
#         img=UploadForm()
#     images=MyUser.objects.all()
#     return render(request,'upload.html',{'form':img,'images':images})

def addpost(request):
    if request.user.is_authenticated():
        if request.method=="POST":
            form= PostForm(request.POST)
            if form.is_valid():
                user=request.user
                topic=form.cleaned_data['topic']
                message=form.cleaned_data['message']
                newmessage=notice(topic=topic,message=message,user=user)
                newmessage.save()
                return HttpResponseRedirect('/')
        else:
            form = PostForm()
        return render(request,'addpost.html',{'form':form})
    else:
        return HttpResponseRedirect('login') 