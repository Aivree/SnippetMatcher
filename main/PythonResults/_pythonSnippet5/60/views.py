from django.core.urlresolvers import reverse
from django.db.models import Q
from django.forms.models import modelformset_factory
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.shortcuts import render_to_response
from django.template import RequestContext, loader
from Overlay.forms import BirthForm
from Overlay.forms import UploadForm
from Overlay.models import Births
from Overlay.models import Upload



def main(request):
    template=loader.get_template('main.html')
    context=RequestContext(request)
    
    return HttpResponse(template.render(context))
    
def checks(request):
    my_list_c = Births.objects.values('county').distinct()
    len_c = len(my_list_c)
    my_list_y = Births.objects.order_by('-year').values('year').distinct()

    obj = Births.objects.get(id=1)
    names = obj.get_names()
    fields = obj.get_fields()
    d = []

    for i in range(0, len(names)):
        if ((names[i] != 'County') and
            (names[i] != 'Year') and
            (names[i] != 'Id') and
            (names[i] != 'Source') and
            (names[i] != 'Births')):

            d.append([names[i], fields[i]])
    
    template=loader.get_template('checks.html')
    context = RequestContext(request, {'c_list': my_list_c,
                                       'len_c': len_c,
                                       'y_list': my_list_y,
                                       'names': names,
                                       'fields': fields,
                                       'dict': d})
    
    return HttpResponse(template.render(context))


def results(request):
    message = "message: \n"
    
    if 'counties' in request.GET:
        message = message + 'counties : %r \n' % request.GET.getlist('counties')
        c = request.GET.getlist('counties')
    
    else:
        message = message + 'no counties'
        
    if 'years' in request.GET:
        message = message + 'years : %r \n' % request.GET.getlist('years')
        y = request.GET.getlist('years')
    
    else:
        message = message + 'no years'
        
    if 'attributes' in request.GET:
        message = message + 'attributes : %r \n' % request.GET.getlist('attributes')
        a = request.GET.getlist('attributes')
    
    else:
        message = message + 'no attributes'

    #message is for testing only- remove
    #c is the list of counties
    #y is the list of years
    #a is the list of field names
    #NOTE: there is a potential for the names to be weirdly formatted
    #let me know if they are and I'll fix it
    
    opts = []
    raw_data = []
    data = []
    
    #the kwargs style syntax was only accepting variables
    c2 = 'county'
    y2 = 'year'

    #filtering multiple counties
    Qr = None
    for x in c:
        q = Q(**{"%s__exact" % c2: x})
        if Qr:
            Qr = Qr | q
        else:
            Qr = q

    my_list = Births.objects.filter(Qr)

    #filtering multiple years
    Qr = None
    for x in y:
        q = Q(**{"%s__exact" % y2: x})
        if Qr:
            Qr = Qr | q
        else:
            Qr = q

    my_list = Births.objects.filter(Qr)

    ##########################################################

    o_data = []
    o_opts = []

    for fld in a:
        
        raw_data = []
        data = []
        fld_opts = Births.objects.values_list(fld).distinct()
        opts = []
        
        if fld == 'mothersEdu':
            for i in fld_opts:
                opts.append( str(i)[3:-3] )
                
        elif fld == 'isRepeat':
            for i in fld_opts:
                opts.append(i[0])
                
        elif fld == 'mothersAge':
            for i in fld_opts:
                opts.append(i[0])
            
        else:
            print "Errr.....",fld

        for i in opts:
            trans = []
            cond = 0
            loop_list = my_list.filter(**{fld + '__exact' : i})

            for j in loop_list:
                cond = 1
                trans.append(j.births)

            if cond == 1:
                raw_data.append(trans)

            else:
                raw_data.append([0])

        for i in raw_data:
            sum_births = 0
            
            for j in i:
                sum_births += j
                
            data.append(sum_births)

        o_opts.append(opts)
        o_data.append(data)
        
    ##########################################################

    #use o_data and o_opts and let me know if you need anything else
    #o_data = [data1, data2, data3]
    #o_opts = [opts1, opts2, opts3]
    #so basically these two are lists of the thing I gave you last time (:
    
    return render_to_response('results.html', {'my_list': my_list,
                                               'o_data': o_data,
                                               'o_opts': o_opts})

def upload(request):
    # If the user has attempted to upload
    if request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)

        # and their upload worked
        if form.is_valid():
            newUp = Upload(upfile = request.FILES['upfile'])
            newUp.save()
            return HttpResponseRedirect(reverse('Overlay.views.upload'))
        
    # Otherwise the user receives a pretty blank page
    else:
        form = UploadForm()
    
    return render_to_response('upload.html',
                              {'form': form},
                              context_instance=RequestContext(request)
    )
