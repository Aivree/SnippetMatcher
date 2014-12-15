from django import forms
from django.utils.translation import ugettext_lazy as _ul
from django.contrib import auth
from django.contrib.auth.models import User
from django.conf import settings


def fields_to_bootstrap(fields):
    for key in fields:
        field = fields[key]
        if field.label and field.required:
            field.label += ' *'
        if field.__class__.__name__ in (
                'DateTimeField', 'DateField', 
                'TimeField', 'CharField', 'Textarea',
                'EmailField', 'IntegerField', 'FloatField',
                ):
            field.widget.attrs.update(
                {'class': field.widget.attrs.get('class', '') + u' form-control'}
                )


class MangoForm(forms.Form):

    def __init__(self, *args, **kwargs):
        super(MangoForm, self).__init__(*args, **kwargs)
        fields_to_bootstrap(self.fields)


class MangoModelForm(forms.ModelForm):

    def __init__(self, *args, **kwargs):
        super(MangoModelForm, self).__init__(*args, **kwargs)
        fields_to_bootstrap(self.fields)


class LoginForm(MangoForm):
    error_messages = {
        'invalid_auth': _ul(u"Invalid login or password."),
    }
    username = forms.EmailField(
        label=_ul(u'Username'),
        widget=forms.TextInput(attrs={'placeholder':_ul(u'Username')}),
        )
    password = forms.CharField(
        label=_ul(u'Password'),
        widget=forms.PasswordInput(attrs={'placeholder':_ul(u'Password')}),
        )

    def clean(self):
        cleaned_data = super(LoginForm, self).clean()
        if 'username' in cleaned_data.keys() and 'password' in cleaned_data.keys():
            for plugin in settings.INSTALLED_PLUGINS:
                plugin = __import__(plugin, fromlist=[plugin.strip('.')[1]])
                if hasattr(plugin, 'is_user_auth'):
                    if not getattr(plugin, 'is_user_auth')(cleaned_data['username'], cleaned_data['password']):
                        raise forms.ValidationError(self.error_messages['invalid_auth'])
            if auth.authenticate(
                    username=cleaned_data['username'], 
                    password=cleaned_data['password']
                    ) is None:
                raise forms.ValidationError(self.error_messages['invalid_auth'])
        return cleaned_data
