from django.http import HttpResponse,HttpResponseRedirect
from django.shortcuts import render
from csv_utils import csv_parser
import csv
import models
import forms
# Create your views here.
def index(request):
    form = forms.FileForm()
    return render(request, 'meetup/index.html', {'form':form})

def employee_table(request):
    if request.method == 'POST':
        parser = csv_parser.CSVParser(request.FILES['csv_file'])
        try:
            parser.validate()
            parser.parse()
        except csv_parser.InvalidExtensionError as e:
            error='Invalid File Extension'+e.ext
            return render(request, 'meetup/index.html', {'error_message':error})
        except csv.Error:
            error='There was an error trying to read your file'
            return render(request, 'meetup/index.html', {'error_message':error})
        else:
            return render(request,'meetup/employees.html', {'obj': models.Employee.objects.all()})
    else:
        return render(request,'meetup/employees.html', {'obj': models.Employee.objects.all()})

def company_table(request):
    return render(request,'meetup/companies.html', {'obj': models.Company.objects.all()})