# -*- coding: utf-8 -*-
import json
from app import db,app
from hashlib import md5
# import flask.ext.whooshalchemy as whooshalchemy
import re
from sqlalchemy import Sequence
from collections import OrderedDict
from datetime import datetime


ROLE_USER = 0
ROLE_ADMIN = 1

def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%Y-%m-%d"), value.strftime("%H:%M:%S")]

class DateEncoder:
    def default(self, obj):
        if hasattr(obj, 'isoformat'):
            return obj.isoformat()
        else:
            return str(obj)
        return json.JSONEncoder.default(self, obj)

    
   
   
    
            
class Contact(db.Model):
    
    # The whoosh searchable field declaration for full-text search
    # __searchable__ = ['about_me','address','fname']
    
    id = db.Column(db.Integer, Sequence('contact_id_seq'), primary_key = True)
    fname = db.Column(db.String(64), unique = True)
    address = db.Column(db.String(464))
    email = db.Column(db.String(120), unique = True)
    role = db.Column(db.SmallInteger, default = ROLE_USER)
    about_me = db.Column(db.String(140))
    last_seen = db.Column(db.DateTime)
    
    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    @property
    def serialize(self):
       """Return object data in easily serializeable format"""
       return {
           'id':self.id,
           'fname':self.fname,
           'address':self.address,
           'email':self.email,
           'role':self.role,
           'about_me':self.about_me,
           'last_seen': dump_datetime(self.last_seen)
       }
    
    
            
            
    @staticmethod
    def make_unique_name(fname):
       if Contact.query.filter_by(fname = fname).first() == None:
           return fname
       version = 2
       while True:
           fname = fname + str(version)
           if Contact.query.filter_by(fname = fname).first() == None:
               break
           version += 1
       return fname        

    @staticmethod
    def make_valid_name(fname):
        return re.sub('[^a-zA-Z0-9_\.]', '', fname) 
        
    
    def __repr__(self):
        return self.jsonize()        

# for search to be initialized, call this
# whooshalchemy.whoosh_index(app, Contact)        
