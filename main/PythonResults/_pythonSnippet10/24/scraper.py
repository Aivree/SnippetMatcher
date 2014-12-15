from bs4 import BeautifulSoup
import re
import urllib2
from flask import Flask, jsonify, render_template
from flask.ext.sqlalchemy import SQLAlchemy
from datetime import datetime
from threading import Timer, Thread, Lock
from time import time
import atexit

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///app.db'
db = SQLAlchemy(app)

next_call = time()
POOL_TIME = 10
data_lock = Lock()
thread = Thread()

def interrupt():
    global thread
    thread.cancel()

""" from http://stackoverflow.com/questions/7102754/jsonify-a-sqlalchemy-result-set-in-flask """
def dump_datetime(value):
    """Deserialize datetime object into string form for JSON processing."""
    if value is None:
        return None
    return [value.strftime("%Y-%m-%d"), value.strftime("%H:%M:%S")]

class Log(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.DateTime, default = datetime.now)
    values = db.Column(db.PickleType)

    def __init__(self, values):
        self.values = values

    @property
    def serialize(self):
        return {
            'date': dump_datetime(self.date),
            'values': self.values
        }

@app.route('/api/<date>')
def api(date):
    print date
    return jsonify(json_list = [i.serialize for i in Log.query
    .order_by(Log.date.desc())
    .filter(Log.date.like(date + '%'))
    ])

def scrape():
        power_levels = []
        snr = []
        url = 'http://192.168.100.1/Docsis_system.asp'
        page = urllib2.urlopen(url)
        soup = BeautifulSoup(page, 'lxml')
        for td in soup.findAll('td', {'class': 'stdbold'}):
            if 'dw(vdbmv)' in str(td):
                power_levels.append(re.search(r"(\d+)\.(\d+)", str(td)).group(0))
            if 'dw(vdb)' in str(td):
                snr.append(re.search(r"(\d+)\.(\d+)", str(td)).group(0))

        export_down = {}
        export_up = {}
        for i in xrange(len(snr)):
            export_down['Channel ' + str(i+1)] = {'Power level': power_levels[i], 'SNR': snr[i]}

        for i in xrange(len(snr), len(power_levels)):
            export_up['Channel ' + str(i-len(snr)+1)] = {'Power level': power_levels[i]}

        export = {'Downstream': export_down, 'Upstream': export_up}

        db.session.add(Log(export))
        return db.session.commit()

def server():
    global next_call, thread, data_lock
    with data_lock:
        scrape()
        next_call = next_call + POOL_TIME
        thread = Timer(next_call - time(), server)
        print(next_call - time())
        thread.start()

@app.route('/')
def index():
    return "hellow"

@app.route('/rawlog')
def rawlog():
    return render_template('rawlog.html', data=Log.query.order_by(Log.date.desc()).all())

@app.route('/rawlog/<date>')
def rawlog_date(date):
    return render_template('rawlog.html', data=Log.query
    .order_by(Log.date.desc())
    .filter(Log.date.like(date + '%')))

@app.route('/graph')
def graph():
    return render_template('graph.html', data=Log.query.order_by(Log.date.desc()).all())


if __name__=='__main__':
    #server()
    atexit.register(interrupt)
    app.run(debug=True, use_reloader=False)
