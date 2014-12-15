# Create your views here.
from mongoengine import *
from django.shortcuts import render
import bson
from django.http import HttpResponse
connect('InterviewZone')
from django.core.files.base import ContentFile
from hack.models import Users, Problems
from django import forms

def index(request):
	context = {}
	return render(request, 'hack/index.html', context)


def solve(request):
	problems = Problems.objects.all()
	context = {"problems":problems}
	return render(request, 'hack/solve.html', context)


def problem(request, p_id):
	pid = bson.ObjectId(str(p_id))
	p = Problems.objects.get(id=pid)
	context = {"p":p}
	return render(request, 'hack/problem.html', context)


def download(request, p_id):
	pid = bson.ObjectId(str(p_id))
	p = Problems.objects.get(id=pid)
	myfile = ContentFile(p.problem_input_file.read())
	content_type = p.problem_input_file.content_type
	response = HttpResponse(myfile, content_type=content_type)
	response['Content-Length'] = myfile.size
	response['Content-Disposition'] = 'attachment; filename="input.txt"'
	return response


class UploadFileForm(forms.Form):
	file = forms.FileField()

def upload(request, p_id):	
	
	if request.method == 'POST':
		pid = bson.ObjectId(str(p_id))
		p = Problems.objects.get(id=pid)
		myfile = p.problem_output_file.read()

		form = UploadFileForm(request.POST, request.FILES)
		if form.is_valid():
			#handle_uploaded_file(request.FILES['file'])
			
			Userfile = request.FILES['file'].read()
			if myfile == Userfile:
				return HttpResponse("Correct Solution")
			else:
				return HttpResponse("Wrong Solution"+"<br>"+"Your Solution<pre>" + str(Userfile)  + "</pre><br>Expected:<pre>" + str(myfile) + "</pre>")
	else:
		form = UploadFileForm()

	return render(request, 'hack/upload.html', {'form':form})
	
	
def handle_uploaded_file(files):
	pass

def contact(request):
	context = {}
	return render(request, 'hack/contact.html', context)


def about(request):
	context = {}
	return render(request, 'hack/about.html', context)
