#!/usr/bin/env python
# -*- coding:utf-8 -*-
import datetime
from django.http import HttpResponse, HttpResponseRedirect
from django.template.loader import get_template
from django.template import RequestContext
from django.core.urlresolvers import reverse
from django.core.paginator import Paginator,EmptyPage, PageNotAnInteger

from django.shortcuts import render_to_response
from FKN_Workgroup.models import MessTheme, Project_Group, Message, UploadFile, Tasks, Invite, Phases, Us_Pr_Role

from django.contrib.auth.models import User

from django.views.decorators.csrf import csrf_protect
from django.views.generic.simple import direct_to_template
from forms import *
from message.forms import MessageForm 
from django.shortcuts import get_object_or_404
from django.contrib import messages

def new_group(request):
    
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
        
    errors = []
    form = NewGroupForm(request.POST or None)
    #context = { 'form': form,  'errors':errors, 'user': request.user}
    
    if request.method == 'POST' and form.is_valid():
        title = form.cleaned_data.get('Title', None)
        descr = form.cleaned_data.get('Description', None)
        
        mt = MessTheme(Title = "first")
        mt.save()
        
        group = Project_Group(Owner = request.user, Title = title, Description = descr, Create_date = datetime.datetime.now(), Phase_id = 1, MessTheme = mt)
        group.save()
        
        upr = Us_Pr_Role(User=request.user,Project_Group=group,Role_id=1)
        upr.save()
        
        return HttpResponseRedirect('/project/'+str(group.id))
    
    template = get_template("new_group.html")
    context = RequestContext(request, {
        'form' : form,
        'errors':errors, 
        'user': request.user
    })
    return HttpResponse(template.render(context))
    #return direct_to_template(request, 'new_group.html',  context)

def delete_group(request, nomber):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    
    group = get_object_or_404(Project_Group, id=nomber, Owner=request.user)
        
    if not group:
        messages.warning(request, 'Сообщение не найдено')
    else:
        print 2
        group.delete()
        messages.info(request, 'Группа была удалена')
    
    return HttpResponseRedirect('/welcome/')
    

def sendinvite(request, nomber):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')

    errors = []    
    
    if request.method == 'POST':        
            project = Project_Group.objects.filter(id=nomber)[0]            
            if (request.user!=project.Owner):
               messages.warning(request, 'Недостаточно прав')
            inviteform = InviteForm(request.POST)
            if inviteform.is_valid():    
                guest_name = inviteform.cleaned_data.get('Guest',None)
                Guest = User.objects.filter(username=guest_name)
                if (len(Guest) == 0):
                    messages.error(request, 'Пользователь не найден')
                    return HttpResponseRedirect(request.META['HTTP_REFERER']) 
                if (Guest[0] == request.user):
                    messages.warning(request, 'Сам себе? хм. Интересно')
                    return  HttpResponseRedirect(request.META['HTTP_REFERER'])
                
                old_inv = Invite.objects.filter(Owner=request.user, Guest = Guest[0], Project_id = nomber, Check__gte=0, Check__lte=2)
                if(len(old_inv)!=0):
                    messages.warning(request, 'Вы уже приглашали этого участника')
                    return  HttpResponseRedirect(request.META['HTTP_REFERER'])
               
                if(not errors):    
                    inv = Invite(Owner=request.user, Guest = Guest[0], Project_id = nomber)
                    Guest[0].new_invite_count += 1
                    Guest[0].save()
                    messages.success(request, 'Приглашение отправлено')
                    inv.save()
                    return HttpResponseRedirect('/project/'+nomber+'/')
  
    return HttpResponseRedirect('/project/'+nomber+'/')
 
   
def project_menu(request, nomber):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    errors = []
    #project = Project_Group.objects.filter(id=nomber)[0]
    project=get_object_or_404(Project_Group,id=nomber)
    messageform = MessageForm(None)
    uploadform = UploadForm(None, None)
    inviteform = InviteForm(None)
    editform = EditForm({'Title':project.Title,'Description':project.Description})            
    
    files = UploadFile.objects.filter(MessTheme=project.MessTheme)

    mess = Message.objects.filter(MessTheme=project.MessTheme).order_by("-id")  
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
    
    member = Us_Pr_Role.objects.filter(User=request.user, Project_Group=project)
    if len(member)>0:
        isMember = 1
    else: 
        isMember = 0

    tasks = Tasks.objects.filter(Project=project)    
    phases = Phases.objects.all()

    template = get_template("menu_project.html")
    context = RequestContext(request, {
        'project': project,
        'uploadform' : uploadform,
        'messageform' : messageform,  
        'inviteform' : inviteform,
        'editform' : editform,
        'files' : files,
        'tasks' : tasks,
        'ms' : ms,
        'isMember' : isMember,        
        'errors':errors, 
        'phases' : phases,
        'user': request.user
    })
    return HttpResponse(template.render(context))

def ok_invite(request,inv_id):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    inv = Invite.objects.filter(id=inv_id, Guest=request.user)[0]
    inv.Check=1
    inv.save()
    
    request.user.new_invite_count -= 1
    request.user.save()
    
    upr = Us_Pr_Role(User=request.user,Project_Group=inv.Project,Role_id=1)
    upr.save()
    
    return HttpResponseRedirect('/project/my_invites/')
    
def refuse_invite(request,inv_id):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    
    inv = Invite.objects.filter(id=inv_id, Guest=request.user)[0]
    inv.Check=3
    inv.save()
    
    request.user.new_invite_count -= 1
    request.user.save()
    
    return HttpResponseRedirect('/project/my_invites/')
    
def my_invites(request):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    errors = []
    invites = Invite.objects.filter(Guest=request.user, Check=0)

    template = get_template("my_invites.html")
    context = RequestContext(request, {
        'invites':invites,
        'errors':errors, 
        'user': request.user
    })
    return HttpResponse(template.render(context))

def my_projects(request):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    errors = []
    
    filterform = FilterForm(request.POST or None)
    projects = projects = Project_Group.objects.filter(Owner=request.user)

    can_find = False    
    mycookie = False
    check_list = []
    findPr = ''
    
        
    if 'findPr' in request.COOKIES and 'check' in request.COOKIES:
        mycookie = True
        findPr = request.COOKIES['findPr']
        check = request.COOKIES['check']
        if check=='all':
            check_list = ['other','my']
        elif check=='none':
            check_list = []
        elif check=='other':
            check_list = ['other']
        else:
            check_list = ['my']
    
    if request.method=="GET" and mycookie:
        can_find = True        
        filterform.fields['PrCheck'].initial = check_list 
        filterform.fields['findPr'].initial = findPr
        
    if request.method=="POST" and filterform.is_valid():        
        can_find = True        
        findPr = filterform.cleaned_data.get('findPr', None)        
        check_list = filterform.cleaned_data.get('PrCheck', None)                  
            
    
    if can_find:           
        if len(check_list)==0:
            #error
            projects = []                        
        elif len(check_list)==2:  #выбрать все проекты
            if findPr!='': #search with pr title
                projects = map(lambda upr:upr.Project_Group, Us_Pr_Role.objects.filter(User=request.user))
                projects = filter(lambda pr: pr.Title.find(findPr)!=-1, projects)
            else: # search without pr title
                projects = map(lambda upr:upr.Project_Group, Us_Pr_Role.objects.filter(User=request.user))
        elif len(check_list)==1:
            if check_list[0]=='my':
                if findPr!='': #search with pr title
                    projects = Project_Group.objects.filter(Owner=request.user,Title__contains=findPr)
                else: # search without pr title
                    projects = Project_Group.objects.filter(Owner=request.user)
            elif check_list[0]=='other':         #choose not my projects
                if findPr!='': #search with pr title                  
                    projects = map(lambda upr:upr.Project_Group, Us_Pr_Role.objects.filter(User=request.user))
                    projects = filter(lambda pr: (pr.Owner!=request.user) and (pr.Title.find(findPr) != -1), projects)
                    #добавить выборку, где не мои! + поиск
                else: # search without pr title
                #warning make with dbquery!!!!!!!
                    projects = map(lambda upr:upr.Project_Group, Us_Pr_Role.objects.filter(User=request.user))
                    projects = filter(lambda pr: pr.Owner!=request.user, projects)
                
    #projects = Project_Group.objects.filter(Owner=request.user)

    paginator = Paginator(projects, 30) # Show 25 contacts per page

    page = request.GET.get('page')
    try:
        pr = paginator.page(page)
    except PageNotAnInteger:
        # If page is not an integer, deliver first page.
        pr = paginator.page(1)
    except EmptyPage:
        # If page is out of range (e.g. 9999), deliver last page of results.
        pr = paginator.page(paginator.num_pages)
    
    
    template = get_template("my_projects.html")
    context = RequestContext(request, {
        'projects': projects,
        'filterform' : filterform,        
        'errors':errors, 
        'user': request.user,
        'proj' : pr
    })
    response = HttpResponse(template.render(context))    
    if request.method=="POST" and filterform.is_valid():        
        response.set_cookie('findPr',findPr)
        if len(check_list)==2:        
            response.set_cookie('check', 'all')
        elif len(check_list)==0:
            response.set_cookie('check', 'none')
        elif check_list[0]=='other':
            response.set_cookie('check', 'other')
        else:
            response.set_cookie('check', 'my')
          
    return response
    
def change_phase(request, nomber, phase):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
        
        
    project = Project_Group.objects.filter(id=nomber)[0]
    if project.Owner != request.user:
        messages.error(request, 'Недостаточно прав')
        return HttpResponseRedirect(request.META['HTTP_REFERER'])

#make validate phase_id
    project.Phase_id = phase
    project.save()
        
    return HttpResponseRedirect('/project/'+nomber+'/')  
    
def edit(request, nomber):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    
    project=get_object_or_404(Project_Group,id=nomber)    
    
    if request.user != project.Owner:
        messages.error(request, 'Недостаточно прав')
        return HttpResponseRedirect(request.META['HTTP_REFERER'])    
    
    form = EditForm(request.POST or None)
    if request.method == 'POST' and form.is_valid():
        title = form.cleaned_data.get('Title', None)
        descr = form.cleaned_data.get('Description', None)
        
        project.Title = title
        project.Description = descr
        project.save()
        messages.success(request, 'Изменения сохранены')
        return HttpResponseRedirect(request.META['HTTP_REFERER'])
        
        
    return HttpResponseRedirect('/project/'+project.id+'/')
    
def leave_project(request, prId):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('/welcome/')
    
    upr=get_object_or_404(Us_Pr_Role,User=request.user,Project_Group_id=prId)
    if upr.Project_Group.Owner==request.user:
        messages.error(request,'Вы являетесь руководителем')
        return HttpResponseRedirect('/project/'+str(prId)+'/')
    else:
        inv = get_object_or_404(Invite,Guest=request.user,Project_id=prId)
        inv.delete()
        upr.delete()
        messages.success(request, 'Вы покинули группу')
        return HttpResponseRedirect('/welcome/')