#! /usr/bin/env python
# -*- coding: utf-8 -*-
import json

from django.contrib.auth.decorators import login_required
from django.contrib.sites.models import Site
from django.core.urlresolvers import reverse
from django.http import Http404
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.core.exceptions import ObjectDoesNotExist

from store.forms import StoreForm
from store.forms import ProductForm
from store.forms import ProductUpdateForm
from store.forms import StoreUpdateForm
from store.forms import UploadImageProductForm
from store.forms import UploadImageStoreForm
from store.models import Store
from store.models import Product
from store.models import ProductTag


@login_required
def store_create(request):
    """This view show and process store form
    """
    try:
        store = Store.objects.get(created_by=request.user)
    except ObjectDoesNotExist:
        store = False

    if store:
        raise Http404

    if not request.user.userprofile.is_provider:
        raise Http404

    if request.method == 'POST':
        form = StoreForm(
            request.POST,
            request.FILES
        )

        if form.is_valid():
            new_store = form.save()
            new_store.created_by = request.user
            new_store.save()

            return HttpResponseRedirect(
                reverse('userprofile:dashboard')
            )
    else:
        form = StoreForm()

    return render(request, 'store_create.html', {
        'form': form,
    })


@login_required
def store_update(request, slug):
    """This view update the info store.
    """
    if not request.user.userprofile.is_provider:
        raise Http404

    store = Store.objects.get(slug=slug)

    if store.created_by != request.user:
        raise Http404

    if request.method == 'POST':
        form = StoreUpdateForm(
            request.POST,
            request.FILES
        )

        if form.is_valid():
            store = Store.objects.get(slug=slug)
            store.name = form.cleaned_data['name']
            store.description = form.cleaned_data['description']
            store.phone = form.cleaned_data['phone']
            store.address = form.cleaned_data['address']
            store.save()
            store = Store.objects.get(id=store.id)

            return HttpResponseRedirect(
                reverse('store:store-detail', args=[store.slug])
            )
    else:
        store = Store.objects.get(slug=slug)
        form = StoreUpdateForm(
            initial={
                'name': store.name,
                'description': store.description,
                'phone': store.phone,
                'address': store.address,
            }
        )

    return render(request, 'store_update.html', {
        'form': form,
        'store': store,
    })


def store_detail(request, slug):
    """This view process the store detail,
    showing the products and info related
    """
    try:
        store = Store.objects.get(slug=slug)
    except ObjectDoesNotExist:
        raise Http404
    products_list = Product.objects.filter(store=store).exclude(quantity__lte=0)
    return render(request, 'store_detail.html', {
        'store': store,
        'products_list': products_list,
        'slug': slug,
    })


@login_required
def product_create(request, slug):
    """This view create a new product.
    """
    if not request.user.userprofile.is_provider:
        raise Http404

    if request.method == 'POST':
        form = ProductForm(
            request.POST,
            request.FILES
        )

        if form.is_valid():
            new_product = form.save()
            new_product.store = Store.objects.get(slug=slug)
            new_product.created_by = request.user
            new_product.save()

            tags = form.cleaned_data['tags']
            tags_list = tags.split(',')
            if tags_list:
                for tag_name in tags_list:
                    tag_name_clean = tag_name.strip()
                    if len(tag_name_clean) > 0:
                        obj = ProductTag.objects.get_or_create(
                            name=tag_name_clean
                        )
                        new_product.tags.add(obj[0])

            return HttpResponseRedirect(
                reverse('store:store-detail', args=[slug])
            )
    else:
        form = ProductForm()

    return render(request, 'product_create.html', {
        'form': form,
    })


@login_required
def product_update(request, slug, product_slug):
    """This view update the info product.
    """
    if not request.user.userprofile.is_provider:
        raise Http404

    try:
        product = Product.objects.get(slug=product_slug)
    except ObjectDoesNotExist:
        raise Http404

    if product.created_by != request.user:
        raise Http404

    if request.method == 'POST':
        form = ProductUpdateForm(
            request.POST,
            request.FILES
        )

        if form.is_valid():
            tags = form.cleaned_data['tags']
            form.cleaned_data['tags'] = ''
            product.name = form.cleaned_data['name']
            product.description = form.cleaned_data['description']
            product.quantity = form.cleaned_data['quantity']
            product.category = form.cleaned_data['category']
            product.price = form.cleaned_data['price']
            product.save()

            tags_list = tags.split(',')
            for product_tag in product.tags.all():
                product.tags.remove(product_tag)

            if tags_list:
                for tag_name in tags_list:
                    tag_name_clean = tag_name.strip()
                    if len(tag_name_clean) > 0:
                        obj = ProductTag.objects.get_or_create(
                            name=tag_name_clean
                        )
                        product.tags.add(obj[0])

            return HttpResponseRedirect(
                reverse('store:product-detail', args=[slug, product.slug])
            )
    else:
        tags_list = product.tags.values_list('name', flat=True)
        tags = ','.join(tags_list)

        form = ProductUpdateForm(
            instance=product,
            initial={
                'tags': tags,
            }
        )

    return render(request, 'product_update.html', {
        'form': form,
        'product': product,
    })


def product_detail(request, slug, product_slug):
    """This view show the detail product info
    """
    try:
        product = Product.objects.get(slug=product_slug)
    except ObjectDoesNotExist:
        raise Http404

    if product.quantity <= 0:
        raise Http404

    current_site = Site.objects.get_current()

    return render(request, 'product_detail.html', {
        'product': product,
        'slug': slug,
        'current_site': current_site,
    })


@login_required
def upload_product_image(request, slug):
    """Process the uploads for images to product
    """
    if not request.user.userprofile.is_provider:
        raise Http404

    if request.method == 'POST':
        product = Product.objects.get(slug=slug)
        uploadform = UploadImageProductForm(
            request.POST, request.FILES, instance=product,
        )
        if uploadform.is_valid():
            uploadform.save()

        return HttpResponseRedirect(
            reverse('store:product-update', args=[product.store.slug, product.slug])
        )

    raise Http404


@login_required
def upload_store_image(request, slug):
    """Process the uploads for images to store
    """
    if not request.user.userprofile.is_provider:
        raise Http404

    if request.method == 'POST':
        store = Store.objects.get(slug=slug)
        uploadform = UploadImageStoreForm(
            request.POST, request.FILES, instance=store,
        )
        if uploadform.is_valid():
            uploadform.save()

        return HttpResponseRedirect(
            reverse('store:store-update', args=[store.slug])
        )

    raise Http404

def products_by_category(request, category):
    """This view show the products by category
    """
    products_list = Product.objects.filter(category__slug=category)
    if not products_list:
        raise Http404
    return render(request, 'products_by_category.html', {
        'products_list': products_list,
        'category': category,
        }
    )

@login_required
def delete_product(request, slug, product_slug):
    """This view delete a product selected by slug
    """
    if not request.user.userprofile.is_provider:
        raise Http404

    try:
        product = Product.objects.get(slug=product_slug)
    except ObjectDoesNotExist:
        raise Http404

    if product.created_by != request.user:
        raise Http404

    product.delete()

    return HttpResponseRedirect(
        reverse('store:store-detail', args=[slug])
    )

@login_required
def ajax_search_tags(request):
    """Search for available tags
    """
    if request.is_ajax() and 'term' in request.GET:
        result_list = []
        search_criteria = request.GET['term']

        tags = ProductTag.objects.filter(
            name__icontains=search_criteria
        )

        for tag in tags:
            result_list.append({
                'id': tag.id,
                'label': tag.name,
                'value': tag.name,
            })

        if not result_list:
            return HttpResponse('')

        return HttpResponse(
            json.dumps(result_list),
            content_type='application/json'
        )

    raise Http404
