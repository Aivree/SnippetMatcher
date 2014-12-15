# -*- coding: utf-8 -*-
from django.template import RequestContext, Context
from django.shortcuts import render_to_response
from django.views.decorators.csrf import csrf_protect
from django.db import models
from shannon_search.models import Dictionary
from shannon_search.models import SearchSample


def root(request):
    ctxt = RequestContext(request, {})
    return render_to_response("index.html", ctxt)

def search(request):
    key = ''
    result_for_template = []

    if "keyword" in request.POST:
        keyword = request.POST["keyword"]
        # 全角も半角もor
        dst = keyword.replace(u'　', ' ')
        splited = dst.split(' ')
        key = "'" + "','".join(splited) + "'"
        result = SearchSample.objects.extra(
          where=["key in (" + key + ")"]
        )
        if len(result) > 0:
            for res in result:
                result_for_template.append(res.name)
        else:
            result_for_template.append("no result")

        ctxt = RequestContext(request, {
            "result": result_for_template,
        })
    return render_to_response("search.html", ctxt)

def register(request):
    ctxt = RequestContext(request, {})
    return render_to_response("register.html", ctxt)

def register_result(request):
    key = message = ''
    if "keyword" in request.POST:
        keyword = request.POST["keyword"]

        # 全角も半角もor
        dst = keyword.replace(u'　', ' ')
        splited = dst.split(' ')

        # 登録ワードが2つ以上
        if len(splited) >= 2:
            key = splited.pop(0)

        for val in splited:
            dictionary = Dictionary()
            # http://iyukki.blog56.fc2.com/blog-entry-137.html,
            # http://stackoverflow.com/questions/12684001/how-to-dump-a-py3k-httpresponse-into-json-load
            dictionary.key = key
            dictionary.name = val
            check = Dictionary.objects.extra(
                where=["key = '" + key + "'", "name = '" + val + "'"]
            )
            if not check:
                dictionary.save()
                #message += (val.encode('utf-8') + ', ')
                message += (val + ', ')
            else:
                message = "no key and message"
    else:
        keyword = ""
        message = "input message"
    ctxt = RequestContext(request, {
        "keyword": keyword,
        "key": key,
        "message": message,
    })
    return render_to_response("register_result.html", ctxt)

from django.http import HttpResponse
from simplejson import dumps
from django.core import serializers

# http://stackoverflow.com/questions/9262278/django-view-returning-json-without-using-template
# it is not using
def get_json(request):
    dictionaries = Dictionary.objects.all()
    to_json = []
    for dictionary in dictionaries:
        dic_dict = {}
        dic_dict['key'] = dictionary.key
        dic_dict['name'] = dictionary.name

        to_json.append(dic_dict)

    # convert the list to JSON
    response_data = dumps(to_json,ensure_ascii=False)
    response_data = response_data.encode('utf-8')
    # http://bixly.com/blog/json-jquery-and-django/
    return HttpResponse(response_data, mimetype='application/json')

def get_json_js(request):
    dictionaries = Dictionary.objects.all()
    to_json = []
    for dictionary in dictionaries:
        dic_dict = {}
        dic_dict['key'] = dictionary.key
        dic_dict['name'] = dictionary.name

        to_json.append(dic_dict)

    # convert the list to JSON
    response_data = dumps(to_json,ensure_ascii=False)
    response_data = response_data.encode('utf-8')
    # http://bixly.com/blog/json-jquery-and-django/
    response_data = "var db_json = " + response_data
    return HttpResponse(response_data, content_type="text/plain")

# it is not using
def get_json2(request):
    foos = Dictionary.objects.all()
    data = serializers.serialize('json', foos)
    return HttpResponse(data, mimetype='application/json')

@csrf_protect
def search_dec(request):
    if "keyword" in request.POST:
        keyword = request.POST["keyword"]
    else:
        keyword = ""
    ctxt = RequestContext(request, {
        "keyword": keyword
    })
    return render_to_response("search.html", ctxt)
