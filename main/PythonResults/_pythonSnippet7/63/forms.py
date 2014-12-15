__author__ = 'heddevanderheide'

# Django specific
from django import forms
from django.contrib.auth import get_user_model
from django.contrib.auth.models import Permission
from django.db.models import Q
from django.utils.translation import ugettext_lazy as _

# App specific
from fabric_interface.models import User


class UserForm(forms.ModelForm):
    email = forms.EmailField(label=_("E-mail"))
    password1 = forms.CharField(widget=forms.PasswordInput, label=_("Password"))
    password2 = forms.CharField(widget=forms.PasswordInput, label=_("Password (again)"))

    first_name = forms.CharField(label=_(u"First name"), required=False)
    family_name_prefix = forms.CharField(label=_(u"Family name prefix"), required=False)
    family_name = forms.CharField(label=_(u"Family name"), required=False)

    class Meta:
        fields = ('email', 'password1', 'password2')
        model = User

    def clean_email(self):
        """
        Validate that the supplied email address is unique for the
        site.
        """
        Model = get_user_model()

        if Model.objects.filter(email__iexact=self.cleaned_data['email']):
            raise forms.ValidationError(
                _("This email address is already in use. Please supply a different email address.")
            )
        return self.cleaned_data['email']

    def clean(self):
        """
        Verifiy that the values entered into the two password fields
        match. Note that an error here will end up in
        ``non_field_errors()`` because it doesn't apply to a single
        field.
        """
        if 'password1' in self.cleaned_data and 'password2' in self.cleaned_data:
            if self.cleaned_data['password1'] != self.cleaned_data['password2']:
                raise forms.ValidationError(_("The two password fields didn't match."))
        return self.cleaned_data

    def save(self, commit=True):
        return User.objects.create_user(
            email=self.cleaned_data['email'],
            password=self.cleaned_data['password1'],
            first_name=self.cleaned_data['first_name'],
            family_name_prefix=self.cleaned_data['family_name_prefix'],
            family_name=self.cleaned_data['family_name'],
        )


class UserUpdateForm(forms.ModelForm):
    first_name = forms.CharField(label=_(u"First name"), required=False)
    family_name_prefix = forms.CharField(label=_(u"Family name prefix"), required=False)
    family_name = forms.CharField(label=_(u"Family name"), required=False)

    class Meta:
        fields = ('email', 'first_name', 'family_name_prefix', 'family_name')
        model = User

    def __init__(self, *args, **kwargs):
        super(UserUpdateForm, self).__init__(*args, **kwargs)
        instance = getattr(self, 'instance', None)
        if instance and instance.pk:
            self.fields['email'].widget.attrs['readonly'] = True

    def clean_email(self):
        instance = getattr(self, 'instance', None)
        if instance and instance.pk:
            return instance.email
        else:
            return self.cleaned_data['email']


class UserPermissionsUpdateForm(forms.ModelForm):
    # todo add object_level permission edit functionality

    class Meta:
        fields = ('user_permissions',)
        model = User

    def __init__(self, *args, **kwargs):
        super(UserPermissionsUpdateForm, self).__init__(*args, **kwargs)
        self.fields['user_permissions'].queryset = Permission.objects.filter(
            Q(content_type__model='project') |
            Q(content_type__model='stage') |
            Q(content_type__model='host') |
            Q(content_type__model='formula') |
            Q(content_type__model='fabfile') |
            Q(content_type__model='configuration')
        )
        self.fields['user_permissions'].label = _(u"Global user permissions")