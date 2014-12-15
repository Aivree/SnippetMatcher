# -*- coding:UTF-8 -*-
from django import forms
from django.contrib import admin
from django.core.exceptions import ValidationError
from portal.models import News, Friends,Student,Studentworks,Teacher
from django.forms.fields import FileField  



class NewsForm(forms.ModelForm):

    content = forms.CharField(label='aaa',widget=forms.Textarea(attrs={'class':'vLargeTextField'}))
    # def clean_show_home_page(self):
    #
    #     data = self.cleaned_data['show_home_page']
    #     print data
    #     count=News.objects.filter(show_home_page__exact='1').count()
    #     print count
#        raise forms.ValidationError("show_home_page er")

        # Always return the cleaned data, whether you have changed it or
        # not.
       #return data
    def clean(self):
        # print self.cleaned_data['show_home_page']
        # print self
        # raise ValidationError('Draft entries may not have a publication date.')
        # s=self.data
        # self.cleaned_data = {}
        #         # �������һ��for �Ǵ� django��forms.py�е� full_clean �и���4��
        # for name, field in self.fields.items():
        #     # value_from_datadict() gets the data from the data dictionaries.
        #     # Each widget type knows how to retrieve its own data, because some
        #     # widgets split data over several HTML fields.
        #     value = field.widget.value_from_datadict(self.data, self.files, self.add_prefix(name))
        #     try:
        #         if isinstance(field, FileField):
        #             initial = self.initial.get(name, field.initial)
        #             value = field.clean(value, initial)
        #         else:
        #             value = field.clean(value)
        #         self.cleaned_data[name] = value
        #         if hasattr(self, 'clean_%s' % name):
        #             value = getattr(self, 'clean_%s' % name)()
        #             self.cleaned_data[name] = value
        #     except ValidationError, e:
        #         self._errors[name] = self.error_class(e.messages)
        #         if name in self.cleaned_data:
        #             del self.cleaned_data[name]
        # #cl=self.cleaned_data
        # #debug()<<<�����õ�,�鿴cl��ֵ,��Ҫ�ǿ�self.cleaned_data��ֵ,���return��,�Ϳ�������
        return self.cleaned_data  
    
    class Meta:
        model = News
        fields = ('title','publishtime','picture','bigpic','content','show_home_page')

#    def __init__(self, *args, **kwargs):
#
#        if kwargs.get('instance'):
#            email = kwargs['instance'].email
#            kwargs.setdefault('initial', {})['confirm_email'] = email
#
#        return super(ContactForm, self).__init__(*args, **kwargs)