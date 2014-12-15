from django.shortcuts import render_to_response
from django.http import HttpResponseRedirect
from django.contrib import auth
from django.core.urlresolvers import reverse
from django.core.context_processors import csrf
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.decorators import login_required
from django.template import Context,loader,RequestContext


from .getweather import getweather, yahoow, getww
from .getW4ny import yahony, noaany, weatny
from .getW4la import yahola, noaala, weatla
from .getW4ho import yahoho, noaaho, weatho
from .getW4sa import yahosa,noaasa, weatsa
from .Myform import MyRegistrationForm
from .UpdateuserFrom import UserUpdateFor

from os import environ
import cgi,cgitb
import smtplib

from Skygraph.models import Userbkp
from Skygraph.models import Document
from Skygraph.forms import DocumentForm

# Create your views here.

def login(request):
    c = {}
    c.update(csrf(request))
    return render_to_response('Login/login.html',c)

def auth_view(request):
    username = request.POST.get('username','')
    password = request.POST.get('password','')
    user = auth.authenticate(username=username,password=password)
    if user is not None:
        auth.login(request,user)
        return HttpResponseRedirect('/accounts/loggedin')

    else:
        return HttpResponseRedirect('/accounts/invalid')

@login_required
def loggedin(request):
    return render_to_response('Login/loggedin.html',{'is_auth' : request.user.is_authenticated(), 'full_name': request.user.username},
                            context_instance=RequestContext(request) )

@login_required
def areal(request):
    return  render_to_response('Weather/areal.html' ,{'is_auth' : request.user.is_authenticated(), 'full_name': request.user.username ,
    'result2': yahoow(), 'data2' : getww(), 'data4' : getweather()},
                            context_instance=RequestContext(request))
def share(request):

    Userp = Userbkp.objects.get( Uname = request.user.username)
    uemail = Userp.Email
    session = smtplib.SMTP('smtp.gmail.com', 587)
    session.ehlo()
    session.starttls()
    session.login('skygraphuic@gmail.com', 'Skygraph@uic2014')
    headers = "\r\n".join(["from: " + 'skygraphuic@gmail.com',
                       "subject: " + 'SkyGraph - Weather',
                       "to: " + uemail,
                       "mime-version: 1.0",
                       "content-type: text/html"])
    forecast = yahoow()
    content = headers + "\r\n\r\n" + forecast['title'] + '<br/>'+forecast['html_description']
    session.sendmail('skygraphuic@gmail.com', uemail, content)
    return render_to_response("Weather/share.html", {'is_auth' : request.user.is_authenticated(),
                                                     'full_name': request.user.username},
                              context_instance=RequestContext(request))
    #return render_to_response('Weather/share.html',{'is_auth' : request.user.is_authenticated(), 'full_name': request.user.username ,
    #'result2': yahoow()},context_instance=RequestContext(request) )

def export(request):
    return render_to_response("/export.html", {'is_auth' : request.user.is_authenticated(), 'full_name': request.user.username ,
                    },context_instance=RequestContext(request))


def newyl(request):
    return render_to_response("Weather/newyl.html", {'is_auth' : request.user.is_authenticated(), 'full_name': request.user.username ,
                    'ryahony': yahony(), 'rnoaany' : noaany(), 'rweatny': weatny()},context_instance=RequestContext(request))

def losal(request):
    return render_to_response("Weather/losal.html", {'is_auth' : request.user.is_authenticated(), 'full_name': request.user.username ,
                                                     'ryahola': yahola(), 'rnoaala' : noaala(), 'rweatla': weatla()},
                              context_instance=RequestContext(request))

def housl(request):
    return render_to_response("Weather/housl.html", {'is_auth' : request.user.is_authenticated(), 'full_name': request.user.username ,
                                                     'ryahoho' : yahoho(),'rnoaaho':noaaho(),'rweatho':weatho()},
                              context_instance=RequestContext(request))

def sanfl(request):
    return render_to_response("Weather/sanfl.html", {'is_auth' : request.user.is_authenticated(),
                                                     'full_name': request.user.username ,'ryahosa' : yahosa(),'rnoaasa':noaasa(),
                                                     'rweatsa':weatsa()},
                              context_instance=RequestContext(request))

def widgets(request):
    return render_to_response("Weather/widgets.html", {'is_auth' : request.user.is_authenticated(),
                                                     'full_name': request.user.username},
                              context_instance=RequestContext(request))


def invalid_login(request):
    return render_to_response('Login/invalid_login.html')

@login_required
def logout(request):
    auth.logout(request)
    return render_to_response('Login/logout.html')

'''def register_user(request):
    if request.method == 'POST':
        form = UserCreationForm(request.POST)
        if form.is_valid():
            form.save()

            return HttpResponseRedirect('/accounts/register_success')

    args = {}
    args.update(csrf(request))
    args['form'] = UserCreationForm()
    return render_to_response('Login/register.html', args)'''

def register_user(request):
    if request.method == 'POST':
        form = MyRegistrationForm(request.POST)
        if form.is_valid():
            form.save()
            return HttpResponseRedirect('/accounts/register_success')
    args = {}
    args.update(csrf(request))
    args['form'] = MyRegistrationForm()
    return render_to_response('Login/register.html',args)

@login_required
def editacc(request):
    return render_to_response("Weather/editacc.html", {'is_auth' : request.user.is_authenticated(),'first_name' : request.user.username},
                              context_instance=RequestContext(request))

def edituser(request):

    if request.method == 'POST':
        form = UserUpdateFor(request.POST)
        if form.is_valid():
            Userp = Userbkp.objects.get( Uname = request.user.username)
            Userp.Fname = form.cleaned_data['nfname']
            Userp.Lname = form.cleaned_data['nlname']
            Userp.City = form.cleaned_data['ncity']
            Userp.save()

            return HttpResponseRedirect('/accounts/loggedin')
        else:
            return HttpResponseRedirect('/Weather/editacc')

def edit2(request):
    return render_to_response('Login/loggedin.html')


def register_success(request):
    return render_to_response('Login/register_success.html', {'uname' : request.user.username})

def home(request):
    #return render_to_response("weather/home.html", {'lines' : Line.objects.all()})
    return render_to_response("Weather/home.html")

def area(request):
    return render_to_response("Weather/area.html", {'result2': yahoow(), 'data2' : getww(), 'data4' : getweather()})

def newy(request):
    return render_to_response("Weather/newy.html", {'ryahony': yahony(), 'rnoaany' : noaany(), 'rweatny': weatny()})

def losa(request):
    return render_to_response("Weather/losa.html", {'ryahola': yahola(), 'rnoaala' : noaala(), 'rweatla': weatla()})

def hous(request):
    return render_to_response("Weather/hous.html", {'ryahoho' : yahoho(),'rnoaaho':noaaho(),'rweatho':weatho()})

def sanf(request):
    return render_to_response("Weather/sanf.html", {'ryahosa' : yahosa(),'rnoaasa':noaasa(),'rweatsa':weatsa()})

def help(request):
    return render_to_response("Weather/help.html")

def helpl(request):
    return render_to_response("Weather/helpl.html")

def data(request):
    return render_to_response("Weather/data_hotdog.html")



def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Document(docfile = request.FILES['docfile'])
            newdoc.save()

            # Redirect to the document list after POST
            return HttpResponseRedirect(reverse('Skygraph.views.list'))
    else:
        form = DocumentForm() # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        "list.html",
        {'documents': documents, 'form': form},
        #context_instance=RequestContext(request),
        RequestContext(request, {})
    )

def account(request):
    #user = super(MyRegistrationForm, self).save(commit = False)
    #user.email = self.cleaned_data['email']
    #user.first_name = self.cleaned_data['first_name']
    #user.last_name = self.cleaned_data['last_name']
    #user.city = self.cleaned_data['city']
    #usern = User.objects.filter(last_name = 'eleven')
    #print usern.email

    Userp = Userbkp.objects.all()
    return render_to_response("Weather/account.html" , {'is_auth' : request.user.is_authenticated(),'first_name' : request.user.username, 'allobj': Userp},
                              context_instance=RequestContext(request))



def compare(request):
    documents = Document.objects.all()
    return render_to_response("compare.html", {'documents': documents})

def export(request):
    documents = Document.objects.all()
    return render_to_response("export.html", {'documents':documents})
