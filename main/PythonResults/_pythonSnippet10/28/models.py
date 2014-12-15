from app import db, logger
from werkzeug.security import generate_password_hash, check_password_hash
import datetime
import requests
import uuid

ROLE_USER = 0
ROLE_ADMIN = 1

#REPORT STATUS
# 0 = NEW
# 1 = PROCESSING
# 3 = FINISHED

def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%Y-%m-%d"), value.strftime("%H:%M:%S")]

class User(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    name = db.Column(db.String(64))
    # admin has email == "admin"
    email = db.Column(db.String(120), index = True, unique = True)
    pw_hash = db.Column(db.String)
    role = db.Column(db.SmallInteger, default = ROLE_USER)
    last_seen = db.Column(db.DateTime)

    def __init__(self, email=None, password=None, *args, **kwargs):
        super(User, self).__init__(*args, **kwargs)
        self.email = email
        self._set_password(password)

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def is_admin(self):
        if self.role == ROLE_ADMIN:
            return True
        return False

    def get_id(self):
        return unicode(self.id)

    def _set_password(self, password):
        self.pw_hash = generate_password_hash(password)

    def reset_password(self, password):
        self._set_password(password)
        db.session.commit()

    def check_password(self, password):
        if self.pw_hash is None:
            return False
        else:
            return check_password_hash(self.pw_hash, password)

    def __repr__(self):
        return '<User %r>' % (self.email)   
        
class Report(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    timestamp = db.Column(db.DateTime)
    report_type = db.Column(db.SmallInteger)
    status = db.Column(db.SmallInteger, default = 0)
    geo_lat = db.Column(db.Float)
    geo_lng = db.Column(db.Float)
    content = db.Column(db.Text)
    city = db.Column(db.Text, default = "")

    # contains just the filename. Path and extension are constant
    img_src = db.Column(db.String(64))
    vid_src = db.Column(db.String(64))

    def __init__(self, *args, **kwargs):
        super(Report, self).__init__(*args, **kwargs)
        self.timestamp = datetime.datetime.utcnow()
        db.session.commit()

    def get_attribute_list(self):
        return [self.id, self.timestamp, self.report_type, self.status,
                self.geo_lat, self.geo_lng, self.content, self.city]

    def set_city(self):
        if self.geo_lat is None:
            return

        try:

            r = requests.get("http://maps.googleapis.com/maps/api/geocode/json"
                             "?latlng=%f,%f&sensor=false" % (self.geo_lat, self.geo_lng))
            j = r.json()

            acs = []

            for p in j["results"]:
                if "address_components" in p:
                    acs.extend(p["address_components"])
            
            if not acs:
                raise Exception

            for d in acs:
                try:
                    if "locality" in d["types"]:
                        self.city = d["short_name"]
                        break
                except:
                    continue

            else:
                raise Exception

        except:
            self.city = "?"

        finally:
            db.session.commit()

    def serialize(self):
        """Return object data in easily serializeable format"""

        d = {'id': self.id,
             'timestamp': dump_datetime(self.timestamp),
             'status': self.status,
             'type': self.report_type
             }

        if self.geo_lat is not None:
            d.update(location = (self.geo_lat, self.geo_lng))

        if self.content is not None:
            d.update(content = self.content)

        return d

    def __repr__(self):
        return '<Post %r>' % (self.id)
