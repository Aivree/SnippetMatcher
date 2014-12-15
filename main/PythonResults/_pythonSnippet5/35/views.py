
from django.template import RequestContext
from django.shortcuts import render_to_response
from django.views.generic.simple import direct_to_template
from django.http import HttpResponse, HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.shortcuts import get_object_or_404
from django.utils import simplejson
from articles.models import NewsArticle, UploadForm, UploadModel
from filetransfers.api import prepare_upload, serve_file
from django.conf import settings

def home_page(request):
    return render_to_response('base.html', context_instance = RequestContext(request))

def disclaimer_page(request):
    return render_to_response('base.html', context_instance = RequestContext(request))

def contact_page(request):
    return render_to_response('base.html', context_instance = RequestContext(request))

def upload_handler(request):
    view_url = reverse('articles.views.upload_handler')

    if request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)
        form.save()
        return HttpResponseRedirect(view_url)

    upload_url, upload_data = prepare_upload(request, view_url, private=False)
    form = UploadForm()

    return direct_to_template(request, 'upload.html',{'form': form, 'upload_url': upload_url, 'upload_data': upload_data})


def download_handler(request, pk):
    upload = get_object_or_404(UploadModel, pk=pk)
    return serve_file(request, upload.file, save_as=False)

def latest_news_articles(request):

    articles = NewsArticle.objects.filter(active=True).order_by('-publish_date')[:30]

    json = simplejson.dumps(
        [{'id' : article.id,
          'publish_date': str(article.publish_date),
          'last_update_date': str(article.last_update_date),
          'title': article.title,
          'text': article.text,
          'copyright': article.copyright,
          'source': str(article.source),
          'location': article.location and dict(latitude=article.location.latitude,
              longitude=article.location.longitude, city=article.location.city, country=article.location.country),
          'video': article.video and str(article.video.url),
          'thumb': article.thumb and settings.BASE_URL + 'download/' + str(article.thumb.data.pk),
          'image_gallery': article.image_gallery and dict(

              first= article.image_gallery.first_image and dict(
                  title= article.image_gallery.first_image.title,
                  caption= article.image_gallery.first_image.caption,
                  url= settings.BASE_URL + 'download/' + str(article.image_gallery.first_image.data.pk)
              ),

              second= article.image_gallery.second_image and dict(
                  title= article.image_gallery.second_image.title,
                  caption= article.image_gallery.second_image.caption,
                  url= settings.BASE_URL + 'download/' + str(article.image_gallery.second_image.data.pk)
              ),

              third= article.image_gallery.third_image and dict(
                  title= article.image_gallery.third_image.title,
                  caption= article.image_gallery.third_image.caption,
                  url= settings.BASE_URL + 'download/' + str(article.image_gallery.third_image.data.pk)
              )
          )
         } for article in articles])

    return HttpResponse(json, mimetype="application/json")