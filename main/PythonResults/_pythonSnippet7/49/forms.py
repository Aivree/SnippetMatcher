#!/usr/bin/env python
# -*- coding: utf-8 -*-
###########################################

import django.forms as forms
from django.forms import Form
from django.forms import ModelChoiceField
from django.forms import ChoiceField
from apps.ajaxsearch.models import *

# forms.py
import floppyforms as forms


class DatePicker(forms.DateInput):
    template_name = 'datepicker.html'

    class Media:
        js = (
            'js/jquery.min.js',
            'js/jquery-ui.min.js',
        )
        css = {
            'all': (
                'css/jquery-ui.css',
            )
        }


class DateForm(forms.Form):
    date = forms.DateField(widget=DatePicker)


def get_my_choices():
    # you place some logic here
    choices_list = []   
    return choices_list
 
class MyForm(forms.Form):
    def __init__(self, *args, **kwargs):
        super(MyForm, self).__init__(*args, **kwargs)
        self.fields['my_choice_field'] = forms.ChoiceField(
            choices=get_my_choices() )



########################################


###############################################################

###############################################################
from django.template.defaultfilters import slugify

def upload_to(path, attribute):    
    def upload_callback(instance, filename):
        return '{0}{1}/{2}'.format(path, unicode(slugify(getattr(instance, attribute))), filename)
    
    return upload_callback

###############################################################
## Input data forms

# from apps.ajaxsearch.models import *

# class AlbumForm(forms.ModelForm):
#     class Meta:
#         model = Album
#         fields = ('title',
#             'rating',
#             )


# class TagForm(forms.ModelForm):
#     class Meta:
#         model = Tag
#         fields = ('tag',)


# class ImagesForm(forms.ModelForm):
#     class Meta:
#         model = Images
#         fields = ('id', 
#             'image_select_thumb',
#             'image_select_push',            
#             'image_select',
#             'image_outtake',
#             )


# class MetadataForm(forms.ModelForm):
#     class Meta:
#         model = Metadata
#         fields = ('metadata_type', 
#             'metadata_tag', 
#             'metadata_value', 
#             'keywords',
#             )



    # if form.is_valid():
    #     subject = form.cleaned_data['subject']
    #     message = form.cleaned_data['message']
    #     sender = form.cleaned_data['sender']
    #     cc_myself = form.cleaned_data['cc_myself']

    #     recipients = ['info@example.com']
    #     if cc_myself:
    #         recipients.append(sender)

    #     from django.core.mail import send_mail
    #     send_mail(subject, message, sender, recipients)
    #     return HttpResponseRedirect('/thanks/') # Redirect after POS

