from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.shortcuts import render
from django.template import RequestContext
from hr_app.models import Organization, Honoree, Honor
from hr_app.forms import HonoreeForm, HonorForm, UploadForm
from mptt.exceptions import InvalidMove
from mptt.forms import MoveNodeForm
from django.views.generic.edit import CreateView, UpdateView
from django.views.generic.detail import DetailView
from django.contrib.auth.decorators import login_required
import helpers

def home(request):
	return render(request,"home.html",{})

def show_organizations(request):
	print str(Organization.objects.get(pk=1).get_absolute_url());
	return render(request, "show_orgs.html",
                          {'nodes':Organization.objects.all().order_by("organization_name")},
                          context_instance=RequestContext(request))

def show_organization_detail(request, org_id):
	return render(request, "show_org_detail.html",
						{'nodes':Organization.objects.get(pk=org_id)},
						context_instance=RequestContext(request))

def show_honorees(request):
	return render(request, "show_honorees.html",
                          {'nodes':Honoree.objects.all().order_by("last_name")},
                          context_instance=RequestContext(request))


def show_honors(request):
    return render(request, "show_honors.html",
                          {'nodes':Honor.objects.all()},
                          context_instance=RequestContext(request))

def AddOrgHonoreeHonorForm(request):
	if request.method == 'POST':
		# I've got the form posted
		data = request.POST
		org, created = Organization.objects.get_or_create(organization_name=data['organization_name'])
		org.save()
		hee = Honoree()
		hee.first_name = data['first_name']
		hee.middle_name = data['middle_name']
		hee.last_name = data['last_name']
		hee.email = data['email']
		hee.parent = org
		hee.save()
		h = Honor()
		h.award_level = data['award_level']
		h.bar_status = data['bar_status']
		h.year = data['year']
		h.parent = hee
		h.save()
		# return HttpResponseRedirect(reverse('honoree-view', kwargs={'pk':hee.id}))
		return HttpResponseRedirect('/honorees')

	else:
		# An empty, unbound form
		orgs = Organization.objects.all()
		form = HonoreeForm()
		hform = HonorForm()
	
	return render(request, 'forms/form.html', {'form': form, 'hform':hform, 'orgs':orgs}, context_instance=RequestContext(request))

# This is the secret sauce here
# User uploads a CSV, and the whole thing goes into the system
@login_required
def uploadCSV(request):
	if request.method == 'POST':
		form = UploadForm(request.POST, request.FILES)
		success = helpers.processCSVfile(request.FILES["file"])
		print success

		# What we will do here is save each row as a lawyer

		return HttpResponseRedirect('/upload') 
	else:
		form = UploadForm()
	return render(request,'upload.html', {'form': form})