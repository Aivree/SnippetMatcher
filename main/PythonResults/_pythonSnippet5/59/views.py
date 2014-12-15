
from django.template import RequestContext
from django.shortcuts import HttpResponse
from django.shortcuts import render
from django.core.urlresolvers import reverse
from django.shortcuts import HttpResponseRedirect
from django.shortcuts import render_to_response
from models import Books
from forms import BooksForm, UploadForm, ContactForm,SubscriptionForm
from django.core.mail import send_mail,EmailMessage
from django.conf import settings
import collections
from json import dumps, load, JSONEncoder
from uuid import UUID
# import jsonpickle
import pickle


class PythonObjectEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, (list, dict, unicode, int, float, bool, type(None))):
            return JSONEncoder.default(self, obj)
        elif isinstance(obj, set):
            return list(obj)
        elif isinstance(obj, str):
            return unicode(obj)


def index(request):
    return render_to_response('index.html', {}, context_instance=RequestContext(request))


def angularTemplate(request, template_name):
    return render_to_response(template_name + '.html', {}, context_instance=RequestContext(request))


def apiGallery(request, archive_id =None):

    response_data = {}
    list_data = []
    # print response_data

    if not archive_id:

        feelings = Books.objects.all().order_by('-publish')
        for elem in feelings:
            d=collections.OrderedDict()
            d['id'] = elem.id
            d['book_catagory'] = elem.book_catagory
            d['book_title'] = elem.book_title
            d['book_author_name'] = elem.book_author_name
            d['book_author_profession'] = elem.book_author_profession
            d['book_cover_image'] = elem.book_cover_image.name
            d['book_upload'] = elem.book_upload.name
            d['book_overview'] = elem.book_overview
            list_data.append(d)
            response_data = {'response_data': list_data}
            print response_data

    else:

        book = Books.objects.get(id= archive_id)


        d= collections.OrderedDict()
        d['id'] = book.id
        d['book_catagory'] = book.book_catagory
        d['book_title'] = book.book_title
        d['book_author_name'] = book.book_author_name
        d['book_author_profession'] = book.book_author_profession
        d['book_cover_image'] = book.book_cover_image.name
        d['book_upload'] = book.book_upload.name
        d['book_overview'] = book.book_overview
        list_data.append(d)
        for elem in list_data:
            response_data = {'response_data': elem}


            # response_data = {'response_data': elem}
            # print response_data
        # response_data = {'response_data': album}

    return HttpResponse(dumps(response_data, cls=PythonObjectEncoder), content_type="application/json")


def add_archive(request):
    if request.method == 'POST':

        form = BooksForm(request.POST, request.FILES)
        if form.is_valid():
            # bd=Feelings(handle_uploaded_file(request.FILES['feelings_owner_img']))

            form.save()
        return HttpResponseRedirect(reverse('feel_home'))
    else:
        form = BooksForm()

    documents = Books.objects.all()
    return render_to_response('contact_form.html', {'form': form, 'documents': documents},context_instance= RequestContext(request))


# def slide(request):
#     return render_to_response('home.html', context_instance=RequestContext(request))


def upload(request):
    if request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)
        if form.is_valid():
            subject = form.cleaned_data['subject']
            message = form.cleaned_data['message']
            attach2 = request.FILES['attach']
            sender = form.cleaned_data['sender']
            cc_myself = form.cleaned_data['cc_myself']
            recipient = ['educationservice24@gmail.com']
            if cc_myself:
                recipient.append(sender)

            try:
                mail=EmailMessage(subject, message, sender, recipient, ['EMAIL_ADDRESS'])
                mail.attach(attach2.name, attach2.read(), attach2.content_type)
                mail.send()

            except:
                return "Attachment error"

            return HttpResponseRedirect(reverse('feel_home'))
    else:
        # Dynamic initial values.
        form = UploadForm(initial={'sender': "Email address"})
    return render(request, 'upload.html', {'form': form})


def contact(request):
    if request.method == 'POST':
        form = ContactForm(request.POST)
        if form.is_valid():
            subject = form.cleaned_data['subject']
            message = form.cleaned_data['message']
            sender = form.cleaned_data['sender']
            cc_myself = form.cleaned_data['cc_myself']
            recipient = ['educationservice24@gmail.com']
            if cc_myself:
                recipient.append(sender)
            from django.core.mail import send_mail

            send_mail(subject, message, sender, recipient)
            return HttpResponseRedirect(reverse('feel_home'))# Redirect after post
    else:

        # Dynamic initial values.
        form = ContactForm(initial={'sender': 'Email address'})
    return render(request, 'personal_contact.html', {'form': form})


def slide(request):
    if request.method == 'POST':

        form = SubscriptionForm(request.POST)
        if form.is_valid():
            # bd=Feelings(handle_uploaded_file(request.FILES['feelings_owner_img']))

            form.save()
        return HttpResponseRedirect(reverse('slide'))
    else:
        form = SubscriptionForm()
    return render_to_response('home.html', {'form': form}, context_instance= RequestContext(request))
