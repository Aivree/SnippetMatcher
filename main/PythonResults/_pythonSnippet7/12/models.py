from google.appengine.ext import db
from google.appengine.ext.db import djangoforms
from django.forms import *
import re

#class Country(db.Model):
#    def __unicode__(self):
#        return "%s"%(self.key().name())
#
#class TimeZone(Country):
#    def __unicode__(self):
#        return "%s"%(self.key().name())

class Location(db.Model):
    country     = db.StringProperty()
    city        = db.StringProperty()
    latitude    = db.IntegerProperty(default=0)
    longitude   = db.IntegerProperty(default=0)
    timezone    = db.IntegerProperty(default=0)
    def __unicode__(self):
        return "%s"%(self.key().name())
    

class LocationForm(djangoforms.ModelForm):
    def __init__(self, data=None, instance=None, *args, **kwargs):
        super(LocationForm, self).__init__(data=data,instance=instance, *args, **kwargs)
        if instance:
            self.toDDMMSS(instance,'latitude')
            self.toDDMMSS(instance,'longitude')
            self.toDDMMSS(instance,'timezone')

    def toDDMMSS(self,instance,attr):
        self.fields[attr+'_p'].initial=getattr(instance,attr)>=0
        self.fields[attr+'_dd'].initial="%02d"%(getattr(instance,attr)/3600)
        self.fields[attr+'_mm'].initial="%02d"%((getattr(instance,attr)/60)%60)
        if attr+'_ss' in self.fields:
            self.fields[attr+'_ss'].initial="%02d"%(getattr(instance,attr)%60)

    def fromDDMMSS(self,attr):
        self.cleaned_data[attr]= int(self.cleaned_data[attr+'_dd'])*3600 \
            + int(self.cleaned_data[attr+'_mm'])*60
        if attr+'_ss' in self.cleaned_data:
            self.cleaned_data[attr]+= int(self.cleaned_data[attr+'_ss']) 
        self.cleaned_data[attr] *= (self.cleaned_data[attr+'_p'] == 'True' and 1 or -1)
        
    def save(self):
        self.is_valid()
        self.cleaned_data['country']=re.sub(r"(\s*\([\+-]\d{2}:\d{2}\))?$"," (%s%s:%s)"%("-+"[eval(self.cleaned_data['timezone_p'])],self.cleaned_data['timezone_dd'],self.cleaned_data['timezone_mm']),self.cleaned_data['country'])
        self.fromDDMMSS('latitude')
        self.fromDDMMSS('longitude')
        self.fromDDMMSS('timezone')
        return djangoforms.ModelForm.save(self)
    class Meta:
        model = Location
    latitude_p  = ChoiceField([('True',"උතුරු"),('False',"දකුණු")])
    latitude_dd = IntegerField(max_value=89, min_value=0, widget=widgets.TextInput({'size':2}) )
    latitude_mm = IntegerField(max_value=59, min_value=0, widget=widgets.TextInput({'size':2}) )
    latitude_ss = IntegerField(max_value=59, min_value=0, widget=widgets.TextInput({'size':2}) )
    longitude_p  = ChoiceField([('True',"නැගෙනහිර"),('False',"බටහිර")])
    longitude_dd = IntegerField(max_value=180, min_value=0, widget=widgets.TextInput({'size':2}) )
    longitude_mm = IntegerField(max_value=59, min_value=0, widget=widgets.TextInput({'size':2}) )
    longitude_ss = IntegerField(max_value=59, min_value=0, widget=widgets.TextInput({'size':2}) )
    timezone_p  = ChoiceField([('True',"+"),('False',"-")])
    timezone_dd = ChoiceField([("%02d"%i,"%02d"%i) for i in range(15)])
    timezone_mm = ChoiceField([("%02d"%i,"%02d"%i) for i in range(60)])
    #timezone_ss = ChoiceField(max_value=59, min_value=0, widget=widgets.TextInput({'size':2}) )
