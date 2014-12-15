import os
from jenkinsapi import jenkins
import datetime
from random import randrange

from django.core.mail import send_mail, EmailMessage
from django.shortcuts import render
from django.shortcuts import render_to_response
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
from django.template import RequestContext

from codetests.models import UploadForm
from codetests.models import LoginForm
from codetests.models import User
from codetests.models import Problem
from codetests.controller import verify as controller_verify

REMOTE_HOST = "ec2-23-20-248-192.compute-1.amazonaws.com"
VERIFY_JOB = "verify-code-test"
SUBMISSION_PATH = "submissions/"
RECIPIENTS = [ 'mirka@knewton.com' ]

def save_file(newfile):
    if file:
        if not os.path.exists(SUBMISSION_PATH):
            os.makedirs(SUBMISSION_PATH)

        destination = open(SUBMISSION_PATH + newfile.name, 'wb+')
        for chunk in newfile.chunks():
            destination.write(chunk)
        destination.close()

def configure_jenkins():
    j = jenkins.Jenkins("http://" + REMOTE_HOST, '', '')
    j.get_jobs()
    j.create_job('empty', jenkins.EMPTY_CONFIG_XML)
    j.disable_job('empty')
    j.copy_job('empty', 'empty_copy')
    j.enable_job('empty_copy')
    j.reconfig_job('empty_copy', jenkins.RECONFIG_XML)

    j.delete_job('empty')
    j.delete_job('empty_copy')
    return j

def verify(problem_id, filename):
    #os.system("scp /tmp/%s %s:submissions/." % (filename, REMOTE_HOST))
    #configure_jenkins().build_job(VERIFY_JOB, {'PROBLEM' : problem_id, 'SOURCE' : filename})
    return controller_verify(problem_id, filename)

def authenticate(login, password):
    try:
        user = User.objects.get(email=login)
    except User.DoesNotExist:
        print "User not found"
        return False

    if password != user.password:
        print "Passwords don't match"
        return False

    if user.activated is None:
        user.activated = datetime.datetime.now()
        problems = Problem.objects.all()
        problem_id = randrange(len(problems))
        user.problem = problems[problem_id]
        user.save()
        return True
    else:
        naive = user.activated.replace(tzinfo=None)
        if (datetime.datetime.now() - naive > 
                datetime.timedelta(days=30)):
            #user.delete()
            return False
        else:
            return True

def login_user(request):
    login = password = ''
    form = LoginForm()

    if request.POST:
        login = request.POST.get('login')
        password = request.POST.get('password')

        valid = authenticate(login, password)
        if valid:
            request.session['login'] = login
            url = reverse('upload')
            return HttpResponseRedirect(url)

    return render(request, 'login_user.html', 
                  { 'login': login,'form': form })

def upload(request):
    user = User.objects.get(email = request.session['login'])
    problem_title = user.problem.title
    statement = user.problem.statement

    if request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)
        if form.is_valid():
            solution = request.FILES['gzfile']
            save_file(solution)
            problem_title = User.objects.get(
                email=request.session['login']).problem.title
            result = verify(problem_title, solution.name)
            
            if result[0] == 0:
                try:
                    pass
                    #mail = EmailMessage(request.POST['problem_title'],
                    #                    request.POST['message'], 
                    #                    request.POST['sender'],
                    #                    RECIPIENTS)
                    #mail.attach(solution.name, 
                    #            solution.read(), 
                    #            solution.content_type)
                    #mail.send()
                except:
                    return render(request, 'upload_again.html', 
                        { 'failed': -2, 'all': 0, 'form' : form })    
                return HttpResponseRedirect('/response/')
            else:
                return render(request, 'upload_again.html', 
                { 'failed': result[0], 'all': result[1], 'form' : form })
    else:
        form = UploadForm()

    return render(request, 'upload.html', 
                    { 'form' : form, 
                      'problem_title' : problem_title,
                      'statement' : statement })

def response(request):
    return render(request, 'response.html', {})
