from app import db
from datetime import datetime, timedelta

ROLE_OUTSIDER = 2
ROLE_INSIDER = 1
ROLE_ADMIN = 0

def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%Y-%m-%d"), value.strftime("%H:%M:%S")]

class User(db.Model):
    """Base Database Model for Users"""
    __tablename__ = 'user'
    id = db.Column(db.Integer, primary_key=True)
    nickname = db.Column(db.String(64), index=True, unique=True)
    role = db.Column(db.SmallInteger, default=ROLE_OUTSIDER)
    phone_no = db.Column(db.String(30), index=True, unique=True)
    messages = db.relationship('Message', backref='sender', lazy='dynamic')

    __mapper_args__ = {
        'polymorphic_identity': 'user',
        'polymorphic_on': role
    }

    def sorted_messages(self):
        """ Return a users messages sorted by timestamp descending. """
        return self.posts.order_by(Message.timestamp.desc())

    @staticmethod
    def generate_fake(count=100):
        from sqlalchemy.exc import IntegrityError
        from faker import Faker
        fake = Faker()

        for i in range(count):
            u = User(phone_no=fake.numerify(text="+### ## ### ####"),
                     nickname=fake.user_name())
            db.session.add(u)
        try:
            db.session.commit()
        except IntegrityError:
            db.session.rollback()


class Presenter(User):
    __tablename__ = 'presenter'
    id = db.Column(db.Integer, db.ForeignKey('user.id'), primary_key=True)
    email = db.Column(db.String(120), index=True, unique=True)

    __mapper_args__ = {
        'polymorphic_identity': 'presenter',
    }


class Message(db.Model):
    """Base Database Model for text messages"""
    id = db.Column(db.Integer, primary_key=True)
    body = db.Column(db.String(250))
    timestamp = db.Column(db.DateTime, default=datetime.utcnow())
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    @staticmethod
    def generate_fake(count=100):
        from random import randint
        from faker import Faker
        fake = Faker()
        user_count = User.query.count()
        starttime = datetime.utcnow() - timedelta(seconds=count*5)
        for i in range(count):
            u = User.query.offset(randint(0, user_count - 1)).first()
            p = Message(body=fake.paragraph(),
                        timestamp=starttime + timedelta(seconds=i*5),
                        sender=u)
            db.session.add(p)
        db.session.commit()

    def sorted_messages(self):
        return self.order_by(Message.timestamp.desc())

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'id': self.id,
            'sender': self.sender.nickname,
            'timestamp': self.timestamp,
            'message': self.body
            # This is an example how to deal with Many2Many relations
            # 'many2many'  : self.serialize_many2many
        }

    @property
    def serialize_many2many(self):
        """
        Return object's relations in easily serializeable format.
        NB! Calls many2many's serialize property.
        """
        return [item.serialize for item in self.many2many]
