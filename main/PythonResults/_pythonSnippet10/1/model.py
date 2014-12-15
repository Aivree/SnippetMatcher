from datetime import datetime
from sqlalchemy import Column, Integer, DateTime
from PiFlash import db


def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return value.strftime("%Y-%m-%dT%H:%M:%S%z")


class PiMessage(db.Model):
    __tablename__ = 'messages'
    id = Column(db.Integer, primary_key=True)
    message = Column(db.String(1000))
    date_created = Column(DateTime())
    was_read = Column(Integer(), default=0)

    def __init__(self, message):
        self.message = message
        self.date_created = datetime.today()

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'id': self.id,
            'date_created': dump_datetime(self.date_created),
            'message': self.message,
            'was_read': self.was_read
        }