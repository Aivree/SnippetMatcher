# -*- coding: utf-8 -*-
from django import forms
from cc.shop.models import Order

class ViewOrderForm(forms.Form):
    email = forms.EmailField()
    ref = forms.CharField()
    
    def clean(self):
        try:
            cleaned_data = self.cleaned_data
            Order.objects.get(email=cleaned_data.get('email'), ref=cleaned_data.get('ref'))
        except Order.DoesNotExist:
            raise forms.ValidationError('Incorrect details, try again')
        return cleaned_data
    
    def save(self):
        try:
            return Order.objects.get(email=self.cleaned_data['email'], ref=self.cleaned_data['ref'])
        except Order.DoesNotExist:
            pass
    

class OrderForm(forms.ModelForm):
    
    class Meta:
        model = Order
    
    def __init__(self, *args, **kwargs):
        super(OrderForm, self).__init__(*args, **kwargs)
        instance = getattr(self, 'instance', None)
        if instance and instance.id:
            self.fields['ref'].widget.attrs['readonly'] = True
            self.fields['transaction_id'].widget.attrs['readonly'] = True
            self.fields['shipping_method'].widget.attrs['readonly'] = True
        
    

