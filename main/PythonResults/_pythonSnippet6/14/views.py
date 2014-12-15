# Create your views here.
from django.shortcuts import render_to_response, HttpResponse
from mysite.books.models import Book
from django import forms
from django.http import HttpResponseRedirect


class UploadFileForm(forms.Form):
	title = forms.CharField(max_length=50)
	file  = forms.FileField()



#import subprocess

#import cgi,os
#import cgitb; cgitb.enable()


#handle_uploaded_file(request.FILES['file'])


def test1(request):
	VAR0='0'
	VAR1='1'
	VAR2 = '2'
	if request.method == 'POST':
		form = UploadFileForm(request.POST, request.FILES)
   	#if form.is_valid():
		VAR0 = dir(form)
		VAR1 = request.FILES['file']
		VAR2 = dir(VAR1)
		VAR0 = VAR1.read()
		DestinationFile=open('/home/Masoud/cr100','w')
		for chunk in VAR1.chunks():
			DestinationFile.write(chunk)
		DestinationFile.close()
		return render_to_response('test1_results.html',{'var0':VAR0, 'var1': VAR1, 'var2': VAR2})
	else:
		return render_to_response('test1_form.html', {'error': True})




def test1_form(request):
	return render_to_response('test1_form.html')

def test1_a(request):
	if 'q' in request.GET and request.GET['q']:
        	q = request.GET['q']
        	return render_to_response('test1_results.html',{'query': q})
    	else:
        	return render_to_response('test1_form.html', {'error': True})












def search_form(request):
    return render_to_response('search_form.html')
	

def search(request):
	if 'q' in request.GET and request.GET['q']:
        	q = request.GET['q']
       		books = Book.objects.filter(title__icontains=q)
        	return render_to_response('search_results.html',{'books': books, 'query': q})
    	else:
        	return render_to_response('search_form.html', {'error': True})


