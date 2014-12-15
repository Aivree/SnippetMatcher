#_*_coding:utf-8_*_ 

from django.shortcuts import render_to_response, render
from django.template import RequestContext
from django.http import HttpResponse
from blog.models import News

def post(request):

	if request.method == "POST":
		title = request.POST.get('title','')
		content = request.POST.get('content','')
		img = request.FILES['img']
		s = img.read()
		News.objects.create(title=title,content=content,img=img)
		return HttpResponse("<p>%s</p>" % s)

	return render(request, 'post.html',{})

def hello(request):

	pic = News.objects.get(id=1)

	return render(request,'hello.html',{'pic':pic})