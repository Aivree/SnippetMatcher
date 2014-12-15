from django.forms import ModelForm, TextInput
from events.models import Event


class EventForm(ModelForm):

    class Meta:
        model = Event
        widgets = {
            'author': TextInput(),
        }

    def __init__(self, *args, **kwargs):

        super(EventForm, self).__init__(*args, **kwargs)
        #instance = getattr(self, 'instance', None)
        #if instance and instance.pk:
        #self.fields['author'].widget.attrs['disabled'] = True
        self.fields['author'].widget.attrs['readonly'] = True

    def clean_author(self):
        instance = getattr(self, 'instance', None)
        if instance and instance.pk:
            return instance.author
        else:
            return self.cleaned_data['author']