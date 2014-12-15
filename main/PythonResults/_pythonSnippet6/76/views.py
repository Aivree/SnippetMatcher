import sys
import os
from subprocess import Popen, PIPE

from django.shortcuts import render
from checker.forms import UploadFileForm

def home(request):
    if (request.method == 'POST'):
        form = UploadFileForm(request.POST, request.FILES)
        if form.is_valid():
            #process code
            with open('main.c', 'wb+') as destination:
                for chunk in request.FILES['file'].chunks():
                    destination.write(chunk)
            
            os.system('gcc main.c')
            process = Popen(['./a.out'], stdout=PIPE)
            output = process.stdout
            temp_string = output.read()
            temp_string = temp_string.decode("utf-8")
            correct_file = open("correct.txt", "r")
            correct_string = correct_file.read()

            if(correct_string == temp_string):
                state = "YOU GOT IT RIGHT! YAY"
            else:
                state = "YOU'RE TERRABAD!!"

            return render(request,  'uploaded.html', {
                'state': state,
            })
    else:
        form = UploadFileForm()

    return render(request, 'home.html', {
        'form': form
    })
