from flask import Blueprint, request, render_template, redirect, url_for, json, g, flash, jsonify, session
from flaskext.uploads import (UploadSet, configure_uploads, IMAGES,
                              UploadNotAllowed)
import datetime as dt
import numpy as np
import os

from sonic_art import app
from config import UPLOADED_SONG_DEST

from utils import readMP3, readWAV


####### Config for flask-uploads ########
SOUND_FILE_EXTENSIONS = ('wav','mp3','aac','ogg','oga','flac')
uploaded_song = UploadSet('song', SOUND_FILE_EXTENSIONS)
configure_uploads(app, uploaded_song)


@app.route("/")
def index():
	return render_template("index.html")


@app.route("/test_data")
def get_test_data():
    data = readMP3("/home/jmeiring/envs/SonicArt/sonic_art/D5-587.33.mp3")
    print "*"*80
    print session["filepath"]
    #data = readMP3(session.filepath)
    return json.dumps(data.spectrogram.tolist())


@app.route('/upload', methods=['GET', 'POST'])
def upload():
    if request.method == 'POST' and 'song_upload' in request.files:
        current_time = dt.datetime.now()

        #print request.files['song']
        #filename = request.files['song_upload'] + current_time.strftime("%Y-%m-%dT%H:%M:%S")
        filename = uploaded_song.save(request.files['song_upload'])

        filepath = os.path.join(UPLOADED_SONG_DEST, filename)
        #print filepath
        session["filepath"] = filepath
        #session.raw_data = raw_data
        data = readMP3(filepath)
        #return redirect(url_for('configure'))
        #return render_template('configure.html', raw_data=json.dumps(raw_data.tolist()))
        return render_template('configure.html', raw_data=json.dumps(data.spectrogram.tolist()))
        #return render_template('configure.html')

    else:
        return render_template('upload.html')


@app.route('/configure', methods=['GET', 'POST'])
def configure():
    print session
    if request.method == 'POST' and 'confirm' in request.files:
        return render_template('index.html')
    else:
        return render_template('configure.html')

















