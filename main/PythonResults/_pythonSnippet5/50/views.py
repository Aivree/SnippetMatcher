# Create your views here.
from django.http import HttpResponse, HttpResponseRedirect
from django.template import Context, loader, RequestContext
from django.shortcuts import render, render_to_response, get_object_or_404, redirect
from django.core.urlresolvers import reverse
from lol.models import Snippet, Language
from django.core.exceptions import ValidationError
from lol.forms import UploadForm
from django.core.mail import send_mail
import json

def index(request):
    if request.method == 'GET':
        random = Snippet.objects.filter(approved=True).order_by('?')[0]
        thanks = False
        if request.session.get('thanks', False):
            request.session['thanks'] = False
            thanks = True
        response =  render_to_response('index.html', {'snippet': random, 'thanks': thanks, 'indexView': True},
                                  context_instance=RequestContext(request))
        response['Cache-Control'] = 'no-cache'
        return response
    
    elif request.method == 'POST':
        bias = request.POST['value']
        snippet_id = request.POST['snippet_id']
        s = get_object_or_404(Snippet, pk=snippet_id)
        if (bias == '1'):
            s.leet += 1
        elif(bias == '-1'):
            s.lame += 1
        s.save()
        return HttpResponse("Success")
        #return redirect('index')
        #return HttpResponseRedirect(reverse('index'))

def upload(request):
    if request.method == 'GET':
        form = UploadForm()
        return render(request, 'upload.html', {
            'form': form,
            'submitLanguages':Language.objects.all()
        })
    elif request.method == 'POST':
        form = UploadForm(request.POST) 
        if form.is_valid():
            desc = form.cleaned_data['description']
            formCode = form.cleaned_data['inputCode']
            lang = form.cleaned_data['language']
            userName = form.cleaned_data['userName']
            attributionData = form.cleaned_data['attribution']
            s = Snippet(code=formCode.strip(), description=desc.strip(), language=lang, userName=userName.strip(), attribution=attributionData.strip())
            if s.validate():
                s.save()
            else:
                return render(request, 'upload.html', {
                    'form':                form,
                    'error_message':       "Looks like we already have that code in our system." ,
                    'error_message_title': "Oops sorry!"
                })
            request.session['thanks'] = True
            #TODO - send email here
            return redirect('index')
        else:
            return render(request, 'upload.html', {
                'form': form,
                'submitLanguages':Language.objects.all()
            })

def top(request, language_id=None):
    limit = 10
    lang = None
    if language_id != None:
        lang = get_object_or_404(Language, pk=language_id)
        top = sorted(Snippet.objects.filter(approved=True).filter(language=lang), key=lambda a: a.score, reverse=True)[:limit]
    else:
        top = sorted(Snippet.objects.filter(approved=True), key=lambda a: a.score, reverse=True)[:limit]
    return render_to_response('top.html', {'top': top, 'limit': limit, 'langTop': lang},
                              context_instance=RequestContext(request))

def view(request, snippet_id):
    snippetList = Snippet.objects.filter(pk=snippet_id).filter(approved=True)
    if len(snippetList) == 1:
        return render_to_response('view.html', {'snippet': snippetList[0]},context_instance=RequestContext(request))
    else:
        return redirect('index')

def bylang(request, language_id):
    lang = get_object_or_404(Language, pk=language_id)
    snippet = Snippet.objects.filter(language=lang).filter(approved=True).order_by('?')[0]
    return render_to_response('index.html', {'snippet': snippet, 'lang':lang, 'indexView': True},context_instance=RequestContext(request))

def about(request):
    return render_to_response('about.html')
    