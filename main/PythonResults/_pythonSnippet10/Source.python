def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%Y-%m-%d"), value.strftime("%H:%M:%S")]

class Foo(db.Model):
    # ... SQLAlchemy defs here..
    def __init__(self, ...):
       # self.foo = ...
       pass

    @property
    def serialize(self):
       """Return object data in easily serializeable format"""
       return {
           'id'         : self.id,
           'modified_at': dump_datetime(self.modified_at),
           # This is an example how to deal with Many2Many relations
           'many2many'  : self.serialize_many2many
       }
    @property
    def serialize_many2many(self):
       """
       Return object's relations in easily serializeable format.
       NB! Calls many2many's serialize property.
       """
       return [ item.serialize for item in self.many2many]