# Create your views here.
from django.http import HttpResponseRedirect
from django.template import RequestContext
from django.core.urlresolvers import reverse
from django.shortcuts import *
from models import *
from forms import *

def home(request):
	categorias= Categoria.objects.all()
	documentos = Documento.objects.all()
	
	return render_to_response('pagina.html', locals())

def home2(request):
	# documentos= Documento.objects.objects.all()
	categorias= Categoria.objects.all()
	documentos = Documento.objects.all()
	return render_to_response('pagina2.html',locals())

def home3(request):
	categorias= Categoria.objects.all()
	documentos = Documento.objects.all()
	
	return render_to_response('index.html', locals())

#union de lista y prueba 3
def home4(request):
	if request.method == 'POST':
		form = DocumentForm()
		if form.is_valid():
			newdoc = Documento()
			newdoc.save()
			return HttpResponseRedirect('/gracias/')
	
	documentos = Documento.objects.all()
	categorias = Categoria.objects.all()
	return render_to_response('index.html', locals())

def lista(request):
	if request.method == 'POST':
		form = DocumentForm(request.POST, request.FILES)
		if form.is_valid():
			newdoc = Documento(archivo = request.FILES['archivo'])
			newdoc.save()

			return HttpResponseRedirect(reverse("lista"))
			# return redirect("lista")

	else:
		form = DocumentForm()

	documentos = Documento.objects.all()

	return render_to_response('list.html', locals())
	

