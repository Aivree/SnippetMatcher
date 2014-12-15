#-*- coding: utf-8 -*-
import logging

from pylons import request, response, session, tmpl_context as c, url
from pylons.controllers.util import abort, redirect
from sparta.lib import app_globals as g
from sparta.lib.base import BaseController, render
from sparta.model import * 

from sqlalchemy.engine.base import RowProxy
import datetime
from AutoPages import *


log = logging.getLogger(__name__)

#모듈의 이름을 구한다.
#이 모듈 이름은 모듈의 파일명이 바뀌어도 모듈 안에서 경로 지정에 문제가 없도록 하기 위해서
#컨트롤러 내부에서 사용한다.
MODULE_NAME = os.path.splitext(os.path.basename(__file__))[0];


#입력된 SQL 문을 실행하고 RowProxy 객체를 리턴하는 함수
def RunSql(sql, conn=None):
	#데이터베이스 연결 객체
	if (conn == None):
		conn = Base.metadata.bind.connect();
		
	return conn.execute(sql);

def JSON(obj):
	return json.dumps(obj=obj, skipkeys=True, encoding='utf8', ensure_ascii=False, cls=RowProxyJsonEncoder);


#인력별 스케줄을 보여줄때 사용할 인력에 대한 정보를 사전으로 만드는 함수
def MakeResourceItem(obj):
	item = {
		"Id" : obj.UserIDX,
		"Name" : obj.UserName, 
		"Department" : obj.Department,
		"UserId" : obj.UserId,
		"MaxTaskLevel" : 0,
		"TaskCount" : 0};

	return item;


#인력별 스케줄을 보여줄때 사용할 스케줄에 대한 정보를 사전으로 만드는 함수
def MakeScheduleItem(obj, conflict_tasks, level):
	#task_ids = [];
	
	#for task in conflict_tasks:
	#	task_ids.append(task.TaskId);
	
	item = {
		"TaskId" : obj.TaskId,
		"Level" : level,
		"ResourceId" : obj.UserIDX,
		"StartDate" : obj.EstStart, 
		"EndDate" : obj.EstEndForView,
		"Title" : obj.TaskName
		};
		
	if (hasattr(obj, "ProjectName")):
		item["ProjectName"] = obj.ProjectName;
		
	if (hasattr(obj, "ShotName")):
		item["ShotName"] = obj.ShotName;
		
	if (hasattr(obj, "ShotThumb")):
		item["ShotThumb"] = obj.ShotThumb;
		
		#"ConflictTasks":task_ids};

	return item;



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


#인력 관리 컨트롤러
#인력별 일정을 관리하는 컨트롤러
class ManageResourceController(BaseController):

	def index(self):
		#스파르타 상단의 사용자 정보에 사용자 아이디와 메일 수신 아이콘 처리 및 로그인 검사하는 함수
		autoFrameData(c, "/"+MODULE_NAME);

		#c.firstLoadPage = MODULE_NAME+"/view_all_resource_schedule_overview/"
		c.firstLoadPage = url(controller=MODULE_NAME, action="view_resource_role")
		c.firstLoadTitle = u"전체 인력 현황"
		c.leftAccordPanels = u"""[{title:"Navigation",url:"/%s/menu",rootText:"All Tasks",iconCls:"icon-folder-go"}]"""%(MODULE_NAME)
		return render("frame.html");


	#모든 인력의 스케쥴을 보여주는 메서드
	def view_all_resource_schedule_overview(self, id=None):

		#모든 인력의 목록을 JSON 으로 리턴해주는 주소
		c.resource_list_data_url = u"/%s/get_resource_data_as_json"%(MODULE_NAME);

		#모든 인력의 작업 일정 정보를 JSON 으로 리턴해주는 주소
		c.resource_schedule_data_url = u"/%s/get_resource_schedule_data_as_json"%(MODULE_NAME);
		
		#특정 프로젝트에 대한 정보만 얻고자할 경
		if (id is not None):
			c.resource_schedule_data_url += u"/%s" % (id);
			c.resource_list_data_url += u"/%s" % (id);
			
		#작업에 관한 정보를 변경시킬때 사용할 주소
		c.move_task_url = u"/%s/move_task"%(MODULE_NAME);

		return render("view_all_resource_schedule_overview.html");

	#인력별 할당된 역할을 보여주는 메서드
	def view_resource_role(self, id=None):
	
		#인력별 데이터를 제공하는 주소
		c.data_url = u"/%s/get_resource_role_data_as_json" % (MODULE_NAME);
		if (id is not None):
			c.data_url += u"/%s" % (id);
			
		c.role_columns, c.role_data_fields = self.get_resource_column_data_as_string();
		if (request.params.has_key("project_id_list")):
			c.project_id_list = request.params["project_id_list"];
		else:
			c.project_id_list = "[]";
		
		return render("view_resource_role.html");

	#frame.html 에서 이 메서드를 호출해서 왼쪽 메뉴를 만든다.
	def menu(self):

		#프로젝트 메뉴의 최상위 항목을 생성한다.
		project_root_menu = {
			"text":"전체 인력 현황", 
			"leaf":True, 
			"expanded":True, 
			"taburl":u"/%s/view_all_resource_schedule_overview/"%(MODULE_NAME),
			"title":u"전체 인력 현황",
			"load_in_maintab":True};

		#인력별 역할 할당 메뉴 항목을 생성한다.
		resource_role_menu = {
			"text":"작업자별 역할표", 
			"leaf":False, 
			"expanded":True, 
			"taburl":u"/%s/view_resource_role/"%(MODULE_NAME),
			"title":u"작업자별 역할표",
			"load_in_maintab":True};
			
		#조직 구조를 구한다.
		team_list, team_tree = self.get_full_team_name_table();
		
		for (code, team) in team_list.items():
			team["text"] = team["Name"];
			team["title"] = team["Name"]+u" 역할표";
			team["leaf"] = not (team.has_key("children") and len(team["children"]) > 0);
			if (team["leaf"] == True):
				team["iconCls"] = "icon-team-type4";
			else:
				team["iconCls"] = "icon-team-type1";
			team["taburl"] = u"/%s/view_resource_role/%s"%(MODULE_NAME, team["TreeCode"]);
			team["load_in_maintab"] = True;
			
		resource_role_menu["children"] = team_tree;

		r = [resource_role_menu, project_root_menu];
		
		#결과를 JSON 형태로 변환해서 리턴한다.
		return json.dumps(obj=r, skipkeys=True, encoding='utf8', ensure_ascii=False, cls=RowProxyJsonEncoder);

	#인력별 작업 배정표에 컬럼 데이터로 표시할 정해져 잇는 작업 종류 정보를 문자열로 리턴하는 메서드
	def get_resource_column_data_as_string(self):
		
		#정의된 모든 작업 종류 정보를 구한다.
		task_types = RunSql(u"""
			SELECT 
				Code,
				Name AS TaskTypeName 
			FROM 
				Task_Type 
			ORDER BY 
				Name
			""");
			
		c = u"";
		f = u"";
		
		for task_type in task_types:
			c = c + u"""
			{
				header : '%s', 
				dataIndex : '%s', 
				width : 40,
				menuDisabled : true,
				align : 'center',
				renderer : pctChange
			},				
			""" % (task_type.TaskTypeName, task_type.Code);
		
			f = f + u"""
			{
				name : '%s',
				type : 'bool'
			},
			""" % (task_type.Code);
			
		
		return (c, f);
		
	#User_Team.TreeCode 값으로 자신이 속한 팀의 정식명칭을 얻을 수 있도록 테이블을 리턴하는 메서드
	#본부/실/팀 등을 포함한 팀명을 TreeCode 값으로 알수 있다.
	def get_full_team_name_table(self):
		#전체 팀 정보를 TreeCode 값으로 정렬시켜서 얻는다.
		teams = RunSql(u"""
			SELECT
				Name AS TeamName,
				TreeCode,
				Code As TeamCode
			FROM
				User_Team
			ORDER BY
				TreeCode
			""");
			
		r = {};
		team_tree = [];
		code = "";
		full_name = "";
		
		for team in teams:
			code = team.TreeCode;
			
			team_data = {};
			team_data["Name"] = team.TeamName;
			team_data["TeamCode"] = team.TeamCode;
			team_data["TreeCode"] = team.TreeCode;
			team_data["children"] = [];
			
			team_to_attach = team_tree;
			
			if (len(code) > 2):

				#중간에 코드가 비어 있을 수 있기 때문에 루프를 돌면서 붙일수 있는 가장 근접한 부모 노드를 찾는다.				
				i = len(code)-2;
				while (i >= 2):
					sub_code = code[0:i];
				
					if (r.has_key(sub_code)):
						team_to_attach = r[sub_code]["children"];
						full_name = r[sub_code]["FullName"]+" / "+team.TeamName;
						break;
					i -= 2;
			else:
				full_name = team.TeamName;
		
			r[code] = team_data;
			team_to_attach.append(team_data);
			team_data["FullName"] = full_name;
			
		return (r, team_tree);
			


	#사용자별로 처리할 수 있는 작업 종류를 JSON형태로 리턴하는 메서드
	def get_resource_role_data_as_json(self, id=None):
		
		#팀 명칭 테이블을 얻는다.
		team_code_table, team_tree = self.get_full_team_name_table();
		
		where = u"";
		if (id is not None):
			where = u"WHERE User_Team.TreeCode LIKE '%s%%%%'" % (id);
		
		#특정 프로젝트에 참가하는 인력만을 필터링하기 원한다면
		project_join = u"";
		if (request.params.has_key("project_id_list")):
			project_id_list = json.loads(request.params["project_id_list"]);
			project_join = u"""
				JOIN
					(SELECT
						AssignUser
					FROM
						Task
					WHERE
						Parent1 IN(%s) AND AssignUser is not null
					GROUP BY
						AssignUser
					ORDER BY
						AssignUser
					) AS WorkerOfProject
				ON
					(User.UserID = WorkerOfProject.AssignUser)
				""" % (",".join(map(str, project_id_list)));
		
		sql = u"""
			SELECT 
				User.IDX AS UserIDX,
				User.UserID,
				User.Name AS UserName,
				User_Team.Name AS UserTeam,
				IF(User_Team.TreeCode is null, '9999999999', User_Team.TreeCode) AS TreeCode,
				User.TaskTypeCodes
			FROM 
				User
			LEFT OUTER JOIN
				User_Team
			ON
				User.TeamCode = User_Team.Code
			%s
			%s
			ORDER BY
				TreeCode,
				User.Name
			""" % (project_join, where);
			
		
		#작업자들에게 할당된 처리 가능한 작업 종류 데이터를 구한다.
		users = RunSql(sql);
			
		
		r = [];
		
		for user in users:
			data = {
				"Id" : user.UserIDX,
				"UserID" : user.UserID,
				"UserName" : user.UserName,
				"UserTeam" : user.UserTeam,
				"TreeCode" : user.TreeCode};
				
			#작업자가 할 수 있는 작업 종류 코드 목록을 얻는다.
			roles = user.TaskTypeCodes.split(",");
			
			for role in roles:
				if (role <> u""):
					data[role] = True;
			
			r.append(data);
			
		#결과를 JSON 형태로 변환해서 리턴한다.
		return json.dumps(obj=r, skipkeys=True, encoding='utf8', ensure_ascii=False, cls=RowProxyJsonEncoder);


	#전체 작업자들의 목록을 JSON형태로 리턴하는 메서드
	#각 작업자별로 중첩으로 할당된 최대 작업 수준 값을 계산해서 함께 전달한다.
	def get_resource_data_as_json(self, id=None):

		#특정 프로젝트에 참가하는 인력만을 필터링하기 원한다면
		project_join = u"";
		if (id is not None):
			project_join = u"""
				JOIN
					(SELECT
						AssignUser
					FROM
						Task
					WHERE
						Parent1 = %s AND AssignUser is not null
					GROUP BY
						AssignUser
					ORDER BY
						AssignUser
					) AS WorkerOfProject
				ON
					(User.UserID = WorkerOfProject.AssignUser)
				""" % (id);
		
	
		#모든 인력 리스트를 구한다.
		sql_select_worker = u"""
			SELECT 
				User.IDX as UserIDX,
				User.Name as UserName,
				User.UserId,
				User_Team.Name as Department,
				User_Type.Name as UserType
			FROM
				User
			LEFT OUTER JOIN
				User_Team
			ON
				(User.TeamCode = User_Team.Code)
			LEFT OUTER JOIN
				User_Type
			ON
				(User.TypeCode = User_Type.Code)
			%s
			WHERE
				User.StatCode = 'ACT' 
			ORDER BY
				UserIDX
		""" % (project_join);
		
		workers = RunSql(sql_select_worker);
		
		
		
		task_filter = u"";
		take_filter = u"";
		
		#특정 프로젝트에 대한 작업 정보를 얻기 원한다면
		#if (id is not None):
		#	task_filter = u"WHERE Parent1=%s" % (id);
		#	take_filter = u"WHERE Parent1=%s" % (id);
			
		#기간이 설정된 경우
		if (request.params.has_key("start_date") and request.params.has_key("end_date")):
			start_date = datetime.datetime.strptime(request.params["start_date"], "%Y-%m-%d").date();
			end_date = datetime.datetime.strptime(request.params["end_date"], "%Y-%m-%d").date();
			
			if (task_filter == u""):
				task_filter += u"WHERE ";
			else:
				task_filter += u" AND ";
				
			task_filter += u" DATE(EstStart) >= DATE('%s') AND DATE(ADDDATE(EstStart, INTERVAL IF(EstDay <= 0, 0, EstDay-1) Day)) <= DATE('%s')" % (str(start_date), str(end_date));

		#설정된 조건에서 작업이 있는 작업자만 원하는지 검사한다.
		workers_who_has_task_only = (request.params.has_key("workers_who_has_task_only") and (request.params["workers_who_has_task_only"] == u"true"));
		
	
		#모든 인력의 작업 일정을 구한다.
		sql = u"""
			SELECT
				Task.IDX as TaskId, 
				TaskStatus.TakeId,
				Task.AssignUser,
				DATE(Task.EstStart) EstStart, 
				DATE(ADDDATE(Task.EstStart, INTERVAL Task.EstDay Day)) EstEndForView, 
				DATE(ADDDATE(Task.EstStart, INTERVAL Task.EstDay-1 Day)) EstEnd, 
				Task.EstDay as Duration, 
				Task.Parent3 as ShotId, 
				Task.Parent1 as ProjectId, 
				User.Name as UserName, 
				User.OfficeName, 
				User.Department,
				User.UserIDX,
				User.UserId,
				CONCAT(Task_Type.Name, ' (', Task.Element, ')') as TaskName,
				Task_Type.Name,
				TaskStatus.TakeCount,
				TaskStatus.TakeStatus as TaskStatus

			FROM 
				(SELECT
					IDX,
					Element,
					TypeCode,
					EstStart,
					IF((EstDay <= 0) or (EstDay is null), 1, EstDay) AS EstDay,
					Parent1,
					Parent3,
					AssignUser
				FROM
					Task 
				%s
				) AS Task

			LEFT OUTER JOIN 
				(SELECT
					User.IDX as UserIDX, 
					User.Name,
					User.UserId,
					User.OfficeName,
					User_Team.Name as Department
				FROM
					User
				LEFT OUTER JOIN
					User_Team
				ON
					(User.TeamCode = User_Team.Code)) as User
			ON 
				(Task.AssignUser = User.UserId)

			LEFT OUTER JOIN
				(SELECT
					Name,
					Code
				FROM
					Task_Type) AS Task_Type				
			ON
				(Task.TypeCode = Task_Type.Code)

			LEFT OUTER JOIN
				(SELECT
					Take.TakeId, 
					Take.TakeCode, 
					Take_Stat.Name as TakeStatus, 
					Take.TaskId, 
					COUNT(Take.TakeId) as TakeCount 
				FROM
					(SELECT 
						IDX as TakeId,
						Code as TakeCode,
						Parent4 as TaskId,
						StatCode
					FROM 
						Take 
					%s
					ORDER BY TakeId DESC) AS Take
				LEFT OUTER JOIN
					Take_Stat 
				ON 
					(Take.StatCode = Take_Stat.Code) 
				GROUP BY 
					TaskId
				ORDER BY 
					TaskId) as TaskStatus
			ON
				(Task.IDX = TaskStatus.TaskId)
				
			JOIN
				(SELECT
					AssignUser
				FROM
					Task
				WHERE
					Parent1 = %s AND AssignUser is not null
				GROUP BY
					AssignUser
				ORDER BY
					AssignUser
				) AS WorkerOfProject
			ON
				(WorkerOfProject.AssignUser = Task.AssignUser)

			WHERE
				UserId is not null
			ORDER BY
				UserIDX,				 
				EstStart,
				Duration DESC
			""" % (task_filter, take_filter, id);
		#print "get_resource_data_as_json() SQL============>\n", sql;
		schedules = RunSql(sql);

		#결과를 저장할 리스트를 생성한다.
		r = [];
		
		#현재 작업의 시작날짜까지 할당되어 있는 작업 목록을 저장할 리스트
		conflict_tasks = [];
		
		task_levels = {};
		
		#작업자 정보를 저장하는 사전
		resource = {};
		
		#작업자에게 할당된 작업 목록을 저장할 리스트
		tasks = [];
		
		level = 0;
		#최대 중첩되는 작업의 수
		max_task_level = 0;
		#작업자의 총 작업수
		task_count = 0;
		
		schedule_index = -1;
		schedule_count = schedules.rowcount;
		
		#print "schedule row count = [%d]" % (schedule_count);
		#print "worker   row count = [%d]" % (workers.rowcount);
		
		is_need_new_schedule = True;
		
		for worker in workers:
			
			#print "IDX = [%.4d], Name = [%s], Id = [%s]" % (worker.UserIDX, worker.UserName, worker.UserId);
			
			#작업자에 대한 정보를 저장하는 사전을 생성한다.
			resource = MakeResourceItem(worker);
		
			#새로 생성된 작업자 정보를 전체 결과 목록에 추가시킨다.
			#r.append(resource);
			
			#작업이 할당된 작업자도 있고 작업이 없는 작업자도 있을수 있기 때문에, workers 와 schedulers 에 있는
			#작업자의 수가 다를 수 있다.
			#하지만 workers 에 있는 작업자의 수가 더 많으며 두 리스트 모두 사용자 키값으로 정렬되어 있기 때문에
			#workers 에는 있는 작업자이지만 할당된 작업이 없을 경우, schedulers 목록에는 없을 수 있다.
			#이런 작업자들을 전체 목록에 추가시킨다.
			if (schedule_index >= schedule_count):
				#작업이 할당되지 않은 사용자 데이터도 원한다면, 작업자를 결과에 추가시킨다.
				if (workers_who_has_task_only == False):
					r.append(resource);
				continue;
				
			#작업 목록에서 작업을 꺼내야하는 경우
			if (is_need_new_schedule):
			
				#작업자 목록에 없는 작업자가 할당된 작업이 있을 경우, 처리하지 않고 넘긴다.
				while(True):
					schedule = schedules.fetchone();
					schedule_index = schedule_index+1;
					
					if (schedule is None):
						break;
					
					if (worker.UserIDX <= schedule.UserIDX):
						break;
						
					#print "TaskId = [%.4d], AssignedUserIDX = [%.4d] is not valid user... skip" % (schedule.TaskId, schedule.UserIDX);
				
				is_need_new_schedule = False;
			
			#print "IDX = [%.4d], Name = [%s], Id = [%s]" % (worker.UserIDX, worker.UserName, worker.UserId);
			
			if (schedule is None):
				#print "작업 일정 데이터가 더이상 없음...";
				
				#작업이 할당되지 않은 사용자 데이터도 원한다면, 작업자를 결과에 추가시킨다.
				if (workers_who_has_task_only == False):
					r.append(resource);			
				continue;
			
			#현재 작업자에게 할당된 작업이 없다면,
			if (worker.UserIDX < schedule.UserIDX):
				#print "현재 작업자에게 할당된 작업 일정 데이터가 더이상 없음...";
				
				#작업이 할당되지 않은 사용자 데이터도 원한다면, 작업자를 결과에 추가시킨다.
				if (workers_who_has_task_only == False):
					r.append(resource);			
			 	continue;
	
			r.append(resource);
			
			#새로운 작업자에 대해서 중첩 작업 레벨 값을 계산하기 위해서 관련된 변수들을 초기화한다.
			conflict_tasks = [];				
			task_levels = {};
			tasks = [];
			max_task_level = 0;
			task_count = 0;
			
			is_need_new_schedule = True;
			
			#할당된 작업이 있는 작업자의 경우, 최대로 중첩되는 작업 중첩 레벨을 계산한다.
			while (schedule_index < schedule_count):
				#print "[%.4d] TaskId = [%d]" % (schedule_index, schedule.TaskId);
				
				#일정이 겹치는 작업 목록에 있는 작업들 중에서, 현재 작업과 비교해서 중첩되지 않는 작업들을 제거한다.
				if (len(conflict_tasks) > 0):
					i = 0;
					while (i < len(conflict_tasks)):
						task = conflict_tasks[i];
					
						if (task.EstEnd < schedule.EstStart):
							del task_levels[task.TaskId];
							del conflict_tasks[i];
						else:
							i = i+1;
						
		
				#중첩되는 작업들의 레벨을 리스트로 얻는다.
				levels = task_levels.values();
			
				#현재 작업의 레벨을 판단한다.			
				level = 0;
				
				#중첩되는 작업들의 레벨 값 중에서 가장 작으면서 중복되지 않는 것을 찾는다.
				while (True):
					if (level not in levels):
						break;
					else:
						level = level + 1;

				#print "task_id=[%d], level=[%d], start=[%s], end=[%s], assign=[%s]" % (schedule.TaskId, level, str(schedule.EstStart), str(schedule.EstEnd), schedule.AssignUser);

				#작업자에게 할당된 작업에 대한 정보를 저장하는 사전 객체를 생성해서 작업 목록에 추가시킨다.
				tasks.append(MakeScheduleItem(schedule, conflict_tasks, level));
				task_count += 1;
			
				#작업자에게 중첩할당된 작업의 최고 수준 값을 갱신시킨다.
				if (level > max_task_level):
					max_task_level = level;
				
				#현재 작업이 다음 일정과 겹치는지 검사하기 위해서 현재 작업을 리스트에 넣는다.
				conflict_tasks.append(schedule);
			
				#작업 아이디에 해당하는 작업의 중첩 수준 값을 저장한다.
				#같은 레벨에 작업들이 겹쳐지지 않도록 하기 위한 리스트
				task_levels[schedule.TaskId] = level;

				worker_changed = False;

				#다음 작업 데이터를 얻는다.
				schedule = schedules.fetchone();
				
				#작업 목록에서 다른 작업에 대해서 검사하기 위해서 작업 인덱스를 증가시킨다.
				schedule_index = schedule_index+1;

				if (schedule is None):
					worker_changed = True;
				elif (worker.UserIDX <> schedule.UserIDX):
					worker_changed = True;

				#검사 대상 작업자가 변경되는 경우(즉, 새로운 작업자에게 할당된 작업 목록의 처리를 시작해야하는 경우)
				#또는 마지막 작업일 경우
				if (worker_changed):
			
					#작업자의 최대 중첩 작업 수준값을 갱신한다.
					resource["MaxTaskLevel"] = max_task_level;
					
					#총 작업의 수를 갱신한다.
					resource["TaskCount"] = task_count;
			
					#작업자의 작업 목록을 JSON 문자열로 만들어서 작업자 정보에 속성으로 넣는다.
					#resource["Tasks"] = json.dumps(obj=tasks, skipkeys=True, encoding='utf8', ensure_ascii=False, cls=RowProxyJsonEncoder);

					#resource["Name"] = "%s(%d)" % (resource["Name"], max_task_level+1);
					#print "[%s] level = [%d]" % (resource["Name"], max_task_level);
					
					#마지막 작업 데이터를 처리했다면, 더이상 작업 데이터가 없으므로 이 루프 안으로 들어오지 않도록 작업 인덱스를 작업 개수와 동일하게 만든다.
					if (schedule_index == schedule_count-1):
						schedule_index = schedule_count;
					
					is_need_new_schedule = False;
					
					#작업자가 변경되었으므로 더이상 처리하지 않고 종료한다.
					break;
				

		
		#결과 리스트를 JSON 형태로 변환한다.
		return json.dumps(obj=r, skipkeys=True, encoding='utf8', ensure_ascii=False, cls=RowProxyJsonEncoder);

	#특정 작업의 일정이나 작업 할당자를 변경시키는 메서드
	def move_task(self):
		#print "move_task(taskid=[%s], user=[%s], start=[%s]"%(request.params["TaskId"], request.params["AssignedUserId"], request.params["StartDate"]);
		
		user_id = request.params["AssignedUserId"];
		
		try:
			r = RunSql(u"""
				UPDATE 
					Task
				SET
					AssignUser='%s',
					EstStart='%s'
				WHERE
					IDX IN(%s) AND
					(SELECT TaskTypeCodes FROM `User` where UserID = '%s') LIKE CONCAT('%%%%',TypeCode,',%%%%')
			""" % (user_id, request.params["StartDate"], request.params["TaskId"], user_id));
			
			#print "적용된 행의 수 = [%d]" % (r.rowcount);
		except:
			return "failed....";
		
		return "task has changed!!";


	#작업이 할당된 인력의 작업 일정을 JSON으로 리턴하는 메서드
	def get_resource_schedule_data_as_json(self, id=None):
	
		users = u"";
	
		#할당된 작업 목록을 얻고자하는 작업자 목록을 얻는다.
		if (request.params.has_key("Users")):
			users = request.params["Users"]; 		
			#print 'requested users = ',users;
			
		task_filter = u"";
		shot_filter = u"";
		take_filter = u"";
		project_filter = u"";
		
		#프로젝트 아이디를 숫자값으로 변경한다.
		try:
			project_id = int(id);
		except:
			project_id = 0;
		
		#특정 프로젝트에 대한 작업 정보를 얻기 원한다면
		#if (id is not None):				
		#	task_filter = u"WHERE Parent1=%s" % (id);
		#	take_filter = u"WHERE Parent1=%s" % (id);
		#	shot_filter = u"WHERE Parent1=%s" % (id);
		#	project_filter = u"WHERE IDX=%s" % (id);
			
		#기간이 설정된 경우
		if (request.params.has_key("start_date") and request.params.has_key("end_date")):
			start_date = datetime.datetime.strptime(request.params["start_date"], "%Y-%m-%d").date();
			end_date = datetime.datetime.strptime(request.params["end_date"], "%Y-%m-%d").date();
			
			if (task_filter == u""):
				task_filter += u"WHERE ";
			else:
				task_filter += u" AND ";
				
			task_filter += u" DATE(EstStart) >= DATE('%s') AND DATE(ADDDATE(EstStart, INTERVAL EstDay-1 Day)) <= DATE('%s')" % (str(start_date), str(end_date));
		
			
		#모든 인력의 작업 일정을 구한다.
		sql = u"""
			SELECT
				Project.ProjectIDX,
				Project.ProjectName,
				Shot.ShotName,
				Shot.ShotThumb,
				Task.IDX as TaskId, 
				Task.AssignUser,				
				TaskStatus.TakeId,
				DATE(Task.EstStart) EstStart, 
				DATE(ADDDATE(Task.EstStart, INTERVAL Task.EstDay Day)) EstEndForView, 
				DATE(ADDDATE(Task.EstStart, INTERVAL Task.EstDay-1 Day)) EstEnd, 
				Task.EstDay as Duration, 
				Task.Parent3 as ShotId, 
				Task.Parent1 as ProjectId, 
				User.Name as UserName, 
				User.OfficeName, 
				User.Department,
				User.UserIDX,
				User.UserId,
				CONCAT(Task_Type.Name, ' (', Task.Element, ')') as TaskName,
				Task_Type.Name,
				TaskStatus.TakeCount,
				TaskStatus.TakeStatus as TaskStatus

			FROM 
				(SELECT
					IDX,
					Element,
					TypeCode,
					EstStart,
					IF((EstDay <= 0) or (EstDay is null), 1, EstDay) AS EstDay,
					Parent1,
					Parent3,
					AssignUser
				FROM
					Task 
				%s
				) AS Task

			LEFT OUTER JOIN 
				(SELECT
					User.IDX as UserIDX, 
					User.Name,
					User.UserId,
					User.OfficeName,
					User_Team.Name as Department
				FROM
					User
				LEFT OUTER JOIN
					User_Team
				ON
					(User.TeamCode = User_Team.Code)) as User
			ON 
				(Task.AssignUser = User.UserId)

			LEFT OUTER JOIN
				(SELECT
					Name,
					Code
				FROM
					Task_Type) AS Task_Type				
			ON
				(Task.TypeCode = Task_Type.Code)

			LEFT OUTER JOIN
				(SELECT
					Take.TakeId, 
					Take.TakeCode, 
					Take_Stat.Name as TakeStatus, 
					Take.TaskId, 
					COUNT(Take.TakeId) as TakeCount 
				FROM
					(SELECT 
						IDX as TakeId,
						Code as TakeCode,
						Parent4 as TaskId,
						StatCode
					FROM 
						Take 
					%s
					ORDER BY TakeId DESC) AS Take
				LEFT OUTER JOIN
					Take_Stat 
				ON 
					(Take.StatCode = Take_Stat.Code) 
				GROUP BY 
					TaskId
				ORDER BY 
					TaskId) as TaskStatus
			ON
				(Task.IDX = TaskStatus.TaskId)
	
			LEFT OUTER JOIN
				(SELECT
					Project.IDX as ProjectIDX,
					Project.Name as ProjectName
				FROM
					Project
				%s) AS Project
			ON
				(Task.Parent1 = Project.ProjectIDX)
	
			LEFT OUTER JOIN
				(SELECT
					Shot.IDX,
					Shot.Name as ShotName,
					Shot.Thumb as ShotThumb
				FROM
					Shot
				%s) AS Shot
			ON
				(Task.Parent3 = Shot.IDX)


		
			JOIN
				(SELECT
					AssignUser
				FROM
					Task
				WHERE
					Parent1 = %s AND AssignUser is not null
				GROUP BY
					AssignUser
				ORDER BY
					AssignUser
				) AS WorkerOfProject
			ON
				(WorkerOfProject.AssignUser = Task.AssignUser)



			WHERE
				UserId is not null 
			ORDER BY
				UserIDX,				 
				EstStart,
				Duration DESC
		""" % (task_filter, take_filter, project_filter, shot_filter, id);
		
		#print "sql=================>\n", sql;
		
		schedules = RunSql(sql);

		#결과를 저장할 리스트를 생성한다.
		r = [];
		
		#현재 작업의 시작날짜까지 할당되어 있는 작업 목록을 저장할 리스트
		conflict_tasks = [];
		
		task_levels = {};
		
		level = 0;
		
		#현재 검사하고 있는 대상 작업자의 식별자 (검사 대상 작업자가 변경되는 것을 감지하기 위해서 사용)
		current_user_idx = -1;
		
		for schedule in schedules:
		
			#검사 대상 작업자가 변경되는 경우(즉, 새로운 작업자에게 할당된 작업 목록의 처리를 시작해야하는 경우)
			if (current_user_idx <> schedule.UserIDX):
				#print "IDX = [%.4d], Name = [%s], Id = [%s]" % (schedule.UserIDX, schedule.UserName, schedule.UserId);
			
				#변경된 작업자의 식별자를 저장해서, 이후에 같은 작업자에 대한 할당 작업 목록은 동일한 작업자로 처리되도록 한다.
				current_user_idx = schedule.UserIDX;
				
				#작업자가 변경되었으므로, 작업자에게 중첩되는 작업 목록을 저장하던 리스트를 초기화한다.
				conflict_tasks = [];				
				task_levels = {};
			
			#일정이 겹치는 작업 목록에 있는 작업들 중에서, 현재 작업과 비교해서 중첩되지 않는 작업들을 제거한다.
			if (len(conflict_tasks) > 0):
				i = 0;
				while (i < len(conflict_tasks)):
					task = conflict_tasks[i];
					if (task.EstEnd < schedule.EstStart):
						del task_levels[task.TaskId];
						conflict_tasks.remove(task);
					else:
						i = i+1;
						
		
			#중첩되는 작업들의 레벨을 리스트로 얻는다.
			levels = task_levels.values();
			
			#print str(levels);

			#현재 작업의 레벨을 판단한다.			
			level = 0;
			
			#중첩되는 작업들의 레벨 값 중에서 가장 작으면서 중복되지 않는 것을 찾는다.
			while (True):
				if (level not in levels):
					break;
				else:
					level = level + 1;

			#print "task_id=[%d], level=[%d], start=[%s], end=[%s], assign=[%s]" % (schedule.TaskId, level, str(schedule.EstStart), str(schedule.EstEnd), schedule.AssignUser);
			item = MakeScheduleItem(schedule, conflict_tasks, level);
			
			#특정 프로젝트와 연관된 작업자만 로드하는 경우, 현재 작업이 그 프로젝트의 작업인지를 나타내는 정보를 추가시킨다.
			if (id is not None):
				item["RelatedTask"] = (schedule.ProjectIDX == project_id);
				
			r.append(item);
			
			#현재 작업이 다음 일정과 겹치는지 검사하기 위해서 현재 작업을 리스트에 넣는다.
			conflict_tasks.append(schedule);
					
			task_levels[schedule.TaskId] = level;

		#결과 리스트를 JSON 형태로 변환한다.
		return json.dumps(obj=r, skipkeys=True, encoding='utf8', ensure_ascii=False, cls=RowProxyJsonEncoder);


	#파라메터로 입력된 프로젝트 아이디를 이용해서, 해당 프로젝트에 참여한 작업자들이 할당 받은 작업의 종류를
	#해당 사용자가 처리할 수 있는 작업의 종류로 설정하는 메서드
	def set_user_role_from_project(self, id=None):
		project_filter = u"";
		
		#특정 프로젝트에서 역할 정보를 갱신하고 싶은 경우
		if (id is not None):
			project_filter = u"Task.Parent1 = %s AND " % (id);
	
		sql = u"""
			SELECT
				User.IDX AS UserIDX, 
				User.Name AS UserName, 
				User.UserID,
				Task.TypeCode AS TaskTypeCode,
				Task_Type.Name AS TaskTypeName
			FROM
				Task
			LEFT OUTER JOIN
				User
			ON
				AssignUser=User.UserID
			JOIN
				Task_Type
			ON
				Task.TypeCode = Task_Type.Code
			WHERE
				%s
				Task.AssignUser is not null AND
				User.IDX is not null
			GROUP BY
				Task.AssignUser, 
				Task.TypeCode 
			ORDER BY
				User.Name
			""" % (project_filter);
	
		#입력된 프로젝트 아이디에 작업을 할당받은 사용자 목록과 할당 받았던 작업의 종류를 구한다.
		user_roles = RunSql(sql);
		
		#사용자 테이블에서 역할 정보를 모두 제거한다.
		RunSql(u"""
			UPDATE
				User
			SET
				TaskTypeCodes = ''
			""");
			
		#현재 검사 대상 사용자의 키값
		current_user_idx = -1;
		count = 0;
		
		update_sql = u"";
		
		r = [];

		if (user_roles.rowcount > 0):
			for user in user_roles:
		
				#검사 대상 사용자가 변경되었다면
				if (current_user_idx <> user.UserIDX):
			
					#기존에 검사한 사용자가 있다면, 그 사용자가 처리할 수 있는 작업 종류에 대한 정보를 사용자 테이블에 갱신한다.
					if (current_user_idx <> -1):
						count += 1;
						#print "[%d] name = [%s], role = [%s]" % (count, user_name, role);
						
						r.append({"No":count, "Name":user_name, "Role":role});
					
						update_sql += u"""
							UPDATE
								User
							SET
								TaskTypeCodes = '%s'
							WHERE
								IDX = %d; \n
							""" % (role, current_user_idx);
							
						#10명씩 모아서 SQL을 실행한다.
						if ((count % 1) == 0):
							RunSql(update_sql);
							update_sql = u"";
						
					current_user_idx = user.UserIDX;
					role = u"";
					user_name = user.UserName;
				
				#현재 사용자가 처리할 수 있는 작업 종류 코드를 콤마로 연결한 단일 문자열을 만든다.
				role += user.TaskTypeCode + ",";

			#마지막 사용자에 대한 처리를 한다.
			#print "[%d] name = [%s], role = [%s]" % (count, user_name, role);
			r.append({"No":count, "Name":user_name, "Role":role});
			
			update_sql += u"""
				UPDATE
					User
				SET
					TaskTypeCodes = '%s'
				WHERE
					IDX = %d
				""" % (role, current_user_idx);
				
			RunSql(update_sql);
			
			return JSON(r);
				
				

