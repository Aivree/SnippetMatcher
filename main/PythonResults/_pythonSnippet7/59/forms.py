# -*- coding: utf-8 -*-
from form_utils.forms import BetterForm, BetterModelForm
from sq_core.forms.widgets import AutocompleteSelect


class Form(BetterForm):
    context = {}
    def save(self):
        pass

    def update_context(self,context):
        self.context.update(context)

    # Возвращает cleaned_data, но только с заполненными в форме полями
    def get_filled_data(self):
        return dict((k, v) for k, v in self.cleaned_data.iteritems()  if v != 'none')


class ModelForm(BetterModelForm):
    pass


class AutocompleteForm(object):
    """
    Добавляет в форму поддержку автокомплита
    """
    def __init__(self, *args, **kwargs):
        super(AutocompleteForm, self).__init__(*args, **kwargs)

        if 'instance' in kwargs:
            for field_id, field in self.fields.iteritems():
                if isinstance(field.widget, AutocompleteSelect):
                    property = getattr(kwargs['instance'], field_id)
                    if property:
                        field.choices = [property.choice(), ]
                    else:
                        field.choices = []
        elif 'data' in kwargs:
            for field_id, field in self.fields.iteritems():
                if isinstance(field.widget, AutocompleteSelect) and field_id in kwargs['data']:
                    value = kwargs['data'][field_id]
                    field.choices = [(value, value), ]
