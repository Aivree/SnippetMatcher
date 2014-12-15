#
# Copyright (C) 2014, Martin Owens <doctormo@gmail.com>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

from django.forms import *

import sys
from . import models

__all__ = ('BugForm','BugAdminForm',)

class BugForm(ModelForm):
    comment = CharField(widget=Textarea(attrs={'rows':None, 'cols':None}))
    reporter = CharField(max_length=128)

    class Meta:
       model = models.Bug
       fields = ('product', 'component', 'title', 'comment', 'reporter')

    def clean_product(self):
        ret = self.cleaned_data['product']
        if ret:
            return Product.objects.get(extra__slug=ret)
        raise FieldError("Product is required")

    def clean_component(self):
        ret = self.cleaned_data['component']
        if ret:
            return Component.objects.get(extra__slug=ret)
        return Component.get_default()

    def clean_reporter(self):
        eml = self.cleaned_data['reporter']
        if isinstance(eml, basestring):
            # Check for existing user, fail if no password
            eml = User.objects.get(email=eml)
        return eml

    def clean_comment(self):
        comment = self.cleaned_data['comment']
        #comment = bug.comments.create(content=comment) 
        return [ comment ]


class BugAdminForm(ModelForm):
    class Meta:
        model = models.Bug

    def __init__(self, *args, **kwargs):
        super(BugAdminForm, self).__init__(*args, **kwargs)
        self._choices = list( self.add_choices() )

    def add_choices(self):
        product = None
        if self.instance.id and self.instance.product:
                product = self.instance.product
        for (name, field) in self.fields.items():
            if isinstance(field, CharField) and field.max_length == 64:
                try:
                    model = getattr(models, name.title())
                    queryset = model.objects.all()
                    if hasattr(model, 'product') and product:
                        queryset = queryset.filter(product=product)
                    if hasattr(model, 'active'):
                        queryset = queryset.filter(active=True)
                    initial = None
                    if self.instance.id:
                        try:
                            initial = model.objects.get(value=getattr(self.instance, name))
                        except model.DoesNotExist as error:
                            sys.stderr.write("ERROR: %s\n" % str(error))

                    self.fields[name] = ModelChoiceField(queryset=queryset, initial=initial)
                    yield name
                except Exception:
                    raise
    def clean(self):
        cleaned_data = super(BugAdminForm, self).clean()
        for name in self._choices:
            if name in cleaned_data and cleaned_data[name] is not None:
                cleaned_data[name] = cleaned_data[name].value
        return cleaned_data

