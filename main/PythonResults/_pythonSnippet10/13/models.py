"""
models.py

"""
from apps import db


def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%Y-%m-%d"), value.strftime("%H:%M:%S")]

    
class Event(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(255))
    title_cal = db.Column(db.String(255))
    content = db.Column(db.Text())
    host = db.Column(db.String(255))
    category_char = db.Column(db.String(255))
    category_host = db.Column(db.String(255))

    date_created = db.Column(db.DateTime(timezone=True), default=db.func.now())
    date_start = db.Column(db.DateTime(timezone=True))
    date_end = db.Column(db.DateTime(timezone=True))
    
    location = db.Column(db.String(255))
    link = db.Column(db.String(255))
    poster = db.Column(db.String(255))
    contact = db.Column(db.String(255))
    contact_open = db.Column(db.Boolean())

    acceptance = db.Column(db.Boolean())

    # http://stackoverflow.com/questions/7102754/jsonify-a-sqlalchemy-result-set-in-flask
    @property
    def serialize(self):
     """Return object data in easily serializeable format"""
     return {
     'title': self.title,
     'title_cal': self.title_cal,
     'date_start': dump_datetime(self.date_start),
     'date_end': dump_datetime(self.date_end)
     }