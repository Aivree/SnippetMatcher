#-*- coding: utf-8 -*-
from django import forms
from django.contrib.auth.models import User
from users.models import Client

class LoginForm(forms.Form):
	user = forms.CharField(max_length = 30)
	password = forms.CharField()

class UserInscriptionForm(forms.ModelForm):
	password_validation = forms.CharField(required = True, label = u"Entrez de nouveau votre mot de passe", widget = forms.PasswordInput())
	class Meta:
		model = Client
		fields = ['username', 'first_name', 'last_name', 'phone', 'kgibss', 'email', 'password']

	def	__init__(self, *args, **kwargs):
		super(UserInscriptionForm, self).__init__(*args, **kwargs)
		self.fields['first_name'].required = True
		self.fields['last_name'].required = True
		self.fields['email'].required = True
		self.fields['password'].widget = forms.PasswordInput()

	def	clean(self):
		cleaned_data = super(UserInscriptionForm, self).clean()
		pass1 = cleaned_data.get('password')
		pass2 = cleaned_data.get('password_validation')
		if pass1 != None and pass1 != pass2:
			raise forms.ValidationError("Erreur : les mots de passe ne correspondent pas.")
		return cleaned_data

class UserEditForm(forms.ModelForm):
	class Meta:
		model = Client
		fields = ['first_name', 'last_name', 'email', 'phone', 'kgibss']

	def __init__(self, *args, **kwargs):
		self.request = kwargs.pop('request', None)
		super(UserEditForm, self).__init__(*args, **kwargs)
		self.fields['first_name'].widget.attrs['disabled'] = True
		self.fields['last_name'].widget.attrs['disabled'] = True
		self.fields['first_name'].required = True
		self.fields['last_name'].required = True
		self.fields['email'].required = True

	def	clean_first_name(self):
		instance = getattr(self, 'instance', None)
		return instance.first_name

	def	clean_last_name(self):
		instance = getattr(self, 'instance', None)
		return instance.last_name
