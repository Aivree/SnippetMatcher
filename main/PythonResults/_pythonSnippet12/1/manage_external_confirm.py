#-*- coding: utf-8 -*-
import logging

from pylons import request, response, session, tmpl_context as c, url
from pylons.controllers.util import abort, redirect
from sparta.lib import app_globals as g
from sparta.lib.base import BaseController, render
from sparta.model import * 

from sqlalchemy.engine.base import RowProxy
from datetime import *
import hashlib
from AutoPages import *


#스파르타에 정의된 오류 코드를 임포트한다.
from sparta_error_code import *

log = logging.getLogger(__name__)

#모듈의 이름을 구한다.
#이 모듈 이름은 모듈의 파일명이 바뀌어도 모듈 안에서 경로 지정에 문제가 없도록 하기 위해서
#컨트롤러 내부에서 사용한다.
MODULE_NAME = os.path.splitext(os.path.basename(__file__))[0];


#sqlalchemy로 쿼리 실행 결과를 얻었을때, 리스트가 리턴되는데, 이 리스트 안에 각 레코드는
#RowProxy 라는 타입으로 리턴된다. 이렇게 리턴된 쿼리 결과를 json.dumps()함수를 이용해서
#JSON형태로 변환할때 RowProxy 가 표준 타입이 아니기 때문에 json모듈에서 오류가 발생한다.
#이렇게 표준 타입이 아닌 데이터를 JSON으로 변환하기 위해서 json.dumps()함수는 cls 라는 
#파라메터를 제공하는데, 이 파라메터에 JSONEncoder자식 클래스를 지정하면 dumps() 함수가
#표준 타입이 아닌 데이터에 대해서 default()메서드를 호출한다. 여기서 데이터를 표준 타입으로
#변환해서 리턴하면 json 모듈이 해당 타입을 올바르게 JSON형태로 만들어준다.
#이클래스는 sqlalchemy의 RowProxy 타입을 올바른 JSON형태로 변환할 수 있도록 해주는 클래스이다.
class RowProxyJsonEncoder(json.JSONEncoder):
	def default(self, obj):
		if type(obj) is RowProxy:
			return tuple(obj);
		else:
			return unicode(obj);
		return json.JSONEncoder.default(self, obj)

#입력된 SQL 문을 실행하고 RowProxy 객체를 리턴하는 함수
def RunSql(sql, conn=None):
	#데이터베이스 연결 객체
	if (conn == None):
		conn = Base.metadata.bind.connect();
		
	return conn.execute(sql);
	
#입력된 파이썬 객체를 JSON 문자열로 만들어서 리턴하는 함수
def JSON(obj):
	return json.dumps(obj=obj, skipkeys=True, encoding='utf8', ensure_ascii=False, cls=RowProxyJsonEncoder);


#입력된 객체를 간트차트 모듈에서 필요한 형태의 사전으로 만드는 함수
def MakeDict(rec):
	r = {};
	for key in rec.keys():
		r[str(key)] = rec[key];
		
	return r;

#외부 컨펌 컨트롤러
class ManageExternalConfirmController(BaseController):

	def index(self):
		#스파르타 상단의 사용자 정보에 사용자 아이디와 메일 수신 아이콘 처리 및 로그인 검사하는 함수
		autoFrameData(c, "/"+MODULE_NAME);
		
		c.firstLoadPage  = url(controller=MODULE_NAME, action="view_shots_need_confirm_for_project")
		c.firstLoadTitle = u"[우샤] 감독 리뷰"
		c.leftAccordPanels = u"""[{title:"Navigation",url:"/%s/menu",rootText:"All Tasks",iconCls:"icon-folder-go"}]"""%(MODULE_NAME)
		return render("frame_for_external_confirm.html");


	#현재 진행중인 프로젝트 리스트를 JSON 형태로 만드는 메서드
	#frame.html 에서 이 메서드를 호출해서 왼쪽 메뉴를 만든다.
	def menu(self):
		#SELECT IDX,Name as ProjectName,Code FROM Project where StatCode = 'ACT'
		#진행중인 프로젝트 목록을 구한다.
		active_projects = RunSql("SELECT IDX,Name as ProjectName,Code FROM Project where StatCode = 'ACT'");

		#프로젝트 메뉴의 최상위 항목을 생성한다.
		project_root_menu = {
			"text" : "전체 프로젝트 진행 현황", 
			"leaf" : False, 
			"expanded" : True, 
			"taburl" : u"/%s/view_shots_need_confirm_for_project/"%(MODULE_NAME),
			"title" : u"전체 프로젝트 진행 현황",
			"load_in_maintab" : True};

		#전체 프로젝트 목록을 저장할 리스트 객체를 생성한다.
		project_menu = [];

		#SQL 쿼리로 얻은 진행중인 프로젝트 목록을 사전 형태로 만든다.
		for project in active_projects:
			project_menu.append({
				"text" : project.ProjectName, 
				"leaf" : True, 
				"expanded" : True, 
				"taburl" : u"/%s/view_specific_project_schedule/%d"%(MODULE_NAME, project.IDX), 
				"title" : u"[%s] 일정"%project.ProjectName,
				"load_in_maintab" : True});

		project_root_menu["children"] = project_menu;
		
		r = [project_root_menu];
		
		#결과를 JSON 형태로 변환해서 리턴한다.
		return JSON(r);


	#컨펌해야할 샷 목록을 보여주는 메서드
	def view_shots_need_confirm_for_project(self):
		#컨펌할 샷 데이터를 로드하는 주소
		c.url_shot_list_need_extern_confirm = url(controller=MODULE_NAME, action="get_shots_that_need_confirm_for_project", id=75);
		
		#한페이지에 표시할 샷의 수
		c.shot_count_per_page = 20;
		
		return render("view_shots_need_confirm_for_project.html");		
		
	#특정 프로젝트에 대해서 외부 컨펌이 필요한 샷 목록을 리턴하는 메서드
	def get_shots_that_need_confirm_for_project(self, id):
		
		#SQL 실행을 위해서 데이터베이스에 연결을한다.
		conn = Base.metadata.bind.connect();
	
		#SQL 파라메터를 저장하는 사전
		sql_params = dict();
		
		#샷 아이디
		sql_params["shot_id"] = id;
		
		#샷을 페이징하기 위한 LIMIT 문
		sql_params["shot_page_filter"] = u"";
		
		#페이징 요청이 있는지 확인한다.
		if (request.params.has_key("start") and request.params.has_key("limit")):
			sql_params["shot_page_filter"] = u" LIMIT %s, %s " % (request.params["start"], request.params["limit"]); 
	
		#컨펌 대상 샷 목록을 얻기 위한 SQL을 만든다.
		sql = u"""
			SELECT SQL_CALC_FOUND_ROWS
				IDX AS Id,
				Name AS ShotName,
				Thumb,
				Content,
				Roll,
				Scene,
				Duration,
				Note
			FROM
				Shot
			WHERE
				Shot.Parent1 = %(shot_id)s 
			%(shot_page_filter)s				
			""" % sql_params;
		
		#컨펌 대상 샷 목록을 구한다.
		shot_list = RunSql(sql, conn);
		
		#LIMIT 를 하지 않았을때의 총 샷의 수를 얻는다.
		total_shot_count = RunSql(u"SELECT FOUND_ROWS() AS TotalRows", conn).fetchone().TotalRows;
		
		r = [];
		
		
		#컨펌 대상 샷을 결과 목록에 저장한다.
		for shot in shot_list:
			r.append(MakeDict(shot));
			#print str(dir(shot));
			
		conn.close();
		
		return JSON({"TotalRowCount" : total_shot_count, "Rows" : r});
		
		
		
