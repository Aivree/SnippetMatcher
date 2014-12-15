from bootstrap3_datetime.widgets import DateTimePicker
from django import forms
from profiles.models import MyUser


class RegistrationForm(forms.ModelForm):

    password2 = forms.CharField(label='Confirm Password', widget=forms.PasswordInput(attrs={'class': 'form-control'}))

    class Meta:
        model = MyUser
        fields = ['username', 'email', 'password']
        widgets = {
            'username': forms.TextInput(attrs={'class': 'form-control'}),
            'email': forms.TextInput(attrs={'class': 'form-control'}),
            'password': forms.PasswordInput(attrs={'class': 'form-control'}),
        }

    def clean(self):
        # Check that the two password entries match
        cleaned_data = super(RegistrationForm, self).clean()
        password1 = self.cleaned_data.get("password")
        password2 = self.cleaned_data.get("password2")
        if password1 and password2 and password1 != password2:
            raise forms.ValidationError("Passwords don't match")
        return cleaned_data

    def save(self, commit=True):
        user = super(RegistrationForm, self).save(commit=False)
        user.set_password(self.cleaned_data.get('password'))
        if commit is True:
            user.save()
        return user


class LoginForm(forms.ModelForm):
    class Meta:
        model = MyUser
        fields = ['email', 'password']
        widgets = {
            'email': forms.EmailInput(attrs={'class': 'form-control', 'placeholder': 'Email'}),
            'password': forms.PasswordInput(attrs={'class': 'form-control', 'placeholder': 'Password'}),
        }


class EditForm(forms.ModelForm):

    class Meta:
        model = MyUser
        fields = ['username', 'email', 'avatar', 'first_name', 'last_name', 'hometown', 'date_of_birth', 'category1']
        labels = {
            'category1': 'Interest',
        }
        widgets = {
            'email': forms.TextInput(attrs={'class': 'form-control'}),
            'username': forms.TextInput(attrs={'class': 'form-control'}),
            'first_name': forms.TextInput(attrs={'class': 'form-control'}),
            'last_name': forms.TextInput(attrs={'class': 'form-control'}),
            'hometown': forms.TextInput(attrs={'class': 'form-control'}),
            'date_of_birth': DateTimePicker(options={'format': 'MM/DD/YYYY', 'pickTime': 'false'}),
            'category1': forms.Select(attrs={'class': 'form-control'}),
        }

    def clean(self):
        cleaned_data = super(EditForm, self).clean()

        is_changed = False

        for field in self.Meta.fields:
            if hasattr(self.instance, field) and getattr(self.instance, field) != self.cleaned_data.get(field):
                is_changed = True

        if not is_changed:
            raise forms.ValidationError("Nothing seems to be changed")

        return cleaned_data

