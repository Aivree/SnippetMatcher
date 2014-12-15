from django.shortcuts import render
from django.core.urlresolvers import reverse
from slips.models import Archive
from django.contrib.auth.decorators import login_required
from django.contrib.auth import authenticate, login, logout
from django.http import HttpResponseRedirect, HttpResponse
from slips.forms import UploadForm, ArchiveTable, EditForm, AdminArchiveTable
import datetime
from django.db.models import Sum, Min, Max
from django.views.decorators.cache import cache_control

@cache_control(no_cache=True, must_revalidate=True, private=True,
               max_age=0)
def user_login(request):
    "User login"
    if request.POST:
        username = request.POST['username']
        password = request.POST['password']
        user = authenticate(username=username, password=password)
        if user is not None:
            if user.is_active:
                login(request, user)
                return HttpResponseRedirect(reverse('slips:index'))
            else:
                state = 'User is not active'
        else:
            state = "Username or password incorrect"
        return render(request, 'slips/login.html', {'state': state})
    else:
        return render(request, 'slips/login.html')

@cache_control(no_cache=True, must_revalidate=True, private=True)
def user_logout(request):
    "User logout"
    logout(request)
    return HttpResponseRedirect(reverse('slips:index'))


@login_required(login_url="/slips/login")
def index(request):
    "Home page view"
    user = request.user
    return render(request, 'slips/index.html', {'user': user})


@login_required(login_url="/slips/login")
def upload(request):
    "Upload form view"
    if request.method == "POST":
        form = UploadForm(data=request.POST, files=request.FILES)
        if form.is_valid():
            data_upload = form.save(commit=False)
            data_upload.user = request.user
            data_upload.save()
            if request.is_ajax():
                status = "success"
                return HttpResponse(status)
        elif request.is_ajax():
            status = "error"
            return HttpResponse(status)

    else:
        form = UploadForm()
    return render(request, 'slips/upload.html', {'upload_form': form})


def edit(request, arc_id):
    "Edit form view"
    current_record = Archive.objects.get(id=arc_id)
    if request.POST:
        form = EditForm(request.POST, instance=current_record)
        if form.is_valid():
            form.save()
            return HttpResponseRedirect(reverse("slips:index"))
        else:
            form = EditForm(instance=current_record)
            return render(request, 'slips/edit.html', {'form': form})
    else:
        form = EditForm(instance=current_record)
        return render(request, 'slips/edit.html', {'form': form})


def delete(request, arc_id):
    "Delete form view"
    if request.POST:
        Archive.objects.get(id=arc_id).delete()
        return HttpResponseRedirect(reverse('slips:index'))
    else:
        return render(request, 'slips/delete.html')


@login_required(login_url="/slips/login")
def table_view(request, cat_id):
    "View tables"
    today = datetime.date.today()
    month = today.strftime("%B")
    if request.user.is_staff:
        total_month = Archive.objects.filter(category_id=cat_id,
                                  date__year=today.year,
                                  date__month=today.month).aggregate(Sum('price'))
        table = AdminArchiveTable(Archive.objects.filter(category_id=cat_id))
        grand_total = Archive.objects.filter(category_id=cat_id).aggregate(Sum('price'))
    else:
        total_month = Archive.objects.filter(user=request.user,
                                             category_id=cat_id,
                                             date__year=today.year,
                                             date__month=today.month).aggregate(Sum('price'))
        table = ArchiveTable(Archive.objects.filter(user=request.user,
                                                category_id=cat_id))
        grand_total = Archive.objects.filter(category_id=cat_id,
                                             user=request.user).aggregate(Sum('price'))
    table.paginate(page=request.GET.get('page', 1), per_page=10)
    return render(request, 'slips/table.html', {'table': table,
                                                'total_month': total_month,
                                                'month': month,
                                                'grand_total': grand_total})


@login_required(login_url="/slips/login")
def stats(request, cat_id):
    "View stats"
    full_total = Archive.objects.filter(category_id=cat_id).aggregate(Sum('price'),
                                                                     Max('price'), Min('price'))
    return render(request, 'slips/stats.html', {'full_total': full_total})
