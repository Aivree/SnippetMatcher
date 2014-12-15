from django import forms
from principal.models import Producto
from principal.models import Clientes
from principal.models import Factura
from principal.models import Pagos
from principal.models import Estado_Ciudad
from principal.models import Dolar_peso
import re

class ProductosForm(forms.ModelForm):
	class Meta:
		model = Producto
		exclude = ("Activo")
	def __init__(self, *args, **kwargs):
                user = kwargs.pop('user','')
                super(ProductosForm, self).__init__(*args, **kwargs)

class ProductosBajasForm(forms.ModelForm):
	class Meta:
		model = Producto
		exclude = ("Activo")
	def __init__(self, *args, **kwargs):
                user = kwargs.pop('user','')
                super(ProductosBajasForm, self).__init__(*args, **kwargs)
                self.fields['Clave_Producto'].widget.attrs['disabled']=True
                self.fields['Tipo'].widget.attrs['disabled']=True
                self.fields['Nombre'].widget.attrs['disabled']=True
                self.fields['Unidad_de_medida'].widget.attrs['disabled']=True
                self.fields['Descripcion'].widget.attrs['disabled']=True
                self.fields['Precio'].widget.attrs['disabled']=True
                

class ClientesForm(forms.ModelForm):
	class Meta:
                model = Clientes
                exclude = ("Activo")

	def clean(self):
                data=self.cleaned_data
                rfc=self.cleaned_data.get('RFC')
                tipo=self.cleaned_data.get('Fisico_Moral')
                numInt=self.cleaned_data.get('Numero_interior')
                codpos=self.cleaned_data.get('CP')
                estCiu=self.cleaned_data.get('Estado_Ciudad')

                if not numInt:
                        data['Numero_interior']=0
                if not codpos:
                        data['CP']=0
                if tipo == 'F' and len(rfc) != 13:
                        self._errors['RFC']="Un cliente fisico debe tener un RFC de 13 caracteres"
                        return data
                        #raise forms.ValidationError("Un cliente fisico debe tener un RFC de 13 caracteres")
                if tipo == 'M' and len(rfc) != 12:
                        self._errors['RFC']="Un cliente moral debe tener un RFC de 12 caracteres"
                        return data
                        #raise forms.ValidationError("Un cliente moral debe tener un RFC de 12 caracteres")
                if tipo == 'F':
                        matchObj = re.match( r'[A-Za-z]{4}\d{6}(?:[A-Za-z\d]{3})?$', rfc)
                        if not matchObj:
                           self._errors['RFC']="RFC invalido"
                           return data
                if tipo == 'M':
                        matchObj = re.match( r'[A-Za-z]{3}\d{6}(?:[A-Za-z\d]{3})?$', rfc)
                        if not matchObj:
                           self._errors['RFC']="RFC invalido"
                           return data
                return data

                
	def __init__(self, *args, **kwargs):
                user = kwargs.pop('user','')
                super(ClientesForm, self).__init__(*args, **kwargs)
                #queryset = self.fields['Estado_Ciudad'].queryset
                #choices = [(Estado_Ciudad.pk) for est in queryset]
                self.fields['Estado_Ciudad'].queryset=Estado_Ciudad.objects.order_by('Nombre_Estado')
                #'Nombre_Estado','Nombre_Ciudad'



class ClientesBajasForm(forms.ModelForm):
	class Meta:
		model = Clientes
		exclude = ("Activo")
	def __init__(self, *args, **kwargs):
                user = kwargs.pop('user','')
                super(ClientesBajasForm, self).__init__(*args, **kwargs)
                self.fields['RFC'].widget.attrs['disabled']=True
                self.fields['Fisico_Moral'].widget.attrs['disabled']=True
                self.fields['Nombres'].widget.attrs['disabled']=True
                self.fields['Apellidos'].widget.attrs['disabled']=True
                self.fields['Calle'].widget.attrs['disabled']=True
                self.fields['Numero_interior'].widget.attrs['disabled']=True
                self.fields['Numero_exterior'].widget.attrs['disabled']=True
                self.fields['Colonia'].widget.attrs['disabled']=True
                self.fields['CP'].widget.attrs['disabled']=True
                self.fields['Estado_Ciudad'].widget.attrs['disabled']=True
                self.fields['Cliente_Moroso'].widget.attrs['disabled']=True
                self.fields['Limite_Credito'].widget.attrs['disabled']=True


class FacturaForm(forms.ModelForm):
	class Meta:
		model = Factura

class PagosForm(forms.ModelForm):
	class Meta:
		model = Pagos

class Estado_CiudadForm(forms.ModelForm):
	class Meta:
		model = Estado_Ciudad
        
class Dolar_pesoForm(forms.ModelForm):
    class Meta:
        model = Dolar_peso

    def __init__(self, *args, **kwargs):
        super(Dolar_pesoForm, self).__init__(*args, **kwargs)
        instance = getattr(self, 'instance', None)
        if instance and instance.pk:
            self.fields['fecha'].widget.attrs['readonly'] = True

    def clean_fecha(self):
        instance = getattr(self, 'instance', None)
        if instance and instance.pk:
            return instance.fecha
        else:
            return self.cleaned_data['fecha']