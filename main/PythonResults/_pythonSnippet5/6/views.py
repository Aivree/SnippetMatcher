"""
    Amtex Training Project
    "Image Upload Program"
       
      Sandeep Jadoonanan
       October 24, 2014
"""

import os
from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect, Http404
from django.core.urlresolvers import reverse

from picsApp.models import PictureUpload
from picsApp.forms import PictureUploadForm
from picsApp.simpleFileSize import getSize

# Create your views here.
def uploadFile(request):
  """
  Displays an upload form.
  """
   
  # If a form was POSTed.
  if request.method == "POST":
    # Instantiate a ModelForm with the request data.
    uploadForm = PictureUploadForm(request.POST, request.FILES)
    if uploadForm.is_valid():
      # Save the form data (the picture).
      uploadForm.save()
      return HttpResponseRedirect(reverse("pics:list-files"))
  # No form data, so return an empty form.
  else:
    uploadForm = PictureUploadForm()
   
  return render(request, "picsApp/uploadForm.html", {"form": uploadForm})


def listFiles(request):
  """
  Displays a list of all uploaded images.
  """
   
  # Get a the details of each uploaded file as a list.
  limitResults = 12 
  details = []
  allPictures = PictureUpload.objects.all()
  for pic in allPictures:
    n = {}
    n["fileName"] = os.path.basename(pic.img.name)
    n["fileExtension"] = os.path.splitext(pic.img.name)[-1]
    n["fileSize"] = getSize(pic.img.size)
    n["fileUrl"] = pic.img.name
    details.append(n)
   
  details.reverse()
  return render(request, "picsApp/uploadList.html", {"files": details[:limitResults]})
