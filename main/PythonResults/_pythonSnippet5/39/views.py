#encoding:utf-8
from django.shortcuts import render, render_to_response, get_object_or_404, redirect
from django.template import RequestContext
from django.contrib.auth import login, logout, authenticate
from django.contrib.auth.models import User
from django.http import HttpResponseRedirect
from CRUD.apps.home.forms import LoginForm, RegisterForm, UploadForm
from CRUD.apps.home.models import Upload
from django.contrib.auth.decorators import login_required
from django.contrib.admin.views.decorators import staff_member_required
from django.core.urlresolvers import reverse
from CRUD.apps.home.generate import *
from .DataBase import DataBase

def home(request):
	return render_to_response('home/index.html', context_instance=RequestContext(request))

def login_view(request):
	mensaje = ""
	if request.user.is_authenticated():
		return HttpResponseRedirect('/')
	else:
		if request.method == 'POST':
			form = LoginForm(request.POST)
			if form.is_valid():
				username = form.cleaned_data['username']
				password = form.cleaned_data['password']
				usuario = authenticate(username=username,password=password)
				if usuario is not None and usuario.is_active:
					login(request,usuario)
					return HttpResponseRedirect('/')
				else:
					mensaje = "Usuario y/o contraseña incorrecto"
		form = LoginForm()
		ctx = {'form':form,'mensaje':mensaje}
		return render_to_response('home/login.html',ctx,context_instance=RequestContext(request))

@login_required
def logout_view(request):
	logout(request)
	return HttpResponseRedirect('/')

def nuevo_usuario(request):
	formulario = RegisterForm()
	if request.method == 'POST':
		formulario = RegisterForm(request.POST)
		if formulario.is_valid():
			nombre = formulario.cleaned_data['first_name']
			apellido = formulario.cleaned_data['last_name']
			usuario = formulario.cleaned_data['username']
			#administrador = formulario.cleaned_data['is_staff'],
			email = formulario.cleaned_data['email']
			password_one = formulario.cleaned_data['password_one']
			password_two = formulario.cleaned_data['password_two']
			u = User.objects.create_user(first_name=nombre,last_name=apellido,username=usuario,email=email,password=password_one)
			u.save()
			return HttpResponseRedirect('/adminusers')
		else:
			ctx = {'formulario':formulario}
			return render_to_response('home/nuevousuario.html',ctx, context_instance=RequestContext(request))
	else:
		ctx = {'formulario':formulario}
	return render_to_response('home/nuevousuario.html',ctx, context_instance=RequestContext(request))

def obtenerUsuarios():
	try:
		usuarios = User.objects.all()
	except User.DoesNotExist:
		usuarios = None
	return usuarios

@staff_member_required
def adminusers(request):
	usuarios = User.objects.all()
	return render_to_response('home/adminusers.html',{'usuarios':usuarios}, context_instance=RequestContext(request))

def borrarUsuario(request, usuario):
	try:
		user = User.objects.get(username=usuario)
		if user:  # delete if only exists
			user.delete()
	except User.DoesNotExist:  # catch the DoesNotExist error
		user = None
	return HttpResponseRedirect("/adminusers")

def editarUsuario(request, usuario):
	usuario = User.objects.get(username=usuario)
	if request.method == "POST":
		form = RegisterForm(instance=request.user)
		if form.is_valid():
			nombre = form.cleaned_data['nombre'],
			apellido = form.cleaned_data['apellido'],
			usuario = form.cleaned_data['usuario'],
			email = form.cleaned_data['email'],
			password = form.cleaned_data['password'],
			usuario.first_name = nombre
			usuario.last_name = apellido
			usuario.username = usuario
			usuario.email = email
			usuario.password = password
			usuario.save()
			return HttpResponseRedirect('/usuario/%s'%usuario.username)
	if request.method == "GET":
		form = RegisterForm(initial={
			'nombre':usuario.first_name,
			'apellido':usuario.last_name,
			'usuario':usuario.username,
			'email':usuario.email,
			'password':usuario.password,
			})
	ctx = {'form':form, 'usuario':usuario}
	return render_to_response('home/editarusuario.html',ctx,context_instance=RequestContext(request))

@login_required
def admintables(request):
    # Handle file upload
    if request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)
        if form.is_valid():
            newdoc = Upload(docfile = request.FILES['docfile'])
            newdoc.save()
            db_name, media_filename = exec_sql_file(request.user, request.FILES['docfile'])
            #return HttpResponseRedirect('/personalize/' + db_name)
            # Redirect to the document list after POST
            #return HttpResponseRedirect('/admintables')
            return redirect(reverse(personalize, args=(db_name,)))
            #return HttpResponseRedirect(reverse('home.views.admintables'))
    else:
        form = UploadForm() # A empty, unbound form

    # Load documents for the list page
    documents = Upload.objects.all()
    # Render list page with the documents and the form
    

    return render_to_response(
    	'home/admintables.html',
        {'documents': documents, 'form': form},
        context_instance=RequestContext(request)
    )

@login_required()
def personalize(request, name):
    if name:
        conn = DataBase(name=name)
        tables = []
        for t in conn.show_tables():
            tables.append({"name": t, "columns":  conn.show_fields(table=t)})
        return render(request, "home/personalize.html", locals())
    else:
        raise Http404


@login_required
def delete(request):
    if request.method != 'POST':
        raise HTTP404

    docId = request.POST.get('docfile', None)
    docToDel = get_object_or_404(Upload, pk = docId)
    docToDel.docfile.delete()
    docToDel.delete()
    return HttpResponseRedirect('/admintables')
    #return HttpResponseRedirect(reverse('home.views.admintables'))

@login_required()
def inspectdb(request, id_db):
    if id_db:
        obj = DataBaseTmp.objects.get_or_none(id=id_db)
        if obj:
            filename = create_app(request.user, obj.filename, obj.db_name)
            return redirect(settings.MEDIA_URL + filename)
        else:
            raise Http404
    else:
        raise Http404