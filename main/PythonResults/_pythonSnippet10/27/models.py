import sqlalchemy.orm.exc
from sqlalchemy.exc import IntegrityError
import datetime
from bep_app import db


def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return value.strftime("%Y-%m-%d %H:%M:%S")


class Companies(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    company_name = db.Column(db.String(100), index=True, unique=True)
    documents = db.relationship('Documents', backref='comp', lazy='dynamic')

    def __repr__(self):
        return '<Company %r>' % (self.company_name)

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'company_id': self.id,
            'company_name': self.company_name,
            'documents': [item.serialize for item in self.documents.all()]
        }


class Documents(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    document_name = db.Column(db.String(100), nullable=False)
    document_number = db.Column(db.String(100))
    timestamp = db.Column(db.DateTime)
    fn_company = db.Column(db.Integer, db.ForeignKey('companies.id'))
    exemptfields = db.relationship('Exemptfields', backref='doc',
                                   lazy='dynamic')

    def __repr__(self):
        return '<Document %r, Number %r>' % (self.document_name,
                                             self.document_number)

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'document_id': self.id,
            'document_name': self.document_name,
            # 'Number': self.document_number,
            'timestamp': dump_datetime(self.timestamp),
            'exemptions': [item.serialize for item in self.exemptfields.all()]
        }


class Exemptfields(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    field_name = db.Column(db.String(100), nullable=False)
    field_path = db.Column(db.String(255), nullable=False)
    timestamp = db.Column(db.DateTime)
    fn_document = db.Column(db.Integer, db.ForeignKey('documents.id'))
    notes = db.relationship('Notes', backref='exempt', lazy='dynamic')

    def __repr__(self):
        return '<Exempt Field %r, Path %r>' % (self.field_name,
                                               self.field_path)

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'field_id': self.id,
            'field_name': self.field_name,
            'field_path': self.field_path,
            'timestamp': dump_datetime(self.timestamp),
            'notes': [item.serialize for item in self.notes.all()]
        }


class Notes(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    note_value = db.Column(db.String(1000), nullable=False)
    fn_exempt_field = db.Column(db.Integer, db.ForeignKey('exemptfields.id'))

    def __repr__(self):
        return '<Note %r>' % (self.note_value)

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'note_id': self.id,
            'note': self.note_value
        }


# -------------- Empty all tables -------------------- #
def empty_all_tables():
    for x in Companies.query.all():
        db.session.delete(x)
    for y in Documents.query.all():
        db.session.delete(y)
    for z in Exemptfields.query.all():
        db.session.delete(z)
    for a in Notes.query.all():
        db.session.delete(a)
    db.session.commit()


# -------------- Companies -------------------- #
def add_company(record):
    if isinstance(record, Companies):
        c = record
    else:
        c = Companies(company_name=record)
    try:
        db.session.add(c)
        db.session.commit()
    except sqlalchemy.exc.IntegrityError:
        print '%r already exists.  Returning entry from table' % record
        db.session.rollback()
        c = get_company(record)
    finally:
        return c


def remove_company(record):
    if isinstance(record, str):
        c = get_company(record)
    else:
        c = record
    try:
        db.session.delete(c)
        db.session.commit()
        return True
    except sqlalchemy.orm.exc.UnmappedInstanceError:
        return False


def get_company(name):
    return Companies.query.filter_by(company_name=name).first()


def get_company_by_id(record):
    return Companies.query.get(record)


def get_companies():
    return Companies.query.all()


# ------------ Documents -------------------- #
def add_document(name, number, key):
    if isinstance(key, Companies):
        d = Documents(document_name=name, document_number=number,
                      timestamp=datetime.datetime.utcnow(), comp=key)
    else:
        d = Documents(document_name=name, document_number=number,
                      timestamp=datetime.datetime.utcnow(), fn_company=key)
    try:
        db.session.add(d)
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
    else:
        return d


def remove_document(record):
    if isinstance(record, int):
        d = get_document_by_id(record)
        db.session.delete(d)
    else:
        db.session.delete(record)
    db.session.commit()


def get_document_by_id(key):
    return Documents.query.get(key)


def get_documents_by_company(record):
    if isinstance(record, Companies):
        return record.documents.all()
    else:
        return get_company(record).documents.all()


def get_documents():
    return Documents.query.all()


def get_documents_by_name(company, name):
    return get_company(company).documents.filter_by(
        document_name=name).all()


# -------------- Exemptions -------------------- #
def add_exemption(name, path, key):
    if isinstance(key, Documents):
        e = Exemptfields(field_name=name, field_path=path,
                         timestamp=datetime.datetime.utcnow(), doc=key)
    else:
        e = Exemptfields(field_name=name, field_path=path,
                         timestamp=datetime.datetime.utcnow(), fn_document=key)
    try:
        db.session.add(e)
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
    else:
        return e


def get_exemptfield_by_id(record):
    return Exemptfields.query.get(record)


def get_all_exemptfields():
    return Exemptfields.query.all()


def remove_exemption(record):
    if isinstance(record, int):
        db.session.delete(get_exemptfield_by_id(record))
    else:
        db.session.delete(record)
    db.session.commit()


# -------------- Notes -------------------- #
def add_note(info, key):
    if isinstance(key, Exemptfields):
        n = Notes(note_value=info, exempt=key)
    else:
        n = Notes(note_value=info, fn_exempt_field=key)
    try:
        db.session.add(n)
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
    else:
        return n


def get_note_by_id(record):
    return Notes.query.get(record)


def get_notes_by_exemption(record):
    if isinstance(record, Exemptfields):
        ns = record.notes.all()
    else:
        ns = get_exemptfield_by_id(record).notes.all()
    return ns
