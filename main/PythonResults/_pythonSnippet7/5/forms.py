from django import forms
from academic.models import Institute, InstituteClass, InstituteCourse,InstituteSubjectMaster, InstituteTeacher,Assignment

from registration.forms import RegistrationForm
        
class InstituteAdminForm(forms.ModelForm):
       
    class Meta:
        model = Institute        
        


class InstituteCourseAdminForm(forms.ModelForm):
    class Meta:
        model = InstituteCourse
        fields = ('name','duration','institute','description')

class InstituteCourseSiteAdminForm(forms.ModelForm):
    class Meta:
        model = InstituteCourse
        fields = ('name','duration','institute','description')
    
    def __init__(self, *args, **kwargs):
        super(InstituteCourseSiteAdminForm, self).__init__(*args, **kwargs)
        instance = getattr(self, 'instance', None)

        #if instance: 
        #    self.fields['institute'].queryset = Institute.objects.filter(microsite=self.microsite)
                 
        
class InstituteClassAdminForm(forms.ModelForm):
    class Meta:
        model = InstituteClass
        fields = ('name','section','icourse','class_teacher')

class InstituteClassSiteAdminForm(forms.ModelForm):
    class Meta:
        model = InstituteClass
        fields = ('name','section','icourse','class_teacher')

    def __init__(self, *args, **kwargs):
        super(InstituteClassSiteAdminForm, self).__init__(*args, **kwargs)
        instance = getattr(self, 'instance', None)
        if instance: 
            self.fields['icourse'].queryset = InstituteCourse.objects.filter(institute=self.inst_obj)

        
class InstituteSubjectMasterAdminForm(forms.ModelForm):
    
    class Meta:
        model = InstituteSubjectMaster
        fields= ('name','institute')

class InstituteSubjectMasterSiteAdminForm(forms.ModelForm):
    
    
    class Meta:
        model = InstituteSubjectMaster
        fields = ('name','institute')
        
    def __init__(self, *args, **kwargs):
        super(InstituteSubjectMasterSiteAdminForm, self).__init__(*args, **kwargs)
        instance = getattr(self, 'instance', None)
        if instance: 
            self.fields['institute'].queryset = Institute.objects.filter(microsite=self.microsite)



class InstituteTeacherAdminForm(forms.ModelForm):
    username = forms.CharField(max_length=25,required = True)
    password = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label='Password')
    password2 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label='Confirm Password') 
    email = forms.EmailField(label="Enter an Email Id  ")
    institute = forms.ModelChoiceField(queryset=Institute.objects.all(),label="Select an Institute  ")
    usertype = forms.IntegerField(label="", widget=forms.HiddenInput,required=False,initial=8)
    
    class Meta :
        model = InstituteTeacher
        fields = ('name','description','user','username','password','password2',
                  'email','institute','usertype')
    
    
    def clean(self):
        cleaned_data = self.cleaned_data
        passwordentered = cleaned_data.get('password')
        passwordconfirmed = cleaned_data.get('password2')
        
        if passwordentered != passwordconfirmed :
            raise forms.ValidationError('Both the passwords are not matching, Please re enter your password')
        
        self.formid = 'InstituteTeacherAdminForm'
        
        return cleaned_data
#            
#    def clean_username(self):
#        if self.request:
#            from django.core.cache import cache
#            siteobj=cache.get('micrositeinstance-'+self.request.session.session_key)
#
#        if RegistrationProfile.objects.check_duplicate_profile(self.cleaned_data['username'],siteobj):
#            raise forms.ValidationError('There is a username already for this institute')
#        
#        return self.cleaned_data['username']     

class InstituteTeacherSiteAdminForm(forms.ModelForm):
    username = forms.CharField(max_length=25,required = True)
    password = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label='Password')
    password2 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label='Confirm Password') 
    email = forms.EmailField(label="Enter an Email Id  ")
    
    def clean(self):
        cleaned_data = self.cleaned_data
        passwordentered = cleaned_data.get('password')
        passwordconfirmed = cleaned_data.get('password2')
        
        if passwordentered != passwordconfirmed :
            raise forms.ValidationError('Both the passwords are not matching, Please re enter your password')
        
        self.formid = 'InstituteTeacherSiteAdminForm'  # adding a parameter in form to check if the user is submitting using this form. 
        return cleaned_data
            
#    def clean_username(self):
#        if self.request:
#            from django.core.cache import cache
#            siteobj=cache.get('micrositeinstance-'+self.request.session.session_key)
#
#        if RegistrationProfile.objects.check_duplicate_profile(self.cleaned_data['username'],siteobj):
#            raise forms.ValidationError('There is a username already for this institute')
#        
#        return self.cleaned_data['username']    
          
    class Meta :
        model = InstituteTeacher
        fields = ('name','description','username','password','password2',
                  'email')

class InstituteTeacherAdminEditForm(forms.ModelForm):

    class Meta :
        model = InstituteTeacher
        fields = ('name','description')
    

class AssignmentForm(forms.ModelForm):
    class Meta:
        model = Assignment
        #exclude = ('students','slug','teacher','courses','subjects','classes')