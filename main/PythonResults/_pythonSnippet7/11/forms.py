from django.forms import ModelForm, TextInput
from decisions.models import Decision


class DecisionForm(ModelForm):

    class Meta:
        model = Decision
        exclude = ('created', )
        widgets = {
            'author': TextInput(),
            'event': TextInput(),
        }

    def __init__(self, *args, **kwargs):

        super(DecisionForm, self).__init__(*args, **kwargs)
        #self.fields['author'].widget.attrs['disabled'] = True
        self.fields['author'].widget.attrs['readonly'] = True
        #self.fields['event'].widget.attrs['disabled'] = True
        self.fields['event'].widget.attrs['readonly'] = True

    def clean_author(self):
        instance = getattr(self, 'instance', None)
        if instance and instance.pk:
            return instance.author
        else:
            return self.cleaned_data['author']

    def clean_event(self):
        instance = getattr(self, 'instance', None)
        if instance and instance.pk:
            return instance.event
        else:
            return self.cleaned_data['event']