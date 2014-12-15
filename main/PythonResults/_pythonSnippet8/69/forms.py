from django.forms.forms import Form
from django.forms.fields import FileField, DateField, CharField, IntegerField,\
    ChoiceField, MultipleChoiceField
from django.utils.translation import ugettext_lazy
from django.forms.widgets import DateInput, CheckboxSelectMultiple
from django.forms.models import ModelForm
from PSI_projektas.stock_info.models import Orderfailure, Operation,\
    StockKeepingUnit
from django.core.exceptions import ValidationError
from django.contrib.auth.forms import PasswordChangeForm

class FileForm(Form):
    file_name = FileField(label=ugettext_lazy('File name'))

class QualityForm(Form):
    SKU_AMOUNT = '1'
    ORDER_AMOUNT = '2'
    TYPE_CHOICES = ((SKU_AMOUNT, ugettext_lazy('Anount percentage')),
                    (ORDER_AMOUNT, ugettext_lazy('Order percentage')))
    type = ChoiceField(label=ugettext_lazy('Chart type'), choices=TYPE_CHOICES)
    date_from = DateField(label=ugettext_lazy('Date from'))
    date_to = DateField(label=ugettext_lazy('Date to'))
    group = MultipleChoiceField(label=ugettext_lazy('Group'),
                                required=False,
                                widget=CheckboxSelectMultiple())
    
    def __init__(self, *args, **kwargs):
        super(QualityForm, self).__init__(*args, **kwargs)
        self.fields['group'].choices = StockKeepingUnit.objects.all().values_list('group','group').distinct()

class OrderFailureForm(ModelForm):
    custom_operation = IntegerField(label=ugettext_lazy('Operation ID'))
    
    class Meta:
        model = Orderfailure
        fields = ('custom_operation', 'reason', 'amount')
        
    def clean_operation(self, *args, **kwargs):
        operation_id = self.cleaned_data['custom_operation']
        operations = Operation.objects.filter(operation_id=operation_id)
        if operations.count() == 0:
            raise ValidationError(ugettext_lazy('Operation provided not found'))
        return operations[0]
    
    def save_model(self, commit=True):
        value = self.cleaned_data['custom_operation']
        self.instance.operation = Operation.objects.get(operation_id=value)
        ModelForm.save_model(self, commit)
    
    def __init__(self, *args, **kwargs):
        ModelForm.__init__(self, *args, **kwargs)
        if self.instance:
            try:
                self.fields['custom_operation'].initial = self.instance.operation.operation_id
            except:
                pass
    
class ValidatingUserForm(PasswordChangeForm):
    def save(self, commit=True):
        return PasswordChangeForm.save(self, commit=commit)
    
    MIN_LENGTH = 6
    
    def clean_new_password1(self):
        password1 = self.cleaned_data.get('new_password1')
        if len(password1) < self.MIN_LENGTH:
            raise ValidationError(ugettext_lazy('Password must be at least 6 characters length'))
        return password1