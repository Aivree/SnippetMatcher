from photoViewer.models import Photo
from photoViewer.forms import UploadForm
from django.core.urlresolvers import reverse
from django.core.files import File
from django.http import HttpResponseRedirect, HttpResponse
from django.shortcuts import get_object_or_404, render_to_response, redirect
from django.template import RequestContext
from django.views.generic.simple import direct_to_template
import json
import simplejson
import settings
import binascii

# API Methods will be CRSF Exempt (which will defeat the whole purpose in this case, lol)
# in the future...if you are not using CRSF, you should be checking an API key or authenticating
# before you perform work on the request...
from django.views.decorators.csrf import csrf_exempt

def upload_photo(request):
    view_url = reverse('photosite.photoViewer.views.view_photos')
    if request.method == 'GET':
    	return render_to_response('upload_photo.html',
                              { 'form': UploadForm() },
                              context_instance=RequestContext(request))
    elif request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)
        try:
            if form.is_valid():
                form.save()
                view_url = view_url + '?error=Success!!!'
            else:
                view_url = view_url + '?error=Not a valid image'   
            return HttpResponseRedirect(view_url)         
        except Exception,e:
            import logging
            logger = logging.getLogger(__name__)
            logger.error('caught %s in image upload',e)
            raise e

def view_photos(request):
    return render_to_response('index.html', { 'photos': Photo.objects.all }, context_instance=RequestContext(request))

def remove_photo(request, pk):
    if request.method == 'POST':
        get_object_or_404(Photo, pk=pk).delete()
    return redirect(view_photos)

@csrf_exempt
def api_upload_file(request):
    photoModel= Photo()
    try:
        photoDict = simplejson.loads(request.raw_post_data)
             
        path = settings.MEDIA_ROOT + '/' + photoDict['photo_name'] + '.jpg'
        imageBinary = binascii.a2b_hex(photoDict['photo_data'])

        file=open(path,"w+b")
        file.write(imageBinary)

        # here we have to do a manual set of the ImageField type...
        # to do that, we can call the save function with the path
        # of the saved file, a reference to the file (in a Django 'File'
        # wrapper), and we specify that the file does not need to be saved
        # (we already saved it to proper location and everything)
        photoModel.photo_data.save(path, File(file), save = False)
        
        #photoModel.date_posted = datetime.strptime(photoDict['photo_date'])

        photoModel.save()
        file.close()
    except:
        HttpResponse(json.dumps({ 'status': 'failure', 'pk_id': -1 }), mimetype="application/json")

    # respond with pk_id so that the iPad can delete the new photo if it wishes
    return HttpResponse(json.dumps({ 'status': 'success', 'pk_id': photoModel.pk }), mimetype="application/json")

@csrf_exempt
def api_remove_file(request, pk):
    status = 'failure'
    if request.method == 'POST':
        try:
            get_object_or_404(Photo, pk=pk).delete()
            status = 'success'
        except:
            status = 'failure'
    else:
        status = 'failure'
    return HttpResponse(json.dumps({ 'status': status }), mimetype="application/json")

def api_get_file_list(request):
    photos = Photo.objects.all()
    photo_list = []

    for photo in photos:
        photo_list.append({ 'pk_id': photo.pk, 'photo_name': photo.filename, 'photo_url':settings.MEDIA_URL + photo.filename, 'photo_date': str(photo.date_posted) })

    return HttpResponse(json.dumps(photo_list), mimetype="application/json")