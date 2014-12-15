from sqlalchemy.orm import mapper
from fluffweb import db
from . import list

def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%d.%m.%Y")]

lists = db.Table('lists',
    db.Column('todo_id', db.Integer, db.ForeignKey('todo.id')),
    db.Column('list_id', db.Integer, db.ForeignKey('list.id'))
    )

#mapper(list.List, lists)

class Todo(db.Model):
    __tablename__ = 'todo'

    id = db.Column(db.Integer, primary_key=True)
    description = db.Column(db.String(64))
    date = db.Column(db.DATETIME)
    gone_by = db.Column(db.Integer)
    done = db.Column(db.Boolean)
    lists = db.relationship(list.List, secondary=lists, backref=db.backref('todo', lazy='dynamic'))
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    def __init__(self, description, date, gone_by, done, user_id):
        self.description = description
        self.date = date
        self.gone_by = gone_by
        self.done = done
        self.user_id = user_id

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'description': self.description,
            'date': dump_datetime(self.date),
            'gone_by': self.gone_by,
            'done': self.done
        }

    def write_list(self, text):
        pass

    def __repr__(self):
        return u'<Todo: %s>' % self.description