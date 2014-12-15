#!/usr/bin/env python
# -*- coding:utf-8 -*-

import datetime
from django.http import HttpResponse, HttpResponseRedirect
from django.template.loader import get_template
from django.template import RequestContext
from django.core.urlresolvers import reverse

from django.shortcuts import render_to_response

from FKN_Workgroup.models import MessTheme, Message, UploadFile, Project_Group, Tasks, Us_Pr_Role

from django.views.decorators.csrf import csrf_protect
from django.views.generic.simple import direct_to_template
from forms import *
from message.forms import MessageForm
from django.shortcuts import get_object_or_404
from django.contrib import messages

from django.core.paginator import Paginator,EmptyPage, PageNotAnInteger

def new_task(request, nomber):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
        
    errors = []
    project = Project_Group.objects.filter(id=nomber)[0]
    #context = { 'form': form,  'errors':errors, 'user': request.user}
    
#    joe = Author.objects.create(name="Joe")
#    entry.authors.add(joe)    
    
    if request.method == 'POST': 
        form = NewTaskForm(request.POST)
        if form.is_valid():
            title = form.cleaned_data.get('Title', None)
            descr = form.cleaned_data.get('Description', None)
            
            mt = MessTheme(Title = "first")
            mt.save()
            task = Tasks(Create_date = datetime.datetime.now(),Last_change_date=datetime.datetime.now(),Title = title, Description = descr, Priority=1, State_id = 1, Project=project, MessTheme = mt)
            task.save()
            
            return HttpResponseRedirect('/task/task_list/'+str(nomber) + '/')
    else:
        form = NewTaskForm(None)
    
    template = get_template("new_task.html")
    context = RequestContext(request, {
        'project':project,
        'form' : form,
        'errors':errors, 
        'user': request.user
    })
    return HttpResponse(template.render(context))


def delete_task(request, taskId):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    
    task = get_object_or_404(Tasks, id=taskId)
    prId = task.Project.id        
    if(task.Project.Owner!=request.user):
        messages.warning(request, 'Вы не имеете прав')
        return HttpResponseRedirect('/task/'+str(taskId)+'/' )#request.META['HTTP_REFERER'])
        
    if not task:
        messages.warning(request, 'Задание не найдено')
    else:
        task.delete()
        messages.info(request, 'Задание удалено')    
    
    return HttpResponseRedirect('/project/'+str(prId)+'/' )


def task_menu(request, taskId):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    errors = []

    #task = Tasks.objects.filter(id=task)[0]    
    task = get_object_or_404(Tasks, id=taskId)    
    project = get_object_or_404(Project_Group,id=task.Project.id)
    
    member = Us_Pr_Role.objects.filter(User=request.user, Project_Group=project)
    if len(member)>0:
        isMember = 1
    else: 
        isMember = 0

    messageform = MessageForm(None)
    uploadform = UploadForm(None, None)
    editform = EditForm({'Title':task.Title, 'Description':task.Description})    
    files = UploadFile.objects.filter(MessTheme=task.MessTheme)
    
    mess = Message.objects.filter(MessTheme=task.MessTheme).order_by("-id")  
    paginator = Paginator(mess, 15) # Show 25 contacts per page

    page = request.GET.get('page')
    try:
        ms = paginator.page(page)
    except PageNotAnInteger:
        # If page is not an integer, deliver first page.
        ms = paginator.page(1)
    except EmptyPage:
        # If page is out of range (e.g. 9999), deliver last page of results.
        ms = paginator.page(paginator.num_pages)
    

    
    template = get_template("menu_task.html")
    context = RequestContext(request, {
        'project': project,
        'messageform' : messageform,        
        'uploadform' :uploadform,
        'editform' : editform,
        'files' : files,
        'task' : task,
        'mess' : mess,
        'ms' : ms,
        'errors':errors,
        'isMember' : isMember,
        'user': request.user
    })
    return HttpResponse(template.render(context))    


def task_list(request, nomber):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    errors = []
    project = Project_Group.objects.filter(id=nomber)[0]
    tasks = Tasks.objects.filter(Project=project).order_by('-Create_date')    
         
    member = Us_Pr_Role.objects.filter(User=request.user, Project_Group=project)
    if len(member)>0:
        isMember = 1
    else: 
        isMember = 0
    
    template = get_template("task_list.html")
    context = RequestContext(request, {
        'project': project,                
        'tasks' : tasks,
        'errors':errors, 
        'isMember' : isMember,
        'user': request.user
    })
    return HttpResponse(template.render(context))    


def my_tasks(request):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    errors = []
    tasks = Tasks.objects.filter(User=request.user).order_by('State','-Create_date')    
             
    template = get_template("my_tasks.html")
    context = RequestContext(request, {                
        'tasks' : tasks,
        'errors':errors, 
        'user': request.user
    })
    return HttpResponse(template.render(context))        
    
def take_task(request, taskId):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    
    task = get_object_or_404(Tasks, id=taskId)
    member = Us_Pr_Role.objects.filter(User=request.user, Project_Group=task.Project)
    if len(member)>0:
        isMember = 1
    else: 
        isMember = 0
    
    if task.User:
        messages.warning(request, 'Неверная операция')
        return HttpResponseRedirect(request.META['HTTP_REFERER'])
    else:    
        if not isMember:
            messages.warning(request, 'Вы не участник проекта')
            return HttpResponseRedirect(request.META['HTTP_REFERER'])
        else:
            task.User=request.user
            task.save()
            messages.success(request, 'Вы взяли задание')
            return HttpResponseRedirect(request.META['HTTP_REFERER'])
    
    return HttpResponseRedirect('/task/'+str(taskId) + '/')
    
def refuse_task(request, taskId):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    
    task = get_object_or_404(Tasks, id=taskId)
    member = Us_Pr_Role.objects.filter(User=request.user, Project_Group=task.Project)
    if len(member)>0:
        isMember = 1
    else: 
        isMember = 0
    
    if task.User==None:
        messages.warning(request, 'Неверная операция')
        return HttpResponseRedirect(request.META['HTTP_REFERER'])
    elif task.User==request.user:    
        if not isMember:
            messages.warning(request, 'Вы не участник проекта')
            return HttpResponseRedirect(request.META['HTTP_REFERER'])
        else:
            task.User=None
            task.save()
            messages.success(request, 'Вы отказались от задания')
            return HttpResponseRedirect(request.META['HTTP_REFERER'])
    else:
        messages.warning(request, 'Это не ваше задание')
        return HttpResponseRedirect(request.META['HTTP_REFERER'])

    return HttpResponseRedirect('/task/'+str(taskId) + '/')
    

def edit_task(request, taskId):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    
    task=get_object_or_404(Tasks,id=taskId)    
    
    if request.user != task.Project.Owner:
        messages.error(request, 'Недостаточно прав')
        return HttpResponseRedirect(request.META['HTTP_REFERER'])    
    
    form = EditForm(request.POST or None)
    if request.method == 'POST' and form.is_valid():
        title = form.cleaned_data.get('Title', None)
        descr = form.cleaned_data.get('Description', None)
        
        task.Title = title
        task.Description = descr
        task.save()
        messages.success(request, 'Изменения сохранены')
        return HttpResponseRedirect(request.META['HTTP_REFERER'])
        
        
    return HttpResponseRedirect('/task/'+str(taskId)+'/')