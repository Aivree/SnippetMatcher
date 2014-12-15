# -*- coding: utf-8 -*-
__author__ = 'bebop'


from flask import Flask, redirect, url_for, render_template, request, flash, session, jsonify, make_response,current_app
from werkzeug.security import generate_password_hash, check_password_hash
from apps import app,db
from apps.core.model import models as models


# from forms import JoinForm, LoginForm
from sqlalchemy import desc


#운영자 인증

def admin():
	if session['session_user_email']=='ydproject777@gmail.com':
		return render_template("admin.html")

	return redirect(url_for("index"))

#관리자 권한으로 배우정보 입력
def admin_actor():
	if session['session_user_email']=='ydproject777@gmail.com':
		if request.method=='POST':
			files = request.files['actor_image']
			filestream = files.read()

			actor_write=models.Actor(
				name=request.form['name'],
				image = filestream,
				age=request.form['age'],
				body_size=request.form['body_size'],
				active_year=request.form['active_year'],
				similar_actor=request.form['similar_actor'],
				category=request.form['category']
				)
			db.session.add(actor_write)
			db.session.commit()
			flash(u"배우 DB에 저장되었습니다.")
			return redirect(url_for("admin"))
		return render_template("admin.html")
	return redirect(url_for("index"))


#관리자 권한으로 영상정보 입력
def admin_video():
	if session['session_user_email']=='ydproject777@gmail.com':
		if request.method=='POST':
			files = request.files['video_image']
			filestream = files.read()

			video_write=models.Video(
				name=request.form['name'],
				image = filestream,
				category=request.form['category'],
				release_year=request.form['release_year'],
				exposure=request.form['exposure'],
				)
			db.session.add(video_write)
			db.session.commit()
			flash(u"영상 DB에 저장되었습니다.")
			return redirect(url_for("admin"))
		return render_template("admin.html")
	return redirect(url_for("index"))