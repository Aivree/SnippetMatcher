from django.core.context_processors import csrf
from django.shortcuts import render_to_response, render, redirect
from forms import UserCreationForm, UserChangeForm
from django.contrib import auth
from django.core.files.storage import default_storage
from django.core.files.base import ContentFile
from django import forms
from my_messages.models import Message
from my_messages.views import get_friend_list
from rating.views import get_rating
from django.core.files.temp import NamedTemporaryFile
from django.db import models
from my_registration.models import User
from django.core.files import File
from django.db import models
from my_registration.models import get_upload_file_name
from data_store import profession_list

# ===================== Start block for UPLOAD FILE =====================
def upload_file(request):
    #Add check: if upload file is picture - download, else - ignored
    file = request.FILES['avatar']
    #windows mode:
    default_storage.save("E:/reg_extend_2/static/img/avatars/%s"%(file), ContentFile(file.read()))
    #linux mode:
    #default_storage.save("E:/reg_extend_2/static/img/avatars/%s"%(file), ContentFile(file.read()))
    return str(file)
# ===================== End block for UPLOAD FILE =====================

# Create your views here.
def login(request):
    if request.method == "POST":
        username = request.POST["username"]
        password = request.POST["password"]
        print username
        print password
        user = auth.authenticate(username=username, password=password)
        if user is not None and user.is_active:
            auth.login(request, user)
            return render(request, "index.html")
    c = {}
    c.update(csrf(request))
    return render_to_response("login.html", c)

def index(request):
    if request.user.is_authenticated():
        #Standart data-complect
        all_messages = Message.objects.filter(message_from_user_id = request.user) | Message.objects.filter(message_to_user_id = request.user)
        all_specialist = User.objects.all()
        all_dialogs = get_friend_list(request.user)
        #End of Standart data-complect
        #Rating calculate
        if get_rating(request) is None:
            my_rating = 0
        else:
            my_rating = get_rating(request)

        #End Rating calculate
        c = {}
        c.update(csrf(request))
        return render(request, "index.html", {'all_messages_in_page':all_messages, 'all_specialist_in_page':all_specialist, 'all_dialogs_in_page':all_dialogs, 'rating_in_page':my_rating})
    return redirect("login")

def registration(request):
    if request.method == "POST" :
        form = UserCreationForm(request.POST, request.FILES)
        if form.is_valid():
            print "form is valid"
            form.save()
            return render_to_response("action_succes.html")
    else:
        form = UserCreationForm()

    c = {}
    c.update(csrf(request))
    #args["form"] = UserCreationForm(request.POST)
    #return render_to_response("registration.html", args)
    return render(request, "registration.html", {'form':form})

def logout(request):
    auth.logout(request)
    return render_to_response("index.html")

def show_personal_information(request):
    if request.user.is_authenticated():
        print "views: personal_information, user is auth"
        return render(request, "personal_information.html")
    return render_to_response("index.html")

def edit_personal_information(request):
    if request.method == "POST" :
        #If user change the avatar
        if "avatar" in request.FILES:
            avatar = upload_file(request)
            request.user.avatar = avatar
        request.user.username = request.POST["username"]
        request.user.profession = request.POST["search_specialist"]
        request.user.coordinate_x = request.POST["coordinate_x"]
        request.user.coordinate_y = request.POST["coordinate_y"]
        request.user.profile_vkontakte = request.POST["profile_vkontakte"]
        request.user.save()
        return redirect("/personal_information")
    c = {}
    c.update(csrf(request))
    return render(request, "edit_personal_information.html")

# ================================= Block code of UPLOAD FILE =================================
from django.core.files.storage import default_storage
from django.core.files.base import ContentFile
def test_edit_info(request):


    if request.method == 'POST':
        if 'avatar' in request.FILES:
            file = request.FILES['avatar']
            #windows mode:
            default_storage.save("E:/reg_extend_2/static/img/avatars/%s"%(file), ContentFile(file.read()))
            #linux mode:
            #default_storage.save("E:/reg_extend_2/static/img/avatars/%s"%(file), ContentFile(file.read()))
    c = {}
    c.update(csrf(request))
    return render(request, "test_edit_personal_information.html")