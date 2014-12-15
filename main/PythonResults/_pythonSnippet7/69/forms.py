# -*- coding: utf-8 -*-
from django import forms
from django.forms.forms import NON_FIELD_ERRORS
from django.forms.util import ErrorList
from django.db.models import get_model
from cc.pages.models import Page
from mptt.forms import TreeNodeChoiceField
from mptt.exceptions import InvalidMove
from markitup.widgets import MarkItUpWidget

class PagesForm(forms.ModelForm):
    parent = TreeNodeChoiceField(queryset=Page.objects.all(), empty_label="---", required=False)
    content = forms.CharField(widget=MarkItUpWidget)
    
    
    
    def __init__(self, *args, **kwargs):
        
        super(PagesForm, self).__init__(*args, **kwargs)
        # start 
        qs = Page.objects.all()
        
        # remove the instance and it's children
        if self.instance.pk is not None:
            self.fields['parent'].queryset = self.fields['parent'].queryset.exclude(pk__in=[self.instance.pk]).exclude(pk__in=[d.pk for d in self.instance.get_children()])
    
    
    class Meta:
        model = Page

    
    def clean_slug(self, *args, **kwargs):
        slug=self.cleaned_data['slug']
        try:
            page = Page.objects.get(slug=slug)
            # if the page is the same pk as the instance, cool, else raise the error
            if getattr(self, 'instance', False):
                if page.pk == self.instance.pk:
                    return self.cleaned_data['slug']
            raise forms.ValidationError('There is already a page with this slug.')
            
        except Page.DoesNotExist:
            return self.cleaned_data['slug']
        
        #try:
        #    page = Page.objects.get(slug=self.cleaned_data['slug'])
        #    # if the page is the same pk as the instance, cool, else raise the error
        #    if getattr(self, 'instance', False):
        #        if page.pk == self.instance.pk:
        #            return self.cleaned_data['slug']
        #    raise forms.ValidationError('There is already a page with this slug.')
        #except Page.DoesNotExist:
        #    return self.cleaned_data['slug']
