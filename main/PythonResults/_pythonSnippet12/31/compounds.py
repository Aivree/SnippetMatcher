from sqlalchemy.orm import synonym, relation, backref
from sqlalchemy.schema import Sequence, Column, Table, ForeignKey
from decimal import Decimal
import logging
from sqlalchemy.orm.util import class_mapper, object_mapper
from app.model import chemistry, metadata, DeclarativeBase, MolDecBase
from app.model import DBSession,metadata
from app.lib.innectus.utilities import tinyHash
import json
import datetime
from app.model.register import mergeMol
from app.model.chemistry import combineFragments
import uuid
import re
from sqlalchemy.types import Integer
from app.model.sar import Folder
from app.model.filemanagers import StudyFileManager, IdNotFoundException
from sqlalchemy.engine.base import RowProxy

log = logging.getLogger(__name__)

class InnectusEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, Decimal):
            return float(obj)
        if isinstance(obj, RowProxy):
            return dict(obj)
        try:
            return json.JSONEncoder.default(self, obj)
        except Exception:
            return str(obj)

_gUidLength = 24 



class _IAddLots(object):
    """
    'Interface' for molecules that will have lots associated with them.  Inherit from this class
    and from _rBase to create a molecule that will be registered in the database and will have
    lots.  Inherit only from _RegMolBase to get a molecule that will be registered in the database
    but will NOT have any lots (e.g., salts).
    """
    lots=None

    def getLotCount(self):
        return len(self.lots)
    def addLot(self,lot,jobId=None):
        lot.lot_number = self.getLotCount()+1
        if jobId is not None:
            lot.lot_reg_group_id = jobId
        self.lots.append(lot)
  


"""
Application molecule, lot, salt and lot salts class definitions 
"""

"""
Classes for proprietary corporate compounds
"""

class MolRegConfig(DeclarativeBase):
    
    __table__=metadata.tables['mol_reg_cfg']

    def createSeq(self):
#        The below Sqlalchemy sequence creation method is ignoring the start and increment
#        parameters when creating the sequence and thus the direct sql approach is used.
#        seq = Sequence(self.getSeqName(),start=self.mol_prefix_seq_start,increment=1)
#        seq.create(metadata.bind)
        DBSession().execute("CREATE SEQUENCE " + self.getSeqName() + " START WITH " + str(self.id_seq_start))
        DBSession().execute("GRANT SELECT ON " + self.getSeqName() + " TO backup") 
               
    def dropSeq(self):
        seq = Sequence(self.getSeqName())
        seq.drop(metadata.bind)

    def getRegId(self):
        try:
            format = "%0" + str(self.id_seq_max_length) + "hd"
            regId = self.id_prefix + "-" + format % (self.nextVal())
        except Exception,e:
            log.debug(e)
            raise e
        return regId
    
    def getSeqName(self):
        return "regseq_" + str(self.id_prefix).lower()
    
    def nextVal(self):
        seq = Sequence(self.getSeqName())
        return DBSession().execute(seq)
    
    @classmethod
    def byId(cls, id):
        return DBSession().query(cls).get(id)
    
    @classmethod
    def byPrefix(cls, prefix):
        return DBSession().query(cls).filter(cls.id_prefix == prefix).first()

list_compounds_table = Table('compound_list_compound', metadata,
    #Column('id', Integer, Sequence('appseq'), primary_key=True),
    Column('list_id', Integer, ForeignKey('compound_list.id',
        onupdate="CASCADE", ondelete="CASCADE"), primary_key=True),
    Column('mol_id', Integer, ForeignKey('mol.mol_id',
        onupdate="CASCADE", ondelete="CASCADE"), primary_key=True),
    useexisting=True)

class CompoundList(DeclarativeBase):
    __table__=metadata.tables['compound_list']
    compounds=relation("Mol", secondary=list_compounds_table, order_by="Mol.mol_reg_id", backref=backref("lists", order_by="CompoundList.name", lazy=False))
    createdBy=relation('User', primaryjoin="CompoundList.created_by_id==User.user_id")

    @classmethod
    def byName(cls, name):
        if name is None:
            return None
        return DBSession().query(cls).filter(cls.name==name).first()
    
    def __str__(self):
        return self.name

class Mol(_IAddLots, MolDecBase):
    
    __table__=metadata.tables['mol']
    lots=relation('Lot', backref='mol')
    
    def setAltId(self,value):
        try:
            regConfig = DBSession().query(MolRegConfig).get(value)
            self.mol_reg_id = regConfig.getRegId()
        except Exception,e:
            log.exception(e)
            raise Exception("Configure your registry id prefix first using the 'Configure' link.")

    @classmethod
    def byRegId(cls, regId):
        return DBSession().query(cls).filter(cls.mol_reg_id == regId).first()
    
    @classmethod
    def byId(cls, id):
        if id is None:
            return None
        return DBSession().query(cls).get(id)

    @property
    def compoundListNames(self):
        l = [x.name for x in self.lists if x.name is not None]
        return ', '.join(l)
    
    @compoundListNames.setter
    def compoundListNames(self, val):
        pass

    """
    Proprietary corporate compound
    """
    pass

class Lot(DeclarativeBase):
    """
    A lot associated with a proprietary corporate compound
    """  
    __table__=metadata.tables['lot']
    
    lotSalts=relation('LotSalt', backref='lot', lazy=False)
    vials = relation('Vial',backref='lot')
    
    def getVialCount(self):
        #DBSession().query()
        return DBSession().query(Vial).filter(Vial.vial_lot_id == self.lot_id).count()
    
    @property
    def lot_mw(self):
        mw = float(self.mol.mol_mw)
        salt_mw = 0.0
        for lotSalt in self.lotSalts:
            salt_mw += (float(lotSalt.salt.mol_mw) * float(lotSalt.lot_salt_equiv))
        mw += salt_mw
        return mw
        
    @lot_mw.setter
    def lot_mw(self,value): #@DuplicatedSignature
        pass
        
    @property
    def lot_mf(self):
        mf = str(self.mol.mol_mf)
        for lotSalt in self.lotSalts:
            equiv = ''
            if lotSalt.lot_salt_equiv != 1:
                equiv = str(lotSalt.lot_salt_equiv)
            mf += "." + equiv + lotSalt.salt.mol_mf 
        return mf
    
    @lot_mf.setter
    def lot_mf(self,value): #@DuplicatedSignature
        pass
    
    @property
    def lot_salts(self):
        salts = []
        salt = {}
        for lotSalt in self.lotSalts:
            salt = lotSalt.salt.getSaltInfo()
            salt['equivalents']=lotSalt.lot_salt_equiv
            salts.append(salt)
        return salts

    @lot_salts.setter
    def lot_salts(self, value):  #@DuplicatedSignature
        for lotSalt in self.lotSalts:
            DBSession().delete(lotSalt)
        saltListInfo = []
        if value is None:
            value=[]
        for saltInfo in value:
            salt = DBSession().query(SaltMol).get(saltInfo['id'])
            DBSession().expunge(salt)
            lotSalt = LotSalt(salt,saltInfo['equivalents'])
            lotSalt.lot = self

    @classmethod
    def byLotnumber(cls, regId, lotNumber):
        if regId is None or lotNumber is None:
            return None
        return DBSession().query(Lot).join(Mol).filter(Mol.mol_reg_id == regId).filter(Lot.lot_number == lotNumber).first()
        
class LotSalt(DeclarativeBase):
    """
    Salt associated with the lot of a proprietary corporate compound
    """
    
    __table__=metadata.tables['lot_salt']
    salt=relation('SaltMol', backref='lotsalts')
    
    def __init__(self,salt=None,salt_equiv=None):
        if salt is not None:
            self.lot_salt_equiv = salt_equiv
            self.salt = salt
        
class ReagentLotLocation(DeclarativeBase):
    __table__=metadata.tables['reagent_lot_loc']
    pass

class ReagentMol(_IAddLots, MolDecBase):
    """
    Reagent molecule
    """
    #__tablename__='reagent_mol'
    #_table_args_= ( {'autoload' : True}, )
    __table__=metadata.tables['reagent_mol']
    lots=relation('ReagentLot', backref='mol')
    names=relation('ReagentName', backref='mol', cascade='all,delete-orphan', lazy=False)

    @property
    def mol_synonyms(self):
        '''
        Getter for synonyms for a molecule

        @return: list of dictionaries with keys mol_id, items.  items is an array of dicts with keys
        id and value
        @rtype: list
        '''
        items = { 'dataId' : self.mol_id, 'items' : [] }
        for n in self.names:
            items['items'].append(dict(id=n.id, value=n.name))
        return json.dumps(items)
    
    @mol_synonyms.setter
    def mol_synonyms(self, val):
        '''
        dummy setter--this property is not writeable.  Updates are handled in the controller method since we can't guarantee when this
        setter will be called, and we need the mol and names objects in the session so they have correct IDs.
        '''
        pass

class ReagentName(DeclarativeBase):
    __table__ = metadata.tables['reagent_mol_name']
    #__tablename__='reagent_mol_name'
    #__table_args__ = { 'useexisting' : True }
    #TODO: the allowed values for the cascade option are different in 0.5.8 from later SA versions
    lots = relation("ReagentLot", backref = 'lot_name', lazy=False, cascade='save-update,merge,expunge,refresh-expire', passive_deletes='all')    
    #mol_id = Column(Integer, ForeignKey('reagent_mol.mol_id'), primary_key=True)

    @classmethod
    def byName(cls, name):
        if name is None:
            return None
        return DBSession().query(cls).filter(cls.name==name).first()
    
    def __str__(self):
        return self.name

class ReagentLot(DeclarativeBase):
    """
    A lot associated with a reagent
    """
    #__tablename__='reagent_lot'
    #_table_args_= ( {'autoload' : True}, )
    __table__=metadata.tables['reagent_lot']
    loc=relation('ReagentLotLocation', backref='lots')
    vendor=relation('ReagentVendor', backref='lots')
    #lot_name = relation("ReagentName", backref = 'lots', passive_deletes=True)
    
    @classmethod
    def byBarcode(cls, barcode):
        '''
        Return a lot by the barcode
        '''
        return DBSession().query(cls).filter(cls.lot_barcode == barcode).first()
    
    @property
    def lot_name_choices(self):
        '''
        Getter for the lot name, a list of possible lot names & the one actually selected

        @return: list of dictionaries with keys mol_id, lot_id, id and value
        @rtype: list
        '''
        items = { 'dataId' : self.mol.mol_id, 'items' : [] }
        for n in self.mol.names:
            selected = n.id == self.lot_name_id
            items['items'].append(dict(id=n.id, value=n.name, selected=selected))
        return json.dumps(items)
    
    @lot_name_choices.setter
    def lot_name_choices(self, val):
        '''
        dummy setter--this property is not writeable.  Updates are handled in the controller method since we can't guarantee when this
        setter will be called, and we need the lot, mol and names objects in the session so they have correct IDs.
        '''
        pass

class ReagentVendor(DeclarativeBase):
    __table__=metadata.tables['reagent_vendor']


class ReagentBackload(DeclarativeBase):
    
    molid = None
    fingerprint = None
    __table__=metadata.tables['reagent_lot_backload']
    loc=relation('ReagentLotLocation')



class SaltMol(MolDecBase):
    __table__=metadata.tables['salt_mol']
    def getSaltInfo(self):
        return dict(id=self.mol_id,
                    name=self.mol_name,
                    molecularWeight=self.mol_mw,
                    molecularFormula=self.mol_mf)
    
    @classmethod
    def getSaltList(cls):
        #grab all salts and then send down the full list with specific values 
        salts = DBSession().query(cls).all()
        saltList = []
        for salt in salts:
            saltList.append(salt.getSaltInfo())
        return saltList





class Request(DeclarativeBase):
    __table__=metadata.tables['request']
    requester = relation('User')
      
    def hasOpenItems(self):
        retVal = False

        for item in self.items:
            if item.request_item_dispense_date is None:
                retVal = True
                break  
        return retVal
    
    def hasClosedItems(self):
        retVal = False
        for item in self.items:
            if item.request_item_dispense_date is not None:
                retVal = True
                break  
        return retVal
    
    @property
    def num_items(self):
        return len(self.items)
    @num_items.setter
    def num_items(self,value): #@DuplicatedSignature
        pass
        
    @property
    def requested_by(self):
        return self.requester.display_name
    @requested_by.setter
    def requested_by(self,value): #@DuplicatedSignature
        pass



class RequestItem(DeclarativeBase):
    __table__=metadata.tables['request_item']
    mol = relation('Mol',backref='requestItems')
    request = relation('Request',backref = 'items')
    to_vial = relation('Vial', primaryjoin="RequestItem.request_item_dispense_to_vial_id==Vial.vial_id", backref="dispenseItem")

    @property
    def amount(self):
        dispAmt = calcDispValue(self.request_item_dispense_amt, self.request_item_dispense_amt_units)
        retVal = dict(amount=dispAmt, amountUnits=self.request_item_dispense_amt_units)
        return retVal
    
    @amount.setter
    def amount(self, newAmt): #@DuplicatedSignature
        if newAmt:
            amount = newAmt.get('request_item_dispense_amt', None)
            amountUnits = newAmt.get('request_item_dispense_amt_units', None)
            self.request_item_dispense_amt = calcValueFromDisp(amount, amountUnits)
            self.request_item_dispense_amt_units = amountUnits
    
class Vial(DeclarativeBase):
    __table__=metadata.tables['vial']
    rack = relation('Rack',backref='vials')
    well = relation('RackWell', lazy=False)
    type = relation('VialType', lazy=False)
    parent = relation('Vial', remote_side="Vial.vial_id")

    @classmethod
    def byBarcode(cls, barcode):
        if barcode:
            return DBSession().query(cls).filter(cls.vial_barcode == barcode).first()
        else:
            return None

    @classmethod
    def byRackAndWellName(cls, rack, wellName):
        if rack:
            if wellName:
                return DBSession().query(cls).join(RackWell).filter(cls.rack == rack).filter(RackWell.rack_well_name == wellName).first()
        return None
    
    @classmethod
    def byRackBarcodeAndWellName(cls, rackBarcode, wellName):
        if rackBarcode:
            if wellName:
                return DBSession().query(cls).join(Rack).filter(cls.vial_rack_id == Rack.rack_id).filter(Rack.rack_barcode == rackBarcode).join(RackWell).filter(cls.vial_rack_well_id == RackWell.rack_well_id).filter(RackWell.rack_well_name == wellName).first()
        return None

    @property
    def wellName(self):
        return self.well.rack_well_name
    
    @wellName.setter
    def wellName(self, wellName):
        if wellName:
            self.well = RackWell.byTypeAndName(self.rack.type, wellName)
        
    @property
    def amount(self):
        dispAmt = calcDispValue(self.vial_amt, self.vial_amt_units)
        dispConc = calcDispValue(self.vial_conc, self.vial_conc_units)
        retVal = dict(amount=dispAmt, amountUnits=self.vial_amt_units, conc=dispConc, concUnits=self.vial_conc_units)
        return retVal
    
    @amount.setter
    def amount(self, newAmt): #@DuplicatedSignature
        if newAmt:
            amount = newAmt.get('amount', None)
            amountUnits = newAmt.get('amountUnits', None)
            conc = newAmt.get('conc', None)
            concUnits = newAmt.get('concUnits', None)
            self.vial_amt = calcValueFromDisp(amount, amountUnits)
            self.vial_amt_units = amountUnits
            self.vial_conc = calcValueFromDisp(conc, concUnits)
            self.vial_conc_units = concUnits
    
    @property
    def disp_conc(self):
        return calcDispValue(self.vial_conc, self.vial_conc_units)
    
    @disp_conc.setter
    def disp_conc(self, val): #@DuplicatedSignature dummy setter to make property read only
        pass

    @property
    def contents(self):
        if self.vial_amt_units in ('mg', 'kg', 'g'):
            return 'solid'
        else:
            return 'liquid'
        
    def dispense(self, amount):
        if amount is not None:
            if self.vial_amt is not None:
                self.vial_amt = self.vial_amt - amount
                if self.vial_amt < 0:
                    self.vial_amt = 0
    
    @property
    def rackwell(self):
        if self.well:
            retVal=dict()
            r=self.well.rack_type_id
            retVal['master']=r
            retVal['detail']=self.well.rack_well_id
            return retVal
        return None
    
    @rackwell.setter
    def rackwell(self, val):
        if val is None:
            self.rack_well_id = None
        else:
            self.well = RackWell.byTypeIdAndWellId(val['master'], val['detail'])
            if self.rack:
                self.rack.rack_type_id = val['master']

class VialType(DeclarativeBase):
    __table__=metadata.tables['vial_type']


class Rack(DeclarativeBase):
    __table__=metadata.tables['rack']
    type=relation('RackType', lazy=False)
    location=relation('RackLocation', lazy=False)

    @classmethod
    def byBarcode(cls, barcode):
        if barcode:
            return DBSession().query(cls).filter(cls.rack_barcode == barcode).first()
        else:
            return None
class RackType(DeclarativeBase):
    __table__=metadata.tables['rack_type']

    @classmethod
    def byName(cls, name):
        if name:
            return DBSession().query(cls).filter(cls.rack_type_name == name).first()
        else:
            return None

class RackWell(DeclarativeBase):
    __table__=metadata.tables['rack_well']
    type=relation('RackType', backref='wells')
    @classmethod
    def byTypeAndName(cls, type, name):
        if type:
            if name:
                return DBSession().query(cls).filter(cls.type == type).filter(cls.rack_well_name == name).first()
        return None
    
    @classmethod
    def byTypeIdAndWellId(cls, typeId, wellId):
        if typeId:
            if wellId:
                return DBSession().query(cls).filter(cls.rack_type_id == typeId).filter(cls.rack_well_id == wellId).first()
        return None

class RackLocation(DeclarativeBase):
    __table__=metadata.tables['rack_location']

    @classmethod
    def byName(cls, name):
        if name and name != 'None':
            for l in DBSession().query(cls):
                if l.locationName == name:
                    return l
        return None

    @property
    def locationName(self):
        nameChunks=[]
        for name, prop in dict(Bldg='building', Room='room', Cabinet='cabinet', Shelf='shelf', Coord='coordinate').iteritems():
            val = getattr(self, prop)
            if val:
                nameChunks.append('%s:%s' % (name, val))
        name='None'
        if nameChunks:
            name=','.join(nameChunks)
        return name            

    @classmethod
    def byBarcode(cls, barcode):
        if barcode:
            return DBSession().query(cls).filter(cls.barcode == barcode).first()
        else:
            return None

class File(DeclarativeBase):
    __table__=metadata.tables['file']
    rows = relation('FileRow',backref = 'file',cascade='all,delete-orphan',order_by='FileRow.mol_id')




import cPickle

class FileRow(DeclarativeBase):
    
    __table__= metadata.tables['file_row']
    mol_struct = synonym('_mol_struct', map_column=True)
   
    def __init__(self, columns=None, file_row=dict()):
        if columns:
            for c in columns:
                if c.id not in file_row:
                    file_row[c.id]=None
        self.pickle(file_row)

    def unpickle(self):
        return cPickle.loads(str(self.file_row))

    def pickle(self, file_row):
        self.file_row=cPickle.dumps(file_row)
        
    @property
    def molid(self):
        '''
        Getter for the molid, a property combining multiple data about the mol into one record.

        @return: dict containing keys molId, uid and dbData for the pk (mol_id), uid (hash of mol_struct)
        and whether this is data with a database source (True in this case)
        @rtype: dict
        '''
        molId=self.mol_id
        molStr=self.mol_struct
        uid = None
        if molStr is not None and molStr != '':
            uid = tinyHash(molStr, _gUidLength)
        return {'molId':str(molId),'uid':uid, 'dbData':True,'chirality':self.chirality}

    @molid.setter
    def molid(self, value): #@DuplicatedSignature
        '''
        Dummy, molid is read-only
        '''
        pass

class RxnMol(_IAddLots, MolDecBase):
    __table__=metadata.tables['rxn_mol']

def updateRxnProtocolsAndNamesFromMol(rxnMol):
    '''
    Given a changed rxn molecule, go through and update protocols that depend on that rxn molecule's properties,
    and change the reaction title as well
    '''
    for m in rxnMol.reagents + rxnMol.products:
        updateRxnProtocolFromComponent(m)
    
    rxns=set()
    for p in rxnMol.products:
        rxns.add(p.rxn)
    for r in rxns:
        newName = r.makeName()
        if r.rxn_name != newName:
            r.rxn_name = newName

def deleteMolFromRxnProtocols(rxnMol):
    '''
    Given a molecule to be deleted, go through and remove from protocols containing it.
    This is here for completeness, but most likely we do not want a molecule deleted unless
    no reactions depended on it.
    '''
    for m in rxnMol.reagents + rxnMol.products:
        deleteCompFromRxnProtocol(m)

from BeautifulSoup import BeautifulSoup
def deleteCompFromRxnProtocol(m):
    '''
    Given a deleted reagent or product, update the protocol
    '''
    rxn = m.rxn
    className = re.compile('stoich%s' % m.uuid)
    if rxn.rxn_protocol:
        soup = BeautifulSoup(rxn.rxn_protocol)
        spans = soup.findAll(attrs={'class':className})
        for span in spans:
            span.extract()
        if spans:
            rxn.rxn_protocol = str(soup)

def updateRxnProtocolFromComponent(m):
    '''
    Given a changed reagent or product, update the protocol
    '''
    rxn = m.rxn
    className = re.compile('stoich%s' % m.uuid)
    if rxn.rxn_protocol:
        soup = BeautifulSoup(rxn.rxn_protocol)
        spans = soup.findAll(attrs={'class':className})
        for span in spans:
            span.contents[0].replaceWith(renderComponent(m))
        if spans:
            rxn.rxn_protocol = str(soup)

def renderComponent(m):
        r = ''
        if m.purity is not None and m.purity < 99.99:
            r = r + '%0.1f%% ' % m.purity

        if m.mol.mol_name:
            r = r + m.mol.mol_name
        else:
            r = r + 'Unnamed chemical'
        r = r + ' ('
        if m.mol.state == 'solid' and m.conc > 0:
            r = r + '%0.1f %s, ' % (m.disp_conc, m.conc_units)
        if m.disp_amount > 0:
            dec=0
            if m.disp_amount_units == 'g':
                dec=3
            elif m.disp_amount_units == 'mg':
                dec = 0
            elif m.disp_amount_units == 'kg':
                dec = 5
            elif m.disp_amount_units == 'ml':
                dec = 3
            elif m.disp_amount_units == 'L':
                dec = 3
            elif m.disp_amount_units == 'ul':
                dec = 0
            fmt = '%%0.%df %%s' % dec
            r = r + fmt % (m.disp_amount, m.disp_amount_units)
        else:
            r = r + 'unspecified amount'
        if m.amount_mol > 0:
            mmol=m.amount_mol * 1000;
            r = r + ', %0.1f mmol' % mmol
        if m.equivalents > 0:
            r = r + ', %0.1f equiv.' % m.equivalents
        r = r + ')'
        return r

class Notebook(DeclarativeBase):  

    rxns =  relation('Rxn', backref = 'notebook', passive_deletes=True)
    __table__ = metadata.tables['eln_notebook']
    
    #column values will be filled by reflection
    nb_id = None
    nb_name = None
       
    def setName(self, name):
        self.nb_name = name
        
    def addRxn(self, rxn):
        self.rxns.append(rxn)


class Rxn(DeclarativeBase):
    
    reagents =  relation('RxnReagent',backref = 'rxn')
    products = relation('RxnProduct',backref = 'rxn')
    program = relation('Program')
    org = relation('Org')
    owner = relation('User', primaryjoin="Rxn.rxn_owner_id==User.user_id")
    committer = relation('User', primaryjoin="Rxn.completed_by_id==User.user_id")
    witness = relation('User', primaryjoin="Rxn.witness_id==User.user_id")
    #image render size for views
    _imageRenderSize = 200;

    #columns
    rxn_id = None
    rxn_name = None
    rxn_protocol = None
    
    #relation to Page
    
    __table__=metadata.tables['rxn']

    @classmethod
    def byId(cls, rxnId):
        return DBSession().query(cls).get(int(rxnId))
    
    def makeName(self):
        names = [];
        for p in self.products:
            n = p.mol.mol_name;
            so = p.sort_order
            if not so:
                so = 0
            if not n:
                n = 'unnamed chemical'
            names.append( (n, so ));
        newname = 'Unnamed reaction';
        andStr = ''
        if len(names) > 0:
            names.sort(key=lambda name : name[1])
            lastn = ''
            if len(names) > 1:
                andStr = ' and ';
                lastn = names.pop()[0]
            newname = 'Reaction to produce ';
            comma = '';
            for n in names:
                newname = newname + comma + n[0];
                comma = ', '
            newname = newname + andStr + lastn;
        return newname

    @classmethod
    def byProductId(cls, productId):
        return DBSession().query(cls).join(cls.products).filter(RxnProduct.product_id==productId).first()
    
    def getName(self):
        return self.rxn_name
    
    def setName(self, name):
        self.rxn_name = name
        
    def getProgram(self):
        if self.program is not None:
            return self.program.program_name
        else:
            return ''
        
    def getOrg(self):
        if self.org is not None:
            return self.org.org_name
        else:
            return ''
        
    def getOwner(self):
        if self.owner is not None:
            return self.owner.user_name
        else:
            return ''
        
    def getNotebook(self):
        if self.notebook is not None:
            return self.notebook.nb_name
        else:
            return ''
        
    def getInfoList(self):
        return [['Id',self.rxn_id],
                ['Program',self.getProgram()],
                ['Org',self.getOrg()],
                ['Owner',self.getOwner()],
                ['Notebook',self.getNotebook()],
                ['Page', self.rxn_notebook_page],
                ['Created', datetime.datetime.strftime(self.date_created, '%b %d %Y %X')]]
    
  
    def getId(self):
        return self.rxn_id
    
    @property
    def id(self):
        return self.rxn_id
    
    @property
    def rxn_scheme(self):
        reagentArray = [] 
        productArray = []
        for reagent in self.reagents:
            if reagent.is_displayed == 'Y':
                reagentArray.append(reagent.mol.getMolIdDict())
        for product in self.products:
            if product.is_displayed == 'Y':
                productArray.append(product.mol.getMolIdDict())
        return dict(reagents=reagentArray,products=productArray)
    
    @rxn_scheme.setter
    def rxn_scheme(self, value): #@DuplicatedSignature
        pass
    
    def setImageRenderSize(self, size):
        self._imageRenderSize = size
    
    def getReagentImageUrls(self):
        return self._getImageUrls(self.reagents)
    
    def getProductImageUrls(self):
        return self._getImageUrls(self.products)
        
    def getDisplayUrl(self):
        return "getPreview?rxnId=%s" % self.rxn_id
    
    def getEditUrl(self):
        return "index?rxnId=%s" % self.rxn_id
        
    def _getImageUrls(self,compounds):
        retVal = []
        for compound in compounds:
            if compound.is_displayed == 'Y':
                retVal.append(compound.mol.getMolImageUrl(self._imageRenderSize))
        return retVal
    
    def signRxn(self, user):
        '''
        Sign a reaction electronically
        '''
        # HACK: Out of the box Python datetime + SQLA don't deal with Postgres TIMESTAMP WITH TIME ZONE
        # correctly (a fix might be pytz).  If we are updating and simply assign a new datetime.datetime.now()
        # to the date_completed attribute we'll get a 'can't compare offset-naive and offset-aware datetimes'
        # exception.  For now just set date_completed to None in case we are doing an update to the time.
        # See also: http://blog.abourget.net/2009/4/27/sqlalchemy-and-timezone-support
        self.date_witnessed = None
        DBSession().flush()

        self.witness=user
        self.date_witnessed = datetime.datetime.now()

    def commit(self, user, register, prefixId):
        '''
        Save the reaction to the database as completed
        '''
        # HACK: Out of the box Python datetime + SQLA don't deal with Postgres TIMESTAMP WITH TIME ZONE
        # correctly (a fix might be pytz).  If we are updating and simply assign a new datetime.datetime.now()
        # to the date_completed attribute we'll get a 'can't compare offset-naive and offset-aware datetimes'
        # exception.  For now just set date_completed to None in case we are doing an update to the time.
        # See also: http://blog.abourget.net/2009/4/27/sqlalchemy-and-timezone-support
        self.date_completed=None
        DBSession().flush()
        
        # Now do the real assignment
        self.date_completed=datetime.datetime.now()
        self.completed_by_id=user.user_id
        if register:
            for p in self.products:
                mol=Mol()
                mol.copyFromMol(p.mol)
                # TODO: Unify this with the inventory module's registration as a separate
                # model module 'cinv'.
                mol=mergeMol(mol)
                lot=Lot()
                lot.lot_submitter_id = user.user_id
                lot.lot_program_id = self.rxn_program_id
                mol.addLot(lot, 0)
                lot.lot_purity = p.purity
                lot.lot_source_org_id = self.rxn_org_id
                lot.lot_notebook = self.notebook.nb_name + ', p. ' + self.rxn_notebook_page
                # TODO:  Barcodes should be either None or a non-empty string, but we're not handling
                # that correctly.  Should be done elsewhere but we'll do it here for the moment.
                if p.barcode == '':
                    p.barcode = None
                vial = Vial.byBarcode(p.barcode)
                if vial is None:
                    vial=Vial()
                    vial.vial_barcode = p.barcode
                vial.vial_init_amt = p.submitted_amount
                vial.vial_amt = p.submitted_amount
                if vial.vial_amt > 0:
                    vial.vial_amt_units = 'g'
                vial.vial_conc = p.conc
                if vial.vial_conc > 0:
                    vial.vial_conc_units = 'ml'
                vial.vial_lot_aliquot_no=lot.getVialCount()+1
                lot.vials.append(vial)
                lot.elnProduct = p
                # Flush out any errors before we try assigning the reg id.  That way we don't cause
                # gaps in sequence on a commit.
                DBSession().flush()
                if mol.mol_reg_id is None:
                    mol.setAltId(prefixId)
                
    def clone(self, owner=None):
        crxn = saClone(self)
        newRxn = DBSession().merge(crxn)
        newRxn.rxn_program_id = None
        newRxn.rxn_org_id = None
        newRxn.completed_by_id = None
        newRxn.date_completed = None
        newRxn.date_created = None
        newRxn.rxn_notebook_id = None
        newRxn.rxn_notebook_page = None
        newRxn.date_witnessed = None
        newRxn.witness_id = None
        newRxn.date_created = datetime.datetime.now()
        for reagent in self.reagents:
            newr = saClone(reagent)
            # Update stoich-to-protocol links
            newr.uuid = uuid.uuid4().hex
            newRxn.reagents.append(newr)
            if newRxn.rxn_protocol:
                newRxn.rxn_protocol = newRxn.rxn_protocol.replace(reagent.uuid, newr.uuid)
        for product in self.products:
            newp = saClone(product)
            # Update stoich-to-protocol links
            newp.uuid = uuid.uuid4().hex
            # Clear out things which can't be known until reaction is complete
            newp.amount = None
            newp.submitted_amount = None
            newp.pct_yield = None
            newp.purity = None
            newp.barcode = None
            newRxn.products.append(newp)
            if newRxn.rxn_protocol:
                newRxn.rxn_protocol = newRxn.rxn_protocol.replace(product.uuid, newp.uuid)
        newRxn.owner = owner
        return newRxn

def saClone(o):
    '''
    "Clone" a SQLAlchemy object.  This function makes a copy of all basic SQLAlchemy attributes of an object.  It does
    not include relation properties or backrefs, only the column attributes.  Use this function to make duplicates of
    objects that will be copied but will have different primary keys.  The primary key(s) of the copied object will be
    set to None.
    
    @param o:  object to clone
    @type o: SQLAlchemy object
    @return: cloned object
    @rtype: SQLAlchemy object 
    '''
    log.debug('class to clone:' + str(o))
    r=o.__class__()
    pk_keys = set([c.key for c in object_mapper(o).primary_key])
    for prop in [p for p in object_mapper(o).iterate_properties]:
        # Exclude any relation properties
        if not hasattr(prop, 'uselist'):
            key = prop.key
            if key not in pk_keys:
                setattr(r, prop.key, getattr(o, prop.key, None))
    return r


_unitConv = { 'uM' : Decimal("1.0e+6"),
              'mM' : Decimal("1.0e+3"),
              'M'  : Decimal(1),
              'ul' : Decimal("1.0e+3"),
              'ml' : Decimal(1),
              'L'  : Decimal("1.0e-3"),
              'ug' : Decimal('1.0e+6'),
              'mg' : Decimal('1.0e+3'),
              'g'  : Decimal('1.0'),
              'kg' : Decimal('1.0e-3')}

def calcDispValue(value, units):
    conv = _unitConv.get(units, None)
    dispValue = None
    if conv and value:
        dispValue = Decimal(str(value)) * conv
    return dispValue

def calcValueFromDisp(dispValue, units):
    conv = _unitConv.get(units, None)
    value = None
    if conv and dispValue:
        value = Decimal(str(dispValue)) / conv
    return value

class RxnReagent(DeclarativeBase):
    
    __table__=metadata.tables['rxn_reagent']
    mol =  relation('RxnMol',backref = 'reagents')
    lotSalts = relation('RxnReagentLotSalt',backref = 'lot')
  
    @property
    def lot_salts(self):
        salts = []
        salt = {}
        for lotSalt in self.lotSalts:
            salt = lotSalt.salt.getSaltInfo()
            salt['equivalents']=lotSalt.lot_salt_equiv
            salts.append(salt)
        return salts

    @lot_salts.setter
    def lot_salts(self, value):  #@DuplicatedSignature
        for lotSalt in self.lotSalts:
            DBSession().delete(lotSalt)
        if value is None:
            value=[]
        for saltInfo in value:
            salt = DBSession().query(SaltMol).get(saltInfo['id'])
            DBSession().expunge(salt)
            lotSalt = RxnReagentLotSalt(salt,saltInfo['equivalents'])
            lotSalt.lot = self

    @property
    def disp_amount(self):
        return calcDispValue(self.amount, self.disp_amount_units)
    
    @disp_amount.setter
    def disp_amount(self, val): #@DuplicatedSignature dummy setter to make property read only
        pass
    
    @property
    def disp_conc(self):
        return calcDispValue(self.conc, self.conc_units)
    
    @disp_conc.setter
    def disp_conc(self, val): #@DuplicatedSignature dummy setter to make property read only
        pass

    @property
    def lot_mw(self):
        mw = None
        if self.mol.mol_mw:
            mw = float(self.mol.mol_mw)
            salt_mw = 0.0
            for lotSalt in self.lotSalts:
                salt_mw += (float(lotSalt.salt.mol_mw) * float(lotSalt.lot_salt_equiv))
            mw += salt_mw
        return mw
        
    @lot_mw.setter
    def lot_mw(self,value): #@DuplicatedSignature
        pass
        
    @property
    def lot_mf(self):
        mf = str(self.mol.mol_mf)
        for lotSalt in self.lotSalts:
            equiv = ''
            if lotSalt.lot_salt_equiv != 1:
                equiv = str(lotSalt.lot_salt_equiv)
            mf += "." + equiv + lotSalt.salt.mol_mf 
        return mf
    
    @lot_mf.setter
    def lot_mf(self,value): #@DuplicatedSignature
        pass

class RxnProduct(DeclarativeBase):
    __table__=metadata.tables['rxn_product']
    mol =  relation('RxnMol',backref = 'products')
    regLot = relation('Lot', backref = 'elnProduct')
    lotSalts = relation('RxnProductLotSalt',backref = 'lot')

    @property
    def lot_salts(self):
        salts = []
        salt = {}
        for lotSalt in self.lotSalts:
            salt = lotSalt.salt.getSaltInfo()
            salt['equivalents']=lotSalt.lot_salt_equiv
            salts.append(salt)
        return salts

    @lot_salts.setter
    def lot_salts(self, value):  #@DuplicatedSignature
        for lotSalt in self.lotSalts:
            DBSession().delete(lotSalt)
        if value is None:
            value=[]
        for saltInfo in value:
            salt = DBSession().query(SaltMol).get(saltInfo['id'])
            DBSession().expunge(salt)
            lotSalt = RxnProductLotSalt(salt,saltInfo['equivalents'])
            lotSalt.lot = self
    
    @property
    def disp_amount(self):
        return calcDispValue(self.amount, self.disp_amount_units)
    
    @disp_amount.setter
    def disp_amount(self, val): #@DuplicatedSignature dummy setter to make property read only
        pass
    
    @property
    def disp_conc(self):
        return calcDispValue(self.conc, self.conc_units)
    
    @disp_conc.setter
    def disp_conc(self, val): #@DuplicatedSignature dummy setter to make property read only
        pass

    @property
    def lot_mw(self):
        mw = None
        if self.mol.mol_mw:
            mw = float(self.mol.mol_mw)
            salt_mw = 0.0
            for lotSalt in self.lotSalts:
                salt_mw += (float(lotSalt.salt.mol_mw) * float(lotSalt.lot_salt_equiv))
            mw += salt_mw
        return mw
        
    @lot_mw.setter
    def lot_mw(self,value): #@DuplicatedSignature
        pass
        
    @property
    def lot_mf(self):
        mf = str(self.mol.mol_mf)
        for lotSalt in self.lotSalts:
            equiv = ''
            if lotSalt.lot_salt_equiv != 1:
                equiv = str(lotSalt.lot_salt_equiv)
            mf += "." + equiv + lotSalt.salt.mol_mf 
        return mf
    
    @lot_mf.setter
    def lot_mf(self,value): #@DuplicatedSignature
        pass

class RxnReagentLotSalt(DeclarativeBase):
    """
    Salt associated with the lot of a reagent
    """
    __table__=metadata.tables['rxn_reagent_salt']
    salt=relation('SaltMol')
    
    def __init__(self,salt=None,salt_equiv=None):
        self.lot_salt_equiv = salt_equiv
        self.salt = salt


class RxnProductLotSalt(DeclarativeBase):
    """
    Salt associated with the lot of a reagent
    """
    __table__=metadata.tables['rxn_product_salt']
    salt=relation('SaltMol')
    
    def __init__(self,salt=None,salt_equiv=None):
        self.lot_salt_equiv = salt_equiv
        self.salt = salt




class Org(DeclarativeBase):
    __table__=metadata.tables['org']
    
    @classmethod
    def byName(cls, name):
        if name is None:
            return None
        else:
            return DBSession().query(cls).filter(cls.org_name == name).first()

class Program(DeclarativeBase):
    __table__=metadata.tables['program']

    @classmethod
    def byName(cls, name):
        if name is None:
            return None
        else:
            return DBSession().query(cls).filter(cls.program_name == name).first()

#To replace File Class -- this is more descriptive and less inclined to have a keyword collision
class FileEditor(DeclarativeBase):
    __table__=metadata.tables['file_editor']
    
study_compounds_table = Table('study_compound', metadata,
    Column('study_id', Integer, ForeignKey('study.id',
        onupdate="CASCADE", ondelete="CASCADE")),
    Column('mol_id', Integer, ForeignKey('mol.mol_id',
        onupdate="CASCADE", ondelete="CASCADE")),
    useexisting=True)

class Study(DeclarativeBase):
    __table__=metadata.tables['study']
    # Using passive_deletes=True to prevent sqlalchemy from setting study_type_id to NULL if the
    # parent study type is deleted. 
    createdBy=relation('User', primaryjoin="Study.created_by_id==User.user_id")
    compounds=relation("Mol", secondary=study_compounds_table, order_by="Mol.mol_reg_id")
    closedBy = relation('User', primaryjoin="Study.closed_by_id==User.user_id")
    program=relation('Program')
    performedBy=relation('Org', primaryjoin="Study.org_id==Org.org_id")
    performedFor=relation('Org', primaryjoin="Study.performed_for_id==Org.org_id")

    #HACK: This is a workaround so that we can show
    # the list of compounds in the study search grid
    @property
    def compoundList(self):
        l = [m.mol_reg_id for m in self.compounds if m.mol_reg_id is not None]
        return ', '.join(l)
    
    @compoundList.setter
    def compoundList(self, val):
        pass

    @classmethod
    def byId(cls, id):
        if id is None:
            return None
        return DBSession().query(cls).get(id)

    def close(self, user):
        '''
        Save the study to the database as closed
        '''
        # HACK: Out of the box Python datetime + SQLA don't deal with Postgres TIMESTAMP WITH TIME ZONE
        # correctly (a fix might be pytz).  If we are updating and simply assign a new datetime.datetime.now()
        # to the date_completed attribute we'll get a 'can't compare offset-naive and offset-aware datetimes'
        # exception.  For now just set date_closed to None in case we are doing an update to the time.
        # See also: http://blog.abourget.net/2009/4/27/sqlalchemy-and-timezone-support
        self.date_closed=None
        DBSession().flush()
        
        # Now do the real assignment
        self.date_closed=datetime.datetime.now()
        self.closed_by_id=user.user_id
        self.closed = 'Y'

    def open(self):
        '''
        Mark a study as open
        '''
        self.date_closed = None
        self.closed_by_id = None
        self.closed = 'N'
    
    @property
    def files(self):
        '''
        Get the list of all files for this study
        '''
        list=[]
        for f in self.folders:
            list.extend(self.folderFiles(f))
        return list

    def folderFiles(self, folder):
        '''
        Given a folder find the files for that folder
        '''
        files = []
        try:
            for file in StudyFileManager.getFiles(folder.folder_id):
                files.append(file)
        except IdNotFoundException:
            pass
        return files

    @property
    def folders(self):
        '''
        Iterate through the list of folders for this study
        '''
        folders = []
        root = Folder.getRoot(self.uuid)
        folders.append(root)
        currLevel = root.folders
        while currLevel:
            nextLevel = []
            for f in currLevel:
                folders.append(f)
                nextLevel.extend(f.folders)
            currLevel = nextLevel
            folders.extend(currLevel)
        return folders

class StudyType(DeclarativeBase):
    __table__=metadata.tables['study_type']
    studies=relation('Study', backref='studyType', passive_deletes=True)
    
    @classmethod
    def byName(cls, name):
        if name is None:
            return None
        else:
            return DBSession().query(cls).filter(cls.name == name).first()

    @classmethod
    def byCode(cls, code):
        if code is None:
            return None
        else:
            return DBSession().query(cls).filter(cls.code == code).first()

class Form(DeclarativeBase):
        __table__=metadata.tables['form']
