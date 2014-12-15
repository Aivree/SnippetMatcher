#-*- coding: utf-8 -*-
import logging

from pylons import request, response, session, tmpl_context as c, url
from pylons.controllers.util import abort, redirect
from sparta.lib import app_globals as g
from sparta.lib.base import BaseController, render
from sparta.model import * 

from sqlalchemy.engine.base import RowProxy
from datetime import *
import time as modtime
import hashlib
from AutoPages import *

#차트 모듈
from pychart import *
#웹 칼라 값을 RGB로 변환해주는 모듈 (webcolors 1.3)
from webcolors import *

#스파르타에 정의된 오류 코드를 임포트한다.
from sparta_error_code import *

log = logging.getLogger(__name__)

#모듈의 이름을 구한다.
#이 모듈 이름은 모듈의 파일명이 바뀌어도 모듈 안에서 경로 지정에 문제가 없도록 하기 위해서
#컨트롤러 내부에서 사용한다.
MODULE_NAME = os.path.splitext(os.path.basename(__file__))[0];

#인력관리 모듈 이름을 정의한다.
MODULE_NAME_FOR_RESOURCE = u"manage_resource";


#칼라값을 나타내는 입력 문자열을 RGB 값으로 리턴하는 함수
def ColorStringToRGB(color):

	c = None;
	
	try:	
		c = hex_to_rgb(color);
	except:
		try:
			c = name_to_rgb(color);
		except:
			pass;
			
	if (c == None):
		c = (255, 255, 255);
	
	rgb = (c[0]/255.0, c[1]/255.0, c[2]/255.0);
	
	return rgb;

#SQL Alchemy의 레코드를 파이썬 사전으로 만드는 함수
def MakeDict(rec):
	r = {};
	for key in rec.keys():
		r[str(key)] = rec[key];
		
	return r;

#입력된 객체를 간트차트 모듈에서 필요한 형태의 사전으로 만드는 함수
def MakeScheduleTaskItem(obj, task_id, parent_task_id=None):
	user_name = u"";
	if (hasattr(obj, "UserName") and (obj.UserName is not None)):
		user_name = obj.UserName;
		if (obj.Department is not None):
			user_name = user_name + u" (%s)" % (obj.Department);
	
	percent = 0;
	if (parent_task_id == None):
		percent = 100;

	item = {"Id":task_id, "ParentId":parent_task_id, "IsLeaf":bool(parent_task_id is not None),
		"Idx" : obj.IDX,
		"ProjectName" : obj.ProjectName,
		"Name" : obj.TaskName, 
		"StartDate" : obj.EstStart, 
		"EndDate" : (obj.EstEnd + timedelta(1)) if (obj.EstEnd <> None) else None, 
		"PercentDone" : percent, 
		"Duration" : obj.Duration if (obj.Duration > 0) else 1,
		"Responsible" : user_name,
		"Thumb" : "",
		"EstStart" : obj.EstStart, 
		"EstEnd" : obj.EstEnd, 
		"EstDay" : obj.Duration};

	#테스크의 마지막 상태
	if (hasattr(obj, "TaskStatus") and (obj.TaskStatus is not None)):
		item["TaskStatus"] = obj.TaskStatus;
		#승인된 태스크는 완료율을 100으로 설정한다.
		if (obj.TaskStatus == "OK"):
			item["PercentDone"] = 100;
	else:
		item["TaskStatus"] = "";
	
	#테스크에 대해서 실시한 테이크의 횟수
	if (hasattr(obj, "TakeCount")):
		if (obj.TakeCount is not None):
			item["TakeCount"] = str(obj.TakeCount);
		else:
			item["TakeCount"] = "0";			
	else:
		item["TakeCount"] = "";

	#(샷일 경우에만 해당) 샷의 썸네일 이미지 주소
	if (hasattr(obj, "Thumb")):
		item["Thumb"] = obj.Thumb;
		
	#세부 테스크 요소 정보
	if (hasattr(obj, "TaskElement")):
		item["TaskElement"] = obj.TaskElement;
		
	#작업자 정보
	if (hasattr(obj, "AssignUser")):
		item["AssignedUserId"] = obj.AssignUser;
		
	#작업자 종류 코드
	if (hasattr(obj, "TaskTypeCode")):
		item["TaskTypeCode"] = obj.TaskTypeCode;
		
	#실제 작업 소요 시간 정보
	if (hasattr(obj, "TotalDuration")):
		item["TotalDuration"] = obj.TotalDuration;
		if (hasattr(obj, "TotalDelay")):
			item["Delay"] = obj.TotalDelay;
		else:
			if ((obj.TotalDuration is not None) and (obj.Duration is not None)):
				item["Delay"] = obj.TotalDuration - obj.Duration;
	

	return item;


#전체 프로젝트 진행 상황을 보여주는 간트 차트 화면에 사용할 스케줄 아이템을 만드는 함수
def MakeProjectItem(obj, id):
	user_name = u"";
	if (hasattr(obj, "UserName")):
		user_name = obj.UserName;

	item = {
		"Id":id, "ParentId":None, "IsLeaf":True,
		"Name":obj.ProjectName, 
		"UserName" : obj.UserName,
		"ProjectId":obj.ProjectId,
		"StartDate":obj.EstStart, "EndDate":obj.EstEnd, 
		"PercentDone":0, "Duration":obj.Duration,
		"Responsible":user_name};

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

#입력된 SQL 문을 실행하고 RowProxy 객체를 리턴하는 함수
def RunSql(sql, conn=None):
	#데이터베이스 연결 객체
	if (conn == None):
		conn = Base.metadata.bind.connect();
		
	return conn.execute(sql);
	
#입력된 파이썬 객체를 JSON 문자열로 만들어서 리턴하는 함수
def JSON(obj):
	return json.dumps(obj=obj, skipkeys=True, encoding='utf8', ensure_ascii=False, cls=RowProxyJsonEncoder);

#입력된 오류 코드와 메시지, 연관 데이터를 JSON 형태로 리턴하는 함수
def Error(code = SPT_ERROR_UNKNOWN, msg = ""):
	return JSON({"ErrorCode" : code, "ErrorMsg" : msg});

#작업이 정상적으로 끝난 경우, 결과를 JSON 형태로 리턴하는 함수
def Success(result = None):
	return JSON({"ErrorCode" : SPT_ERROR_NONE, "ErrorMsg" : "", "Result" : result});





#테이크에 대한 피드백을 남기는 함수
def LeaveFeedbackForTake(take_id, contents, user_id, conn=None):

	params = dict();
	params["take_id"] = take_id;
	params["contents"] = contents;
	params["user_id"] = user_id;
	
	sql = u"""
		INSERT INTO
			TakeReply 
			(ParentIDX, Content, CreateBy)
		VALUES
			(%(take_id)s, '%(contents)s', '%(user_id)s')		
		""" % params;
		
	return RunSql(sql, conn);
	
#메시지를 전송하는 메서드
def SendMessageForUser(receiver, sender, subject, contents):
	r = RunSql(u"""
		INSERT INTO 
			MsgInBox (
				Receiver, 
				Sender, 
				SenderName, 
				Subject, 
				Content,
				CreateDate) 
		SELECT
			'%s', 
			'%s', 
			Name, 
			'%s', 
			'%s',
			NOW()
		FROM
			User
		WHERE
			UserID = '%s'
		""" % (receiver, sender, subject, contents, sender));
		
	return (r.rowcount > 0);

	
#테이크에 대한 정보를 리턴하는 함수	
def GetTakeInfo(take_id):

	#SQL 문에 사용할 파라메터를 만든다.
	sql_params = dict();
	sql_params["take_id"] = take_id;

	sql = u"""
		SELECT
			Project.Name AS ProjectName,
			Project.IDX AS ProjectID,
			Shot.Name AS ShotName,
			Shot.IDX AS ShotID,
			Task.TaskTypeName,
			Task.Element AS TaskElement,
			Task.IDX AS TaskID,
			User.Name AS UserName,
			User.UserID AS UserID,
			Take.Path,
			Take.Preview,
			Take.CreateDate,
			Take.Content
		FROM
			(SELECT
				*
			FROM
				Take
			WHERE
				Take.IDX = %(take_id)s) AS Take
		JOIN
			Project
		ON
			Take.Parent1 = Project.IDX
		JOIN
			Shot
		ON
			Take.Parent3 = Shot.IDX
		JOIN
			(SELECT
				Task_Type.Name AS TaskTypeName,
				Task.*
			FROM
				Task
			JOIN
				Task_Type
			ON
				Task.TypeCode = Task_Type.Code) AS Task
		ON
			Take.Parent4 = Task.IDX
		LEFT OUTER JOIN
			User
		ON
			Take.Confirmer = User.UserID
	""" % (sql_params);
	
	r = RunSql(sql).fetchone();
	
	return MakeDict(r);
	
#테이크에 대한 피드백을 리턴하는 함수
def GetFeedbackOfTake(take_id):

	#SQL 문에 사용할 파라메터를 만든다.
	sql_params = dict();
	sql_params["take_id"] = take_id;

	sql = u"""
		SELECT
			TakeReply.*,
			User.Name AS UserName,
			User.UserID
		FROM
			TakeReply
		LEFT OUTER JOIN
			User
		ON
			TakeReply.CreateBy = User.UserID
		WHERE
			ParentIDX = %(take_id)s
		ORDER BY
			TakeReply.CreateDate DESC
	""" % (sql_params);
	
	feedbacks = RunSql(sql);
	
	#피드백 목록을 저장할 리스트 객체를 생성한다.
	r = list();
	
	for feedback in feedbacks:
		r.append(MakeDict(feedback));
		
	return r;


#테이크를 승인 또는 재작업 요청하는 함수
def ConfirmOrRejectTake(take_id, is_confirm, feedback, user_id, send_message = False):

	#SQL 문에 사용할 파라메터를 만든다.
	sql_params = dict();
	sql_params["take_id"] = take_id;
	sql_params["user_id"] = user_id;
	sql_params["take_state"] = 'OK' if is_confirm == True else 'RTK';
	sql_params["task_state"] = 'DON' if is_confirm == True else 'ACT';
	sql_params["shot_state"] = 'CRQ' if is_confirm == True else 'ACT';
	
	sql = u"""
		UPDATE
			Take
		SET
			StatCode = '%(take_state)s',
			Confirmer = '%(user_id)s'
		WHERE
			IDX = %(take_id)s
	""" % (sql_params);
	
	#SQL을 실행한다.
	rows = RunSql(sql);
	
	#테이크의 상태를 변경하는데 문제가 있을 경우, 오류를 리턴한다.
	if (rows.rowcount <= 0):
		return SPT_ERROR_TAKE_ID_UNKNOWN;
		
	#리테이크가 아닌 경우에만 테스크와 샷의 상태를 갱신한다.
	if (is_confirm == True):
	
		#승인된 테이크의 테스크 상태도 완료로 설정한다.
		sql = u"""
			UPDATE 
				Task 
			SET
				StatCode = '%(task_state)s'
			WHERE 
				IDX = (SELECT Parent4 AS TaskID FROM Take WHERE IDX = %(take_id)s)
		""" % (sql_params);
	
		RunSql(sql);
	
		#승인된 테이크의 샷을 구성하는 테스크들이 모두 완료되었다면, 샷을 컨펌 요청 상태로 만든다.
		sql =u"""
			UPDATE
				Shot 
			SET
				StatCode = '%(shot_state)s'
			WHERE
				IDX = (SELECT Parent3 AS ShotID FROM Take WHERE IDX = %(take_id)s) AND
				(SELECT SUM(IF(StatCode <> 'DON',1,0)) AS UncompletedTaskCount	FROM Task WHERE Parent3 = (SELECT Parent3 AS ShotID FROM Take WHERE IDX = %(take_id)s)) = 0
		""" % (sql_params);
	
		RunSql(sql);
	
	#피드백이 있을 경우
	if (feedback <> None):
		
		#테이크에 대한 피드백이 있다면, 피드백을 남긴다.
		LeaveFeedbackForTake(take_id, feedback, user_id);
		
	#작업자에게 메시지 보내기를 원할 경우
	if (send_message == True):
	
		#테이크에 대한 정보를 얻는다.
		take_info = GetTakeInfo(take_id);
		
		#테이크에 대한 피드백을 얻는다.
		take_feedbacks = GetFeedbackOfTake(take_id);

		title = u"""%(ProjectName)s / %(ShotName)s / %(TaskTypeName)s(%(TaskElement)s) 작업이 """ % (take_info);
		if (is_confirm):
			title = title + u"승인되었습니다.";
		else:
			title = title + u"재 작업 요청되었습니다.";
		
		
		feedback_msg = u"<<피드백>>========================<br>";
		
		for f in take_feedbacks:
			feedback_msg += u"%(CreateDate)s - %(UserName)s(%(UserID)s) > %(Content)s <br>" % (f);
		
		msg = title + u"<br><br><br>" + feedback_msg;
		
		#메시지를 보낸다.
		SendMessageForUser(take_info["UserID"], user_id, title, msg);
	
	return SPT_ERROR_NONE;

#테이크를 승인하는 함수
def ConfirmTake(take_id, feedback, user_id):
	return ConfirmOrRejectTake(take_id, True, feedback, user_id);

#테이크를 리테이크 요청하는 함수
def RequestRetakeOfTake(take_id, feedback, user_id):
	return ConfirmOrRejectTake(take_id, False, feedback, user_id);


#샷의 상태 코드와 이름을 사전 형태로 리턴하는 함수
def GetShotStateList():
	#샷 상태 목록을 구한다.
	shot_states = RunSql(u"""
		SELECT
			Code,
			Name AS ShotStateName
		FROM
			Shot_Stat
		ORDER BY
			Name
		""");
		
	r = dict();
	
	for state in shot_states:
		r[state.Code] = state.ShotStateName;

	return r;

#샷을 입력된 조건에 맞춰서 사전 형태로 리턴하는 메서드
def GetShotList(project_id_list, user_id_list, shot_id_list, shot_state_list, shot_name, start=None, limit=None):

	conn = Base.metadata.bind.connect();
	
	project_id = id;
	user_id = u"";
	
	#SQL문에서 사용될 파라메터들을 저장할 사전을 생성한다.
	sql_params = dict();
	
	#샷, 태스크, 테이크 필터링을 위한 조건들을 저장할 리스트를 생성한다.
	shot_filters = list();
	task_filters = list();
	take_filters = list();
	

	sql_params["user_shot_join"] = u"";	
	sql_params["user_task_join"] = u"";	
	sql_params["user_take_join"] = u"";	
	
	
	#특정 사람으로 필터링을 원하는 경우
	if (user_id_list <> None):
		if (len(user_id_list) > 0):
			user_join_sql = u"""
				JOIN
					(SELECT 
						Parent3 AS ShotId 
					FROM 
						Task 
					WHERE 
						AssignUser IN(%s) 
					GROUP BY ShotId
					) AS UserShotFilter
				ON
					(UserShotFilter.ShotId = %s)
				""";
	
			user_ids =	"'"+"','".join(map(str, user_id_list))+"'";		
			sql_params["user_shot_join"] = user_join_sql % (user_ids, u"Shot.IDX");
			sql_params["user_task_join"] = user_join_sql % (user_ids, u"Parent3");
			sql_params["user_take_join"] = user_join_sql % (user_ids, u"Parent3");
		
		
	#특정 프로젝트로 필터링을 원하는 경우
	if (project_id_list <> None):
		if (len(project_id_list) > 0):
			project_ids = ",".join(map(str, project_id_list));
			shot_filters.append(u"Shot.Parent1 IN(%s)" % project_ids);
			task_filters.append(u"Task.Parent1 IN(%s)" % project_ids);
			take_filters.append(u"Take.Parent1 IN(%s)" % project_ids);
		

	#특정 샷에 대한 정보를 요청하는지 확인한다.
	if (shot_id_list <> None):	
		if (len(shot_id_list) > 0):
			shot_ids = ",".join(map(str, shot_id_list));
			shot_filters.append(u"Shot.IDX IN(%s) " % (shot_ids));
			task_filters.append(u"Task.Parent3 IN(%s) " % (shot_ids));
			take_filters.append(u"Take.Parent3 IN(%s) " % (shot_ids));
		

	#샷의 상태에 따른 필터링을 요청하는지 확인한다.
	if (shot_state_list <> None):
		if (len(shot_state_list) > 0):
			shot_filters.append(u"Shot.StatCode IN('%s') " % ("','".join(shot_state_list)));
			
	
	#샷의 이름으로 필터링하기를 원하는지 확인한다.
	if (shot_name <> None):
		if (len(shot_name) > 0):
			#필터링 조건을 만든다.
			shot_filters.append(u"Shot.Name LIKE '"+shot_name.replace("*", "%%")+"'");
			
	
	sql_params["shot_page_filter"] = u"";
	
	
	#페이징 요청이 있는지 확인한다.
	if ((start <> None) and (limit <> None)):
		sql_params["shot_page_filter"] = u" LIMIT %s, %s " % (start, limit); 


	#샷, 테스크, 테이크 별로 필터링 조건문을 만든다.
	sql_params["shot_filter"] = u" WHERE "+u" AND ".join(shot_filters) if (len(shot_filters) > 0) else u"";
	sql_params["task_filter"] = u" WHERE "+u" AND ".join(task_filters) if (len(task_filters) > 0) else u"";
	sql_params["take_filter"] = u" WHERE "+u" AND ".join(take_filters) if (len(take_filters) > 0) else u"";



	#샷 목록을 구한다.
	sql = u"""
		SELECT SQL_CALC_FOUND_ROWS 
			Project.Name AS ProjectName,
			Shot.IDX, 
			Shot.Name AS TaskName,
			Shot.Thumb,
			Shot.Code, 
			Shot.ShotStatus AS TaskStatus,
			Task.EstStart, 
			Task.EstEnd, 
			Task.Duration,
			Task.TotalDuration,
			Task.TotalDelay
		FROM 
			(SELECT
				Shot.IDX,
				Shot.Parent1,
				Shot.Name,
				Shot.Thumb,
				Shot.Code,
				Shot.StatCode,
				Shot_Stat.Name AS ShotStatus
			FROM
				Shot
			LEFT OUTER JOIN
				Shot_Stat
			ON
				(Shot.StatCode = Shot_Stat.Code)
			%(user_shot_join)s
			%(shot_filter)s
				) AS Shot
		LEFT OUTER JOIN 
			(SELECT 
				Parent1 as ProjectID, 
				Parent3 as ShotID, 
				DATE(MIN(EstStart)) as EstStart, 
				DATE(MAX(ADDDATE(Task.EstStart, INTERVAL IF(Task.EstDay <= 0, 0, Task.EstDay) Day))) EstEnd, 
				SUM(Task.EstDay) as Duration,
				SUM(IF(TaskSum.FirstTake >= Task.EstStart, DATEDIFF(TaskSum.FirstTake, Task.EstStart), 0)+TaskSum.Duration) AS TotalDuration,
				SUM(IF(TaskSum.FirstTake >= Task.EstStart, DATEDIFF(TaskSum.FirstTake, Task.EstStart), 0)+TaskSum.Duration - Task.EstDay) AS TotalDelay
			FROM 
				(SELECT
					IDX,
					Parent1,
					Parent3,
					EstStart,
					EstDay
				FROM
					Task 
				%(user_task_join)s
				%(task_filter)s) AS Task
			LEFT OUTER JOIN
				(SELECT
					Take.Parent4 AS TaskId, 
					DATEDIFF(MAX(CreateDate), MIN(CreateDate))+1 AS Duration,
					DATE(MIN(CreateDate)) AS FirstTake
				FROM
					(SELECT
						Parent4,
						CreateDate
					FROM
						Take
					%(user_take_join)s
					%(take_filter)s
					ORDER BY
						IDX DESC) AS Take
				GROUP BY 
					TaskId
				ORDER BY 
					TaskId) as TaskSum
			ON
				(Task.IDX = TaskSum.TaskId)
			%(user_task_join)s
			%(task_filter)s
			GROUP BY
				Task.Parent3
			ORDER BY
				EstStart) AS Task 
		ON 
			Shot.IDX = Task.ShotID 
		JOIN
			(SELECT
				IDX,
				Name
			FROM
				Project
			) AS Project
		ON
			Shot.Parent1 = Project.IDX
		%(shot_filter)s 
		ORDER BY
			Project.Name,
			Shot.Name
		%(shot_page_filter)s	
	"""%(sql_params);
	
	#print "SHOT sql ====>\n",sql;
	shots = RunSql(sql, conn);
	
	#LIMIT 를 하지 않았을때의 총 샷의 수를 얻는다.
	total_shot_count = RunSql(u"SELECT FOUND_ROWS() AS TotalRows", conn).fetchone().TotalRows;
	
	#print "총 샷의 수 = ", total_shot_count;

	#print "shot count = [%d]" % (shots.rowcount);

	#태스크 목록을 구한다.
	sql = u"""
		SELECT
			Task.IDX, 
			Task.Name, 
			Shot.ProjectName,
			Shot.ShotName,
			DATE(Task.EstStart) AS EstStart, 
			DATE(ADDDATE(Task.EstStart, INTERVAL IF(Task.EstDay <= 0, 0, Task.EstDay-1) Day)) AS EstEnd, 
			Task.EstDay AS Duration, 
			Task.Parent3 AS ShotId, 
			Task.AssignUser, 
			Task.Parent1 AS ProjectId, 
			User.Name AS UserName, 
			User.OfficeName, 
			User.Department,
			Task_Type.Name AS TaskName,
			Task.Element AS TaskElement,
			TaskStatus.TakeCount,
			TaskStatus.TakeStatus AS TaskStatus,
			Task.TypeCode AS TaskTypeCode,
			TaskStatus.FirstTake,
			TaskStatus.Duration AS TakeDuration,
			IF(TaskStatus.FirstTake >= Task.EstStart, DATEDIFF(TaskStatus.FirstTake, Task.EstStart), 0)+TaskStatus.Duration AS TotalDuration
		FROM 
			(SELECT 
				*
			FROM
				Task 
			%(user_task_join)s
			%(task_filter)s
				) AS Task

		LEFT OUTER JOIN 
			(SELECT
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
			Task_Type
		ON
			(Task.TypeCode = Task_Type.Code)

		LEFT OUTER JOIN
			(SELECT
				Take.TakeId, 
				Take.TakeCode, 
				Take_Stat.Name as TakeStatus, 
				Take.TaskId, 
				COUNT(Take.TakeId) as TakeCount,
				DATEDIFF(MAX(CreateDate), MIN(CreateDate))+1 AS Duration,
				DATE(MIN(CreateDate)) AS FirstTake
			FROM
				(SELECT 
					IDX as TakeId,
					Code as TakeCode,
					Parent4 as TaskId,
					StatCode,
					CreateDate
				FROM 
					Take 
				%(user_take_join)s
				%(take_filter)s
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
				Project.Name AS ProjectName,
				Shot.IDX,
				Shot.Name AS ShotName
			FROM
				Shot
			%(user_shot_join)s
			JOIN
				(SELECT
					IDX,
					Name
				FROM
					Project
				) AS Project
			ON
				(Shot.Parent1 = Project.IDX)
			%(shot_filter)s
			ORDER BY
				Project.Name,
				Shot.Name
			%(shot_page_filter)s
			) AS Shot
		ON
			(Task.Parent3 = Shot.IDX)
		%(user_task_join)s
		%(task_filter)s

		ORDER BY
			Shot.ProjectName,
			Shot.ShotName,
			EstStart,
			Task.Name,
			EstEnd,
			UserName
	"""%(sql_params);
	
	#print "TASK SQL ===>\n",sql;
	tasks = RunSql(sql, conn);
	
	#print "task count = [%d]" % (tasks.rowcount);

	r = [];
	
	task_index = 0;
	need_fetch_task = True;

	#작업을 식별할 수 있는 아이디
	item_id = u"";

	#태스크의 부모 태스크의 아이디
	parent_item_id = u"";
	
	#작업이 할당된 기간의 최소와 최대 값을 저장할 변수
	min_date = datetime.datetime.now().date();
	max_date = datetime.date(1970,1,1);

	for shot in shots:
		#새로 할당할 태스크 아이디를 샷 태스크 아이디로 사용한다.
		#이후에 태스크가 결과 리스트에 추가될때 이 shot_task_id 를 부모 태스크 아이디로 사용한다.
		parent_item_id = u"S%d" % (shot.IDX);

		#샷을 결과 리스크에 추가한다.
		shot_item = MakeScheduleTaskItem(shot, parent_item_id);
		r.append(shot_item);
		
		task_count = 0;
		
		while True:
			if (task_index >= tasks.rowcount):
				break;
				
			#테스크 하나를 얻는다.
			if (need_fetch_task):
				task = tasks.fetchone();
				need_fetch_task = False;

			#태스크의 샷 아이디가 현재 샷의 아이디와 다를 경우, 현재 샷에 태스크를 추가하면
			#안되기 때문에 루프를 종료한다.
			if (task.ShotId <> shot.IDX):
				break;
				
			item_id = u"T%d" % (task.IDX);

			#태스크를 결과 리스트에 추가시킨다.
			r.append(MakeScheduleTaskItem(task, item_id, parent_item_id));
			task_count += 1;

			#다음 태스크를 얻기 위해서 인덱스를 1증가시킨다.
			task_index = task_index+1;
			need_fetch_task = True;
			
			if (task.EstStart != None):
				if (task.EstStart < min_date):
					min_date = task.EstStart;

			if (task.EstEnd != None):					
				if (task.EstEnd > max_date):
					max_date = task.EstEnd;

	#print "schedule item count is = [%d]"%len(r)
	
	conn.close();
	
	#print "RESULT ====>\n", JSON({"TotalRowCount" : total_shot_count, "Rows" : r});
	#print "Duration === >\n", min_date,max_date;
	
	if (min_date > max_date):
		max_date = min_date;
	
	#결과를 리턴한다.
	return {"TotalRowCount" : total_shot_count, "MinDate" : min_date, "MaxDate" : max_date, "Rows" : r};

# 하이픈(-)으로 구분된 문자열 날짜를 받아 datetime.date 형태로 반환한다.
def convertDate(strDate):
	sdt_split = [int(item) for item in strDate.split("-")]
	return date(sdt_split[0], sdt_split[1], sdt_split[2])

def getTaskFromTask(task_idx, order = "next"):
	conn = Base.metadata.bind.connect()
	
	tasks = None
	
	if "next" == order:
		# 변경을 요청받은 샷을 기준으로 뒤쪽으로 있는 샷 가져오기
		# 구 쿼리
		# select IDX, date(EstStart) as StartDate, date(EstDue) as EndDate, TypeCode from Task t1 where date(t1.EstStart) >= (select date(t2.EstStart) from Task t2 where t2.idx=%d and t1.Parent3 = t2.parent3) order by t1.estStart asc
		# 새 쿼리 설명
		# 변경요청을 받은 태스크 뒤에 있는 태스크 목록을 가져오는 쿼리(단, 변경요청을 받은 태스크의 종료일 이후에 있는 태스크를 가져온다) - 자기자신을 제외한다.
		tasks = RunSql(u"select idx, date(eststart) as StartDate, date(date_add(eststart, interval estday day)) as EndDate, TypeCode from Task t1 where date(date_add(eststart, interval estday day)) >= (select date(date_add(eststart, interval estday day)) from Task t2 where t2.idx=%(idx)d and t1.Parent3 = t2.parent3) and t1.idx != %(idx)d order by t1.estStart asc" % {"idx":task_idx}, conn)
	elif "prev" == order:
		# 변경을 요청받은 샷을 기준으로 앞쪽으로 있는 샷 가져오기
		# 구 쿼리
		# select IDX, date(EstStart) as StartDate, date(EstDue) as EndDate, TypeCode from Task t1 where date(t1.EstStart) <= (select date(t2.EstStart) from Task t2 where t2.idx=%d and t1.Parent3 = t2.parent3) order by t1.estStart asc
		# 새 쿼리 설명
		# 변경요청을 받은 태스크 앞에 있는 태스크 목록을 가져오는 쿼리(단, 변경요청을 받은 태스크의 시작일 이전에 있는 태스크를 가져온다) - 자기자신을 제외한다.
		tasks = RunSql(u"select idx, date(eststart) as StartDate, date(date_add(eststart, interval estday day)) as EndDate, TypeCode from Task t1 where date(date_add(eststart, interval estday day)) <= (select date(eststart) from Task t2 where t2.idx=%(idx)d and t1.Parent3 = t2.Parent3) and t1.idx != %(idx)d order by t1.estStart asc" % {"idx":task_idx}, conn)
		
	return tasks

# 태스크 유닛 콘텍스트 메뉴를 불러오기 위해서만 사용한다.
def task_unit_context_menu():
	# SQL 실행을 위해서 데이터베이스에 연결을한다.
	conn = Base.metadata.bind.connect();
	
	# Task Unit SQL Load
	sql = u"SELECT idx, unit_name, unit_kind FROM Task_Unit"
	
	#컨펌 대상 샷 목록을 구한다.
	task_unit_list = RunSql(sql, conn);
	
	task_units = []
	for unit in task_unit_list:
		task_units.append(
			{
				"data": unit.idx,
				"text": unit.unit_name
			}
		)
	
	return JSON(task_units)

#일정 관리 컨트롤러
#프로젝트 단위나 전체 진행중인 프로젝트의 일정을 관리하는 컨트롤러
class ManageScheduleController(BaseController):

	def index(self):
		#스파르타 상단의 사용자 정보에 사용자 아이디와 메일 수신 아이콘 처리 및 로그인 검사하는 함수
		autoFrameData(c, "/"+MODULE_NAME);
		
		c.firstLoadPage  = url(controller=MODULE_NAME, action="view_all_active_project_schedule_overview")
		c.firstLoadTitle = u"전체 프로젝트 진행 현황"
		c.leftAccordPanels = u"""[{title:"Navigation",url:"/%s/menu",rootText:"All Tasks",iconCls:"icon-folder-go"}]"""%(MODULE_NAME)
		return render("frame.html");



	#전체 프로젝트 일정 현황을 보여주는 메서드
	def view_all_active_project_schedule_overview(self):
	
		#현재 진행중인 프로젝트 일정 현황 데이터를 리턴하는 주소를 만든다.
		c.data_url = url(controller=MODULE_NAME, action=u"get_all_active_project_schedule_overview_data_as_json");

		#전체 프로젝트 일정 현황에서 특정 행을 더블 클릭했을때, 해당 프로젝트 일정으로 이동할 주소를 설정한다.
		c.view_specific_project_url = url(controller=MODULE_NAME, action=u"view_specific_project_schedule");
		
		#특정 프로젝트의 요약 정보를 보여주는 화면 주소를 설정한다.
		c.view_specific_project_overview_url = url(controller=MODULE_NAME, action=u"view_project_overview");
		
		#전체 프로젝트의 시작 및 끝 기간을 설정한다.
		#간트뷰에서 표시할때, 시작 시간을 DB에서 구한 값으로 설정하면, 시작일에 있는 프로젝트가 보기 좋지 않기 때문에,
		#시작일을 1달 정도 이전으로 설정한다. (일정표 앞쪽에 프로젝트명을 출력하기 때문)
		period = self.get_active_project_period();
		c.start_date = str(period["StartDate"] - timedelta(30));
		c.end_date = str(period["EndDate"]);
		
		#샷의 상태 목록을 얻는다.
		shot_states = RunSql(u"""
			SELECT
				Code,
				Name AS StateName,
				BGColor,
				Color,
				IconCls
			FROM
				Shot_Stat
			ORDER BY
				Code
			""");
			
		shot_state_data_fields = [{"name" : u"SHOT_COUNT", "type" : "int"}];

		shot_state_columns = [{"header" : "샷의 수", "sortable" : True, "dataIndex" : "SHOT_COUNT", "locked" : True, "width" : 60, "align" : "center", "hidden" : False}];

		shot_state_and_color = {};
		shot_state_fields = [];
		
		for state in shot_states:
			shot_state_data_fields.append({"name" : u"%s_COUNT" % state.Code, "type" : "int"});
			shot_state_columns.append({"header" : state.StateName, "sortable" : True, "dataIndex" : "%s_COUNT" % state.Code, "locked" : True, "width" : 60, "align" : "center", "hidden" : True});
			shot_state_and_color[u"%s_COUNT"%state.Code] = {"background_color" : state.BGColor, "color" : state.Color, "state_name" : state.StateName};
			shot_state_fields.append(u"%s_COUNT" % state.Code);
			
		c.shot_state_data_fields = JSON(shot_state_data_fields);
		c.shot_state_columns = JSON(shot_state_columns);
		c.shot_state_fields = JSON(shot_state_fields);
		c.shot_state_and_color = JSON(shot_state_and_color);
		
		return render("view_all_active_project_schedule_overview.html");		

	#로그인한 사용자에게 할당된 개인 작업 일정을 보여주는 메서드
	def view_my_task_schedule(self):
		
		#로그인한 사용자 아이디를 얻는다.
		user_id = session["UserID"];
		#user_id = "zelda";
		
		#사용자에게 할당된 작업 일정 데이터를 리턴하는 주소를 설정한다.
		c.user_task_schedule_data_url = url(controller=MODULE_NAME, action=u"get_user_task_schedule_data_as_json", id=user_id);

		#한 페이지에 출력할 샷의 수를 설정한다.
		c.shot_count_per_page = 30;
		
		return render("view_user_task_schedule.html");
		

	#입력된 프로젝트 아이디에 해당하는 프로젝트 일정 현황을 보여주는 메서드
	def view_specific_project_schedule(self, id):
		
		#프로젝트 일정 데이터를 리턴하는 주소를 설정한다.
		c.data_url = url(controller=MODULE_NAME, action=u"get_specific_project_schedule_data_as_json", id=id);
		
		#할당 가능한 작업의 종류 목록을 설정한다.
		c.task_type_list = self.get_task_types();
		
		#프로젝트 정보를 설정한다.
		c.project_info = self.get_project_info_as_json(id);
		
		#한 페이지에 출력할 샷의 수를 설정한다.
		c.shot_count_per_page = 30;
		
		#필터링할 샷의 상태
		c.shot_state_list_for_filter = JSON([]);

		#샷의 상태 목록
		c.shot_state_list = JSON(GetShotStateList());
		
		
		#프로젝트에서 특정 씬을 수행했던 작업자 목록을 리턴하는 주소를 설정한다.
		c.users_worked_on_specific_scene_of_project_url = url(controller=MODULE_NAME, action=u"get_users_worked_on_specific_scene_of_project_as_json");
		
		#특정 사용자에게 할당된 작업 목록을 리턴하는 주소를 설정한다.
		c.user_task_url = url(controller=MODULE_NAME, action=u"get_user_task_as_json");
		
		#작업의 생성/삭제/변경을 하기 위한 주소를 설정한다.
		c.create_delete_update_task_url = url(controller=MODULE_NAME, action=u"create_delete_update_task");
		
		#사용자가 처리할 수 있는 작업에 관한 정보를 얻기 위한 주소를 설정한다.
		c.user_roles_url = url(controller=MODULE_NAME, action=u"get_user_roles_as_json");
		
		#특정 프로젝트에 참여한 인력들의 역할표를 보여주는 주소
		c.worker_and_role_of_project_url = url(controller=MODULE_NAME_FOR_RESOURCE, action=u"view_resource_role");
		
		#특정 프로젝트에 참여한 인력들의 일정표를 보여주는 주소
		c.worker_schedule_url = url(controller=MODULE_NAME_FOR_RESOURCE, action=u"view_all_resource_schedule_overview", id=id);
		
		# 스케쥴 콘텍스트 메뉴에 추가로 사용할 콘텍스트 메뉴만 리턴
		c.schedule_context_menu = task_unit_context_menu()
		
		return render("view_specific_project_schedule.html");

	#특정 프로젝트에 대한 개괄 정보를 보여주는 메서드
	def view_project_overview(self, id):
		project_info = self.get_project_info(id);
		c.project_info = project_info;
		c.shot_status_pie_chart_url = url(controller=MODULE_NAME, action="get_shot_state_statics_image_of_project", id=id);
		c.project_schedule_url = url(controller=MODULE_NAME, action="view_specific_project_schedule", id=id);
		
		c.url_shot_statics = url(controller=MODULE_NAME, action="get_shot_state_statics_of_project_as_json", id=id);
		#감독에게 컨펌 요구를 하기 위해 샷을 보여주는 화면 주소
		c.url_view_shots_for_confirm = url(controller=MODULE_NAME, action="view_shots_for_request_confirm", id=id);
		
		return render("view_project_overview.html");
		
		

	#컴펌을 위한 샷 정보를 보여주는 메서드
	def view_shots_for_request_confirm(self, id):
	
		#컨펌할 샷 데이터를 로드하는 주소
		c.url_shot_list_for_reuqest_confirm = url(controller=MODULE_NAME, action="get_shots_for_request_confirm", id=id);
		c.url_shot_statics = url(controller=MODULE_NAME, action="get_shot_state_statics_of_project_as_json", id=id);
		
		#테이크를 컴펌/리테이크 요청할때 사용하는 주소
		c.url_confirm_or_reject_take = url(controller=MODULE_NAME, action="confirm_or_reject_take");
		
		#한페이지에 표시할 샷의 수
		c.shot_count_per_page = 20;
		
		#샷의 상태별 필터를 초기화한다.
		c.shot_state_list_for_filter = "[]";
		
		#특정 상태의 샷만 보기를 원한다면
		if (request.params.has_key("shot_state_list")):
			c.shot_state_list_for_filter = JSON(request.params["shot_state_list"]);
		
		#샷의 상태 목록
		c.shot_state_list = JSON(GetShotStateList());
		
		return render("view_shots_for_request_confirm.html");

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
			"taburl" : u"/%s/view_all_active_project_schedule_overview/"%(MODULE_NAME),
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
				#"taburl" : u"/%s/view_specific_project_schedule/%d"%(MODULE_NAME, project.IDX), 
				"taburl" : u"/%s/view_project_overview/%d"%(MODULE_NAME, project.IDX),			
				"title" : u"[%s] 일정"%project.ProjectName,
				"load_in_maintab" : True});

		project_root_menu["children"] = project_menu;
		
		r = [project_root_menu];
		
		#결과를 JSON 형태로 변환해서 리턴한다.
		return JSON(r);


	#현재 진행중인 프로젝트들의 가장 빠른 작업일과 가장 늦은작업일을 리턴하는 메서드
	def get_active_project_period(self):
	
		period = RunSql(u"""
			SELECT 
				MIN(TaskSchedule.EstStart) AS StartDate,
				MAX(TaskSchedule.EstEnd) AS EndDate
			FROM
				Project
			LEFT OUTER JOIN
				(SELECT 
					Parent1 as ProjectID, 
					DATE(MIN(EstStart)) as EstStart, 
					DATE(MAX(ADDDATE(Task.EstStart, INTERVAL IF(Task.EstDay <= 0, 0, Task.EstDay-1) Day))) EstEnd, 
					SUM(Task.EstDay) as Duration 
				FROM 
					Task 
				GROUP BY 
					ProjectID) as TaskSchedule
			ON
				(Project.IDX = TaskSchedule.ProjectID)
			WHERE
				Project.StatCode = 'ACT'
			""").fetchone();
			
		r = {"StartDate" : period.StartDate, "EndDate" : period.EndDate};
		return r;		
		
	#특정 프로젝트에 대한 정보를 JSON으로 리턴하는 메서드
	def get_project_info_as_json(self, id):
		return JSON(self.get_project_info(id));

	#특정 프로젝트에 대한 정보를 사전형태로 리턴하는 메서드
	def get_project_info(self, id):
		try:
			#실행할 SQL 문에 인수로 전달할 항목을 만든다.
			sql_params = dict();			
			sql_params["project_id"] = id;
			
			#프로젝트 정보를 구한다.
			project = RunSql(u"""
				SELECT 
					Project.IDX AS ProjectID, 
					Project.Name AS ProjectName,
					Project.Thumb AS ProjectThumb,
					Project.Code AS ProjectCode,
					Project.Content AS Content,
					Project.DeadLine AS DeadLine,
					Shot.ShotCount,
					TaskSchedule.EstStart AS StartDate,
					TaskSchedule.EstEnd AS EndDate,
					TaskSchedule.Duration
				FROM 
					Project
				JOIN
					(SELECT
						Parent1,
						COUNT(IDX) AS ShotCount
					FROM
						Shot
					WHERE
						Parent1 = %(project_id)s
					GROUP BY
						Parent1) AS Shot
				ON
					Project.IDX = Shot.Parent1
				LEFT OUTER JOIN
					(SELECT 
						Parent1 as ProjectID, 
						DATE(MIN(EstStart)) as EstStart, 
						DATE(MAX(ADDDATE(Task.EstStart, INTERVAL IF(Task.EstDay <= 0, 0, Task.EstDay-1) Day))) EstEnd, 
						SUM(Task.EstDay) as Duration 
					FROM 
						Task 
					WHERE
						Parent1 = %(project_id)s
					GROUP BY 
						ProjectID) as TaskSchedule
				ON
					(Project.IDX = TaskSchedule.ProjectID)
					
				WHERE
					Project.IDX = %(project_id)s
				""" % sql_params).fetchone();
			
			r = MakeDict(project);
		except:
			r= {};
			
		return r;

	#입력된 사용자 아이디에 할당한 작업 스케쥴 데이터를 JSON으로 리턴하는 메서드
	def get_user_task_schedule_data_as_json(self, id):
		
		user_id_list = [id];
		shot_list = None;
		shot_state_list = ['RDY', 'ACT'];
		shot_name = None;
		start = None;
		limit = None;
		
		#특정 샷에 대한 정보를 요청하는지 확인한다.
		if (request.params.has_key("shot_id_list")):
		
			#대상 샷 아이디 목록을 얻는다.
			shot_list = json.loads(request.params["shot_id_list"]);
	
		#샷의 상태에 따른 필터링을 요청하는지 확인한다.
		if (request.params.has_key("shot_state_filter")):
		
			#필터링할 샷의 상태 코드를 얻는다.
			shot_state_list = json.loads(request.params["shot_state_filter"]);
		
		#샷의 이름으로 필터링하기를 원하는지 확인한다.
		if (request.params.has_key("shot_name_filter")):
			
			shot_name = request.params["shot_name_filter"].strip();
				
		#페이징 요청이 있는지 확인한다.
		if (request.params.has_key("start") and request.params.has_key("limit")):
			start = request.params["start"];
			limit = request.params["limit"]; 


		#입력된 조건에 해당하는 샷 목록을 구해서 리턴한다.
		result = GetShotList(None, user_id_list, shot_list, shot_state_list, shot_name, start, limit);
		
		return JSON(result);

	#입력된 프로젝트 아이디에 해당하는 프로젝트 일정을 JSON으로 리턴하는 메서드
	def get_specific_project_schedule_data_as_json(self, id=None):
		
		project_id_list = [id];
		shot_list = None;
		shot_state_list = None;
		shot_name = None;
		start = None;
		limit = None;
		
		#특정 샷에 대한 정보를 요청하는지 확인한다.
		if (request.params.has_key("shot_id_list")):
		
			#대상 샷 아이디 목록을 얻는다.
			shot_list = json.loads(request.params["shot_id_list"]);
	
		#샷의 상태에 따른 필터링을 요청하는지 확인한다.
		if (request.params.has_key("shot_state_filter")):
		
			#필터링할 샷의 상태 코드를 얻는다.
			shot_state_list = json.loads(request.params["shot_state_filter"]);
		
		#샷의 이름으로 필터링하기를 원하는지 확인한다.
		if (request.params.has_key("shot_name_filter")):
			
			shot_name = request.params["shot_name_filter"].strip();
				
		#페이징 요청이 있는지 확인한다.
		if (request.params.has_key("start") and request.params.has_key("limit")):
			start = request.params["start"];
			limit = request.params["limit"]; 


		#입력된 조건에 해당하는 샷 목록을 구해서 리턴한다.
		result = GetShotList(project_id_list, None, shot_list, shot_state_list, shot_name, start, limit);
		
		return JSON(result);


	#전체 진행중인 프로젝트 일정을 JSON으로 리턴하는 메서드
	def get_all_active_project_schedule_overview_data_as_json(self):
		
		#샷 상태 목록을 구한다.
		shot_states = RunSql(u"""
			SELECT
				Code
			FROM
				Shot_Stat
			ORDER BY
				Code
			""");
			
		shot_statics_sql = u"""
			(SELECT
				Parent1 AS ProjectID,
				COUNT(*) AS SHOT_COUNT
			""";
			
		#프로그램으로 만들어진 필드들을 조인된 결과에서 SELECT 하기 위한 변수
		shot_state_fields = [u"SHOT_COUNT"];
		
		for state in shot_states:
			shot_statics_sql += u",SUM(IF(StatCode = '%s', 1, 0)) AS %s_COUNT\n" % (state.Code, state.Code);
			shot_state_fields.append(u"%s_COUNT" % (state.Code));

		shot_statics_sql += u""" 
			FROM
				Shot
			GROUP BY
				Shot.Parent1
			) AS ShotStatics
			""";
		
	
	
		#프로젝트 진행 상황을 구한다.
		sql = u"""
			SELECT 
				Project.IDX as ProjectId,
				Project.Name as ProjectName,
				TaskSchedule.EstStart,
				TaskSchedule.EstEnd,
				TaskSchedule.Duration,
				User.UserName,
				ShotStatics.%s

			FROM
				Project

			LEFT OUTER JOIN
				(SELECT 
					Parent1 as ProjectID, 
					DATE(MIN(EstStart)) as EstStart, 
					DATE(MAX(ADDDATE(Task.EstStart, INTERVAL IF(Task.EstDay <= 0, 0, Task.EstDay) Day))) EstEnd, 
					SUM(Task.EstDay) as Duration 
				FROM 
					Task 
				GROUP BY 
					ProjectID) as TaskSchedule
			ON
				(Project.IDX = TaskSchedule.ProjectID)
			
			LEFT OUTER JOIN
				(SELECT
					IDX,
					UserID,
					Name AS UserName
				FROM
					User) AS User
			ON
				(Project.Manager = User.UserID)
				
			LEFT OUTER JOIN
				%s
			ON
				(Project.IDX = ShotStatics.ProjectID)
					
			WHERE
				Project.StatCode = 'ACT'
			ORDER BY
				Project.Name
		""" % (",ShotStatics.".join(shot_state_fields), shot_statics_sql);
		
		#print u"프로젝트 개요 SQL ==============>\n", sql;
		
		projects = RunSql(sql);

		#결과를 저장할 리스트를 생성한다.
		r = [];

		#간트 차트에서 사용할 아이디
		item_id = 0;

		for project in projects:
			item = MakeProjectItem(project, item_id);
			for s in shot_state_fields:
				item[s] = project[s];
			r.append(item);
			item_id = item_id+1;

		#결과 리스트를 JSON 형태로 변환한다.
		return JSON(r);



	#작업 종류 코드와 명칭 데이터를 리턴하는 메서드
	def get_task_types(self):
		
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
			
		r = [];
		
		for task_type in task_types:
			r.append([task_type.Code, task_type.TaskTypeName]);
			
		#결과 리스트를 JSON 형태로 변환한다.
		return JSON(r);


	#프로젝트에서 특정 씬의 작업을 수행했던 작업자 목록을 JSON 형태로 리턴하는 메서드
	def get_users_worked_on_specific_scene_of_project_as_json(self):
		#print "get_users_worked_on_specific_scene_of_project_as_json >> params = ", request.params;
		
		if (request.params["FilterByScene"] == u"true"):
			scene_filter = u" AND Task.Parent3 IN (SELECT IDX FROM Shot where Parent1=%s and Name LIKE '%s_%%%%')" % (request.params["ProjectID"], request.params["SceneName"]);
		else:
			scene_filter = u"";

		#프로젝트에서 입력된 씬에 해당하는 작업을 수행했던 사용자 목록을 구한다.
		users = RunSql(u"""
			SELECT 
				User.UserIDX,
				User.UserID,
				User.UserName,
				User.UserTeam,
				User.TaskTypeCodes
			FROM 
				(SELECT
					IDX as UserIDX,
					UserID,
					User.Name AS UserName,
					User_Team.Name AS UserTeam,
					TaskTypeCodes
				FROM
					User
				LEFT OUTER JOIN
					(SELECT
						Name,
						Code
					FROM
						User_Team) AS User_Team
				ON
					(User.TeamCode = User_Team.Code)) AS User
	
			JOIN
				(SELECT
					IDX,
					Parent3 AS ShotIDX,
					AssignUser
				FROM
					Task
				WHERE
					Task.AssignUser is not null 
					%s
				) AS Task 
			ON
				(Task.AssignUser = User.UserID)
				
			WHERE
				User.TaskTypeCodes LIKE '%%%%%s,%%%%'
			GROUP BY
				AssignUser
			""" % (scene_filter, request.params["TaskTypeCode"]));
			
	
		r = [];
		index = 1;

		#대상 사용자 아이디를 리스트에 복사하고, 사용자 정보를 결과 배열에 저장한다.
		#RowProxy 클래스가 한번 fetch 하면 처음으로 되돌아 갈수 없기 때문에, 여기서 작업 정보를 얻기 위한 사용자
		#아이디 목록과 결과로 리턴할 사용자 정보를 함께 처리한다.
		s = [];
		for user in users:
			#print "[%d] name = %s (id:'%s'), role = %s" % (user.UserIDX, user.UserName, user.UserID, user.TaskTypeCodes);
			s.append("'"+user.UserID+"'");
			r.append({
				"UserIDX" : user.UserIDX,
				"UserID" : user.UserID,
				"UserName" : user.UserName,
				"UserTeam" : user.UserTeam
			});


		#작업 정보를 보고싶은 시작일 얻는다. 시작일이 없을 경우 오늘로 설정한다.
		if (request.params.has_key("StartDate")):
			start_date = datetime.datetime.strptime(request.params["StartDate"], "%Y-%m-%d").date();
		else:
			start_date = date.today();
			
		#작업 정보를 보고싶은 끝일을 얻는다. 끝 날짜 정보가 없을 경우 시작일로부터 30일 이후를 끝날짜로 설정한다.
		if (request.params.has_key("EndDate")):
			end_date = datetime.datetime.strptime(request.params["EndDate"], "%Y-%m-%d").date();
		else:
			end_date = start_date + timedelta(30-1);
			
		#print u"시작일 = ", start_date;
		#print u"종료일 = ", end_date;
		#print u"대상 사용자 = '", ",".join(s),"'";
		
		#대상 사용자가 없으면 종료한다.
		if (len(s) <= 0):
			return JSON([]);
		
		#대상 사용자들의 작업 할당 정보를 얻는다.
		tasks = RunSql(u"""
			SELECT
				*
			FROM
				(SELECT
					AssignUser,
					DATE(EstStart) AS EstStart, 
					DATE(ADDDATE(EstStart, INTERVAL IF(Task.EstDay <= 0, 0, Task.EstDay) Day)) AS EstEnd
				FROM
					Task) AS Task				
			WHERE
				EstStart >= DATE('%s') AND EstStart <= DATE('%s') AND
				EstEnd >= DATE('%s') AND EstEnd <= DATE('%s') AND
				AssignUser IN (%s)
			ORDER BY
				AssignUser,
				EstStart
			""" % (start_date, end_date, start_date, end_date, ",".join(s)));
		
		user_id = u"";
		
		#사용자별로 작업이 없는 가장 빠른 날짜와 기간을 저장하는 사전
		no_work_period = {};
		found = False;
		sd = start_date;
		
		for task in tasks:
			if (task.AssignUser <> user_id):
				if (user_id <> u""):
					if (found == False):
						#기간 중 후반부에 작업을 할 수 있는 기간이 있을 경우
						if (sd <= end_date):
							d = (end_date - sd).days+1;
							no_work_period[user_id] = {"StartDate" : sd, "EndDate" : sd+timedelta(d-1), "Duration" : d, "CanStartInPeriod" : True};
						#기간 중에 작업할 수 있는 기간이 없는 경우
						else:
							no_work_period[user_id] = {"StartDate" : end_date+timedelta(1), "EndDate" : end_date, "Duration" : 0, "CanStartInPeriod" : False};
				user_id = task.AssignUser;	
				sd = start_date;
				found = False;
			else:
				if (found):
					continue;
				
			if (sd >= task.EstStart):
				sd = task.EstEnd + timedelta(1);
			else:
				d = (task.EstStart - sd).days;
				no_work_period[task.AssignUser] = {"StartDate" : sd, "EndDate" : sd+timedelta(d-1), "Duration" : d, "CanStartInPeriod" : True};
				found = True;
				
		#마지막 작업자에 대한 처리		
		if (found == False):
			if (sd <= end_date):
				d = (end_date - sd).days+1;
				no_work_period[user_id] = {"StartDate" : sd, "EndDate" : sd+timedelta(d-1), "Duration" : d, "CanStartInPeriod" : True};
			else:
				no_work_period[user_id] = {"StartDate" : end_date+timedelta(1), "EndDate" : end_date, "Duration" : 0, "CanStartInPeriod" : False};
		
		#print "작업 없는 날 정보 = ", no_work_period;		
		
		for user_data in r:
			user_id = user_data["UserID"];
			
			#가장 빨리 시작할 수 있는 날짜 정보가 있다면, 해당 날짜를 사용한다.
			if (no_work_period.has_key(user_id) == True):
				p = no_work_period[user_id];
				user_data["StartDate"] = p["StartDate"];
				user_data["EndDate"] = p["EndDate"];
				user_data["Duration"] = p["Duration"];
				user_data["CanStartInPeriod"] = p["CanStartInPeriod"];
			else:
				user_data["StartDate"] = start_date;
				user_data["EndDate"] = end_date;
				user_data["Duration"] = (end_date - start_date).days+1;
				user_data["CanStartInPeriod"] = True;
		
			
		return JSON(r);
		
		
	#특정 사용자에게 할당된 작업 목록을 리턴하는 메서드
	def get_user_task_as_json(self):
		
		#print u"사용자 = [%s]" % (request.params["UserID"]);
		
		#결과에 어떤 데이터를 포함시킬지에 관한 파라메터를 얻는다.
		if (request.params.has_key("Mode")):
			mode = request.params["Mode"];
		else:
			mode = "both";
		
		#print u"모드 = [%s]" % (mode);
		
		#print u"StartDate = [%s]" % (request.params["StartDate"]);
		#print u"EndDate = [%s]" % (request.params["EndDate"]);
		
		#작업 정보를 보고싶은 시작일 얻는다. 시작일이 없을 경우 오늘로 설정한다.
		if (request.params.has_key("StartDate")):
			start_date = datetime.datetime.strptime(request.params["StartDate"], "%Y-%m-%d").date();
		else:
			start_date = date.today();
			
		#작업 정보를 보고싶은 끝일을 얻는다. 끝 날짜 정보가 없을 경우 시작일로부터 30일 이후를 끝날짜로 설정한다.
		if (request.params.has_key("EndDate")):
			end_date = datetime.datetime.strptime(request.params["EndDate"], "%Y-%m-%d").date();
		else:
			end_date = start_date + timedelta(30-1);
			
		#print u"시작일 = ", start_date;
		#print u"종료일 = ", end_date;

		#작업이 없는 날도 결과 데이터에 포함시킬지 여부를 파단한다.
		append_no_work_day = (mode == "both") or (mode == "no_work_day_only"); 
		
		#사용자에게 할당된 작업 정보도 결과 데이터에 포함시킬지 여부를 판단한다.
		append_task = (mode == "both") or (mode == "task_only");
		
		#사용자에게 할당된 작업 목록을 구한다.
		tasks = RunSql(u"""
			SELECT 
				Project.Name as ProjectName,
				Project.IDX as ProjectIDX,
				Task.IDX AS TaskIDX,				
				Task_Type.Name AS TaskName, 
				Task.Element AS TaskElement,
				TaskStatus.TaskStatus,
				Task.EstStart, 
				Task.EstEnd, 
				Task.Duration,
				TaskStatus.TakeCount
	
			FROM 
				(SELECT
					IDX,
					TypeCode,
					Parent1 AS ProjectIDX,
					Element,
					AssignUser,
					DATE(Task.EstStart) AS EstStart, 
					DATE(ADDDATE(Task.EstStart, INTERVAL IF(Task.EstDay <= 0, 0, Task.EstDay) Day)) AS EstEnd, 
					Task.EstDay AS Duration
				FROM
					Task) AS Task					
			JOIN
				Task_Type
			ON
				(Task.TypeCode = Task_Type.Code)
	
			JOIN
				Project
			ON
				(Task.ProjectIDX = Project.IDX)
	
			LEFT OUTER JOIN
				(SELECT
					Take.TakeId, 
					Take.TakeCode, 
					Take_Stat.Name AS TaskStatus, 
					Take.TaskId, 
					COUNT(Take.TakeId) AS TakeCount 
				FROM
					(SELECT 
						IDX AS TakeId,
						Code AS TakeCode,
						Parent4 AS TaskId,
						StatCode
					FROM 
						Take 
					ORDER BY TakeId DESC) AS Take
				LEFT OUTER JOIN
					Take_Stat 
				ON 
					(Take.StatCode = Take_Stat.Code) 
				GROUP BY 
					TaskId
				ORDER BY 
					TaskId) AS TaskStatus	
			ON
				(Task.IDX = TaskStatus.TaskId)

			WHERE 
				AssignUser = '%s' AND
				EstStart >= DATE('%s') AND EstStart <= DATE('%s') AND
				EstEnd >= DATE('%s') AND EstEnd <= DATE('%s') 
			
			ORDER BY
				EstStart,
				ProjectName,
				TaskName,
				TaskElement
		""" % (request.params["UserID"], start_date, end_date, start_date, end_date));

		r = [];
		index = 1;
		
		s = start_date;
		
		#for task in tasks:
		while (True):
		
			task = tasks.fetchone();
			
			if (task is None):
				break;
				
			#작업들 사이에 빈 날짜 데이터를 원하는 경우 추가시킨다.
			if (append_no_work_day):
				#빈 날짜를 계산한다.
				d = (task.EstStart - s).days; 
				#빈 날짜가 1일이라도 있다면
				if (d > 0):
					r.append({
						"TaskNo" : index,
						"IsNoWorkPeriod" : True,
						"ProjectName" : u"(작업 없는 기간)",
						"EstStart" : s,
						"EstEnd" : (task.EstStart - timedelta(1)),
						"Duration" : d
					});
					
					index += 1;
					
				if (s <= task.EstEnd):
					s = task.EstEnd + timedelta(1);
					
			
			#작업 정보를 결과에 포함시키길 원하는 경우 추가시킨다.
			if (append_task):
				r.append({
					"TaskNo" : index,
					"TaskIDX" : task.TaskIDX,
					"ProjectName" : task.ProjectName,
					"TaskName" : task.TaskName,
					"TaskElement" : task.TaskElement,
					"TaskStatus" : task.TaskStatus,
					"EstStart" : task.EstStart,
					"EstEnd" : task.EstEnd,
					"Duration" : task.Duration,
					"TakeCount" : task.TakeCount
				});
				
				index += 1;
				
				
		#마지막 작업 이후에 빈 날짜를 계산한다.
		d = (end_date - s).days+1; 
		
		#빈 날짜가 1일이라도 있다면
		if (d > 0):
			r.append({
				"TaskNo" : index,
				"IsNoWorkPeriod" : True,
				"ProjectName" : u"(작업 없는 기간)",
				"EstStart" : s,
				"EstEnd" : end_date,
				"Duration" : d
			});
			
		return JSON(r);

	#입력된 테스크 아이디에 해당하는 테스크들의 정보를 리턴한다. (RowProxy 객체를 리턴한다)
	def get_task_list(self, task_ids, cond=None):
	
		#필터링 조건이 주어진 경우, SQL 문에 있는 조건문과 연결하기 위해서 'AND'를 넣는다.
		if (cond <> None and cond <> u""):
			cond += u" AND ";
		else:
			cond = u"";
			
		#테스크 정보와 사용자 정보를 함께 모은다.
		return RunSql(u"""
			SELECT
				Task.IDX,
				Project.Name AS ProjectName,
				Shot.Name AS ShotName,
				Task_Type.Name AS TaskTypeName,
				Task_Type.Code AS TaskTypeCode,
				Task.Element AS TaskElement,
				DATE(Task.EstStart) AS StartDate,
				DATE(DATE_ADD(Task.EstStart, interval EstDay day)) AS EndDate,
				Task.EstDay AS Duration,
				User.Name AS UserName,
				User.UserID AS UserID,
				Task.StatCode as TaskStatCode
			FROM
				Task
			JOIN
				Task_Type
			ON
				(Task.TypeCode = Task_Type.Code)
			LEFT OUTER JOIN
				User
			ON
				(Task.AssignUser = User.UserID)
			JOIN
				Project
			ON
				(Task.Parent1 = Project.IDX)
			JOIN
				Shot
			ON
				(Task.Parent3 = Shot.IDX)

			WHERE
				%s
				Task.IDX IN(%s)
			""" % (cond, ",".join(map(str, task_ids))));


	#특정 사용자들이 처리할 수 있는 작업 종류에 대한 정보를 리턴하는 메서드
	def get_user_roles(self, userid_list, cond=None):
		if (len(userid_list) <= 0):
			return None;
		
		#필터링 조건이 있다면
		if ((cond <> None) and (cond <> u"")):
			cond += u" AND ";
		else:
			cond = u"";
	
		#목록에 있는 사용자들에 관한 정보를 얻는다.
		return RunSql(u"""
			SELECT 
				User.IDX AS UserIdx, 
				User.UserID AS UserId, 
				User.Name AS UserName, 
				Task_Type.Name AS TaskTypeName,
				Task_Type.Code AS TaskTypeCode, 
				User_Team.Name AS UserTeam

			FROM
				User

			JOIN
				User_Team
			ON
				(User.TeamCode = User_Team.Code)

			LEFT OUTER JOIN
				Task_Type
			ON
				(User.TaskTypeCodes LIKE CONCAT('%%%%',Task_Type.Code,',%%%%') )
	
			WHERE
				%s
				User.UserID IN(%s)
			""" % (cond, ",".join("'"+v+"'" for v in userid_list)));


	#특정 사용자들이 처리할 수 있는 작업 종류에 대한 정보를 리턴하는 메서드
	def get_user_roles_as_dictionary(self, userid_list):
		if (len(userid_list) <= 0):
			return [];
	
		#목록에 있는 사용자들에 관한 정보를 얻는다.
		user_list = self.get_user_roles(userid_list);
			
		r = [];
		
		current_idx = -1;
		task_type_names = [];
		task_type_codes = [];
		
		for user in user_list:
			if (user.UserIdx <> current_idx):
			
				current_idx = user.UserIdx;
				
				#현재 사용자에 할당된 첫번째 작업 종류에 관한 정보로 데이터를 초기화한다.
				if (user.TaskTypeCode == None):
					task_type_names = [];
					task_type_codes = [];
				else:
					task_type_names = [user.TaskTypeName];
					task_type_codes = [user.TaskTypeCode];
					
				info = {"UserName" : user.UserName,
					"UserId" : user.UserId,
					"UserTeam" : user.UserTeam,
					"TaskTypeNames" : task_type_names,
					"TaskTypeCodes" : task_type_codes};
				r.append(info);
			else:
				if (user.TaskTypeCode <> None):
					task_type_names.append(user.TaskTypeName);
					task_type_codes.append(user.TaskTypeCode);
		
		return r;
		
	#파라메터로 입려된 사용자 아이디들에 대해서, 처리할 수 있는 작업 목록을 JSON 형태로 리턴하는 메서드
	def get_user_roles_as_json(self):
		#print "get_user_roles_as_json > params = ", request.params;
	
		if (not request.params.has_key("user_id_list")):
			return JSON([]);
			
		#파라메터에서 대상 사용자 아이디 목록을 구한다.
		user_ids = json.loads(request.params["user_id_list"]);
		
		return JSON(self.get_user_roles_as_dictionary(user_ids));

			
	#입력된 아이디의 사용자가 특정 작업을 수행할 수 있는지를 확인하는 메서드
	def check_user_can_do_task_of_type(self, user_id, task_type_code):
		return (self.get_user_roles([user_id], u"Task_Type.Code = '%s'" % task_type_code).rowcount > 0);

	#사전 객체를 이용해서 특정 사용자에게 보낼 메시지를 그룹화하는 메서드
	def push_msg_to_user(self, msg_dict, user_id, msg):		
		#대상 사용자에게 보낼 메시지 목록을 얻어서 추가한다.
		if (msg_dict.has_key(user_id) == False):
			user_messages = [];
			msg_dict[user_id] = user_messages;
		else:
			user_messages = msg_dict[user_id];
		
		#print msg;
		
		user_messages.append(msg);
		

	#테스크를 생성/삭제/변경하는 메서드
	def create_delete_update_task(self):
		#print "\n\ncreate_delete_update_task>>";
		#print "request.params", request.params;
		
		#대상 샷 아이디가 없는 경우, 오류를 리턴한다.
		if (not request.params.has_key("shot_id")):
			return Error(SPT_ERROR_PARAMETER_MISSING);
		
		#테스크를 추가하려는 대상 샷의 아이디를 얻는다.
		shot_id = request.params["shot_id"];
		#print "shot_id = ", shot_id;
		
		#샷 아이디가 빈 문자열로 넘어온 경우, 오류를 리턴한다.
		if (shot_id == u""):
			#print "shot id is empty";
			return Error(SPT_ERROR_PARAMETER_INVALID);
		
		#대상 샷을 찾는다.
		data = RunSql(u"""
			SELECT
				*
			FROM
				Shot
			WHERE
				IDX = %s
			""" % (shot_id));
			
		#대상 샷을 찾을 수 없는 경우, 오류를 리턴한다.
		if (data.rowcount <= 0):
			#print "no such shot (%s) exist" % (shot_id);
			return Error(SPT_ERROR_PARAMETER_INVALID);
		
		#샷 정보를 얻는다.
		shot = data.fetchone();
		
		#리턴할 데이터를 저장하는 배열 객체들
		deleted_task_ids = [];
		created_task_ids = [];
		updated_task_ids = [];
		
		#각 사용자별로 전송할 메시지 목록
		messages = {};
		
		#print u"샷 아이디=[%d], 시퀀스=[%d], 프로젝트=[%d], 썸네일=[%s]" % (shot.IDX, shot.Parent2, shot.Parent1, shot.Thumb);
		
		#생성할 테스크가 주어진 경우
		if (request.params.has_key("task_to_create")):
			#print "생성할 테스크 : ", request.params["task_to_create"];
			
			#파라메터로 넘어온 생성할 테스크에 관한 JSON 데이터를 파이썬 객체로 만든다.
			task_list = json.loads(request.params["task_to_create"]);
			
			if (len(task_list) > 0):
				#작업자가 배정된 테스크의 아이디 목록을 저장할 객체. 해당 작업자들에게 작업이 배정되었음을 알리는 쪽지를 보내기 위해서 사용한다.
				task_ids_to_notify = [];
			
				#추가시킬 작업들을 데이터베이스에 등록한다.
				for task in task_list:
			
					#테스크로 등록하기 위한 최소한의 정보가 있는지 확인한다. 정보가 부족한 경우 다음 작업을 처리한다.
					if (not(task.has_key("TaskTypeCode") and task.has_key("Duration"))):
						continue;

					#테스크 타입 코드가 없으면, 테스크로 추가할 수 없기 때문에 처리하지 않는다.					
					if (task.has_key("TaskTypeCode") == u""):
						continue;
					
					#print u"task.Type=[%s], element=[%s], day=[%d]" % (task["TaskTypeCode"], task["TaskElement"], task["Duration"]);
				
					try:
						# 태스크를 추가한다.
						RunSql(u"""
							INSERT INTO
								Task (Name, StatCode, TypeCode, Element, EstStart, EstDay, Parent1, Parent2, Parent3, Thumb, AssignUser)
							VALUES ('%s', 'RDY', '%s', '%s', IF('%s' = '', null, DATE('%s')), %d, %d, %d, %d, '%s', '%s')
						""" % (
						u"%s_%s_%s" % (shot.Name, task["TaskTypeCode"], task["TaskElement"]),
						task["TaskTypeCode"], task["TaskElement"], task["StartDate"], task["StartDate"], task["Duration"], 
						shot.Parent1, shot.Parent2, shot.IDX, shot.Thumb,
						task["UserId"]));
					
						#생성된 테스크의 키값을 얻는다.
						created_task_id = RunSql(u"SELECT LAST_INSERT_ID();").fetchone()[0];
					
						#print u"create task id = [%d]" % (created_task_id);
					
						#생성된 테스크의 키 값을 저장한다.
						created_task_ids.append(created_task_id);
					
						#생성된 테스크에 작업자가 배정되어 있다면 해당 작업자의 아이디를 저장한다.
						if (task["UserId"] <> u""):
							task_ids_to_notify.append(created_task_id);
					
					except Exception as e:
						#오류를 출력한다.
						#print "ERROR >>>>>\n\n\n";
						#print e;
						pass

				#생성된 테스크들 중에서 작업자가 배정된 테스크가 있다면
				if (len(task_ids_to_notify) > 0):
				
					#테스크 정보와 사용자 정보를 함께 모은다.
					user_and_task = self.get_task_list(task_ids_to_notify, u"Task.AssignUser is not null");
					
					#각 사용자들에게 새로 배정된 테스크에 대한 정보를 쪽지로 보낸다.
					for info in user_and_task:
						msg = u"[%s] 프로젝트의 [%s] 샷에서 [%s(%s)] 작업이 %s 님에게 배정되었습니다. " % (info.ProjectName, info.ShotName, info.TaskTypeName, info.TaskElement, info.UserName);
						if (info.StartDate <> None):
							msg += u"작업 일정은 [%s] 에 시작해서 [%d] 일 동안입니다." % (str(info.StartDate), info.Duration);
						
						#대상 사용자에게 보낼 메시지 추가한다.
						self.push_msg_to_user(messages, info.UserId, msg);
									
									
									
		#삭제할 테스크가 주어진 경우
		if (request.params.has_key("task_to_delete")):
			#print "삭제할 테스크 : ", request.params["task_to_delete"];
			
			#삭제할 테스크 아이디 목록을 파이썬 객체로 만든다.
			task_id_list = json.loads(request.params["task_to_delete"]);
				
			if (len(task_id_list) > 0):
				#테스크를 삭제하기 전에 작업자가 배정된 테스크에 대한 정보를 모은다.
				user_and_task = self.get_task_list(task_id_list, u"Task.AssignUser is not null");
					
				for task_id in task_id_list:
					#print "task [%d]" % (task_id);

					# 태스크의 확장 속성을 제거한다.
					r1 = RunSql(u"DELETE FROM Task_Attr WHERE ParentIDX = %d" % (task_id))
				
					#태스크를 삭제한다.
					r = RunSql(u"DELETE FROM Task WHERE IDX = %d" % (task_id));
				
					#태스크가 정상적으로 삭제되었는지 확인한다.
					if (r.rowcount == 1):
						#print "=> DELETED";
					
						#삭제된 태스크의 키 값을 저장한다.
						deleted_task_ids.append(task_id);

				#삭제된 테스크를 배정 받은 사용자가 있을 경우
				if (user_and_task.rowcount > 0):
			
					#각 사용자들에게 새로 배정된 테스크에 대한 정보를 쪽지로 보낸다.
					for info in user_and_task:
					
						#실제 삭제된 작업에 대해서만 처리한다.
						if (info.IDX in deleted_task_ids):
							msg = u"%s 님에게 배정되었던 [%s] 프로젝트의 [%s] 샷에서 [%s(%s)] 작업이 삭제되었습니다." % (info.UserName, info.ProjectName, info.ShotName, info.TaskTypeName, info.TaskElement);
							
							#대상 사용자에게 보낼 메시지 추가한다.
							self.push_msg_to_user(messages, info.UserId, msg);
						
						
			
		#갱신할 테스크가 주어진 경우
		if (request.params.has_key("task_to_update")):
			#print "갱신할 테스크 : ", request.params["task_to_update"];
	
			#파라메터로 넘어온 테스크에 관한 JSON 데이터를 파이썬 객체로 만든다.
			task_list = json.loads(request.params["task_to_update"]);
			
			if (len(task_list) > 0):
				#갱신할 테스크에 대한 정보를 모은다.
				user_and_task = self.get_task_list([v["TaskId"] for v in task_list])
				
				for task in user_and_task:
					info = None;
					for t in task_list:
						if (t["TaskId"] == task.IDX):
							info = t;
							break;
					
					if (info == None):
						continue
					
					# 변경을 가하고자 하는 태스크가 선택된 이후 수정해달라고 인자로 넘어온 Task의 StartDate가
					# 서버의 시간보다 이전이면 변경을 취소한다.(날짜가 들어오지 않은 경우도 있을 수 있다.)
					if len(info["StartDate"]) > 0:
						task_start_date = convertDate(info["StartDate"])
						current_date = date.fromtimestamp(modtime.time())
						
						if task_start_date < current_date:
							continue
					
					# Task의 상태가 CRQ거나 DON인 경우는 변경을 취소한다.
					# CRQ : Confirm RQ
					# DON : Done
					if task.TaskStatCode in ( "CRQ", "DON"):
						continue
					
					user_id = info["UserId"];
					task_type_code = info["TaskTypeCode"];
					
					sql = u"UPDATE Task SET TypeCode='%s', Element='%s'"%(task_type_code, info["TaskElement"])
					
					#작업 종류 코드가 변경된 경우
					if ((task.TaskTypeCode <> task_type_code) and (user_id <> u"")):
						
						#변경된 작업 종류를 작업자가 수행할 수 있는지 검사한다.
						if (self.check_user_can_do_task_of_type(user_id, task_type_code) == False):
							user_id = None
					
					#작업자의 아이디를 추가한다.
					sql += u", AssignUser="
					
					if ((user_id == None) or (user_id == u"")):
						sql += u"null"
					else:
						sql += u"'%s'" % user_id
					
					#시작날짜가 올바른 날짜 타입일 경우, 시작날짜를 추가한다.
					if (info["StartDate"] <> u""):
						sql += u", EstStart=DATE('%s')" % (info["StartDate"])
						
					#작업 기간을 추가한다.
					sql += u", EstDay=%d WHERE IDX=%d" % (info["Duration"], info["TaskId"])
					
					##############################################################################################
					# 앞뒤로 따라 움직이는 녀석들에 대한 일정 조정
					##############################################################################################
					
					# 일정 조정이 앞으로 옮기는 건지, 뒤로 옮기는 건지 세팅한다.
					sModifyDateIdentifier = str()
					
					# 여기에서 키 조합에 따라서 일정 변경을 하게 한다.
					if "no" == info.get("IsCtrl", "no"):
						# Ctrl이 눌려지지 않았으면 여러개 일정을 수정해야 한다.
						# RowProxy 객체와 Info 객체를 비교해서 RoxProxy 객체 값보다 Info 객체 시작값이
						# 앞서면 일정을 앞당기는 것이고 RoxProxy 객체값보다 Info 객체값이 늦으면 일정을 뒤로 늦추는 것이다.
						
						if len(info["StartDate"]) < 0:
							requestModifyDate = convertDate(info["StartDate"])
							
							# 변경을 요하는 날짜가 원본 태스크의 날짜보다 이전이면 일정을 앞당기는 것으로 판단한다.
							if requestModifyDate < task.StartDate:
								sModifyDateIdentifier = "task_date_modify_prev"
							# 변경을 요하는 날짜가 원본 태스크의 날짜보다 이후면 일정을 뒤로 미루는 것으로 판단한다.
							elif requestModifyDate > task.StartDate:
								sModifyDateIdentifier = "task_date_modify_next"
						else:
							# 아무 행동도 수행하지 않게 한다.
							sModifyDateIdentifier = ""
						
					if "task_date_modify_prev" == sModifyDateIdentifier:
						# 기능 : Task 일정을 앞으로 당긴다.
						
						# 변경을 요청받은 Task를 기준으로 같은 샷 안의 변경 요청 받은 샷 앞(시작일자)에 있는 태스크를 가져온다.
						change_old_tasks = getTaskFromTask(info["TaskId"], "prev")
						
						if None != change_old_tasks:
							all_row = change_old_tasks.fetchall()
							
							# 변경을 요청받은 태스크의 예전 시작일자에서 새로운 시작일자의 차를 구한다.
							start_diff_end_days = (task.StartDate - convertDate(info["StartDate"])).days
							# current_prev_task_move 플래그를 위해 start_diff_end_days 값을 보존한다.
							current_prev_task_move_days = abs(start_diff_end_days)
							
							affect_ids = [row["idx"] for row in all_row]
							
							# 변경 요청받은 태스크 이전 태스크의 종료일자를 키로 해서 레코드를 담아둔다.
							end_dt_dic = dict()
							[end_dt_dic.update({row["EndDate"]:row}) for row in all_row]
							
							# 변경 요청을 받은 뒤로 가장 먼저 시작되는 Task
							if len(affect_ids) > 0:
								max_end_date = max(end_dt_dic)
								
								# 변경을 통보받은 태스크의 시작일자에서 앞으로 가장 늦게 끝하는 태스크의 종료일자를 뺀 값을 구한다.
								orderedTask_modifyTask_date = (task.StartDate - end_dt_dic[max_end_date]["EndDate"]).days
								
								start_diff_end_days = orderedTask_modifyTask_date - start_diff_end_days
								
								# 변경 통보 태스크와 바로 앞 태스크 사이값이 0(0 포함)보다 크면 앞쪽 작업은 이동하지 말아야 한다.
								if start_diff_end_days >= 0:
									# current_date_ids를 축적하는 부분을 무시하기 위해서 사용한다.
									start_diff_end_days = 0
									affect_ids[:] = []
								else:
									# 간격을 제외한 값이 음수로 나올 경우 값을 이전의 태스크는 옮겨져야 한다. 따라서 음수의 절대값을 구한다.
									start_diff_end_days = abs(start_diff_end_days)
								
								# 간격 변수 : orderedTask_modifyTask_date
								
							# 변경일을 기준으로 더 앞으로 가는건 필터링된다.
							current_date_ids = []
							
							# 변경할 태스크의 IDX 만 얻어온다
							for row in all_row:
								# 변경을 통보받은 태스크는 이전 대상이 아니다.(물론 오늘 이전으로 가는 것도 안된다.)
								task_start_date = row["StartDate"] - timedelta(start_diff_end_days)
								
								if task_start_date <= current_date:
									# 일정을 조정했는데 조정된 범위가 시스템 날짜 이전으로 갈 경우 못 가게 한다.
									# 그리고 뒤에 오는 로직에서 별도로 일정을 시스템 날짜 기준으로 맞추게 한다.
									current_date_ids.append(row["IDX"])
									
									# 그리고 affect_ids에선 해당 태스크를 제거한다.(삭제하고자 하는 값이 없으면 ValueError를 발생시킨다)
									try:
										affect_ids.remove(row["IDX"])
									except:
										pass
							
							if len(affect_ids) > 0:
								# 앞 일정의 시작일과 종료일을 미룬다.
								sql2 = "update Task set EstStart=date_add(EstStart, interval '-%(day)d' day), EstDue=date_add(EstDue, interval '-%(day)d' day) where idx in (%(idx)s)" % \
									{
									"day" : start_diff_end_days,
									"idx": ",".join(map(str, affect_ids))
									}
								
								if (RunSql(sql2).rowcount <= 0):
									print "success"
							
							if len(current_date_ids) > 0:
								# 앞 일정의 시작일과 오늘 날짜로 변경한다.
								# 종료일은 계산해야 한다.
								sql2 = "update Task set EstStart=DATE('%(day)s'), EstDue=date_add(DATE('%(day)s'), interval EstDay day) where idx in (%(idx)s)" % \
									{
									"day" : current_date.strftime("%Y-%m-%d"),
									"idx": ",".join(map(str, current_date_ids))
									}
								
								if (RunSql(sql2).rowcount <= 0):
									print "success"
							
							# 태스크를 앞으로 이동할 경우 앞에 있는 태스크는 당연히 이동되어야 하나 뒤에 있는 태스크도 움직이여 할 수도 있다
							# 따라서 이 경우를 위해서 별도의 플래그를 두어 태스크를 이동하게 할지 결정한다.
							current_prev_task_move = False
							
							if current_prev_task_move:
								# 변경 요청을 받은 태스크 앞의 태스크는 당연히 이동하는데 변경 요청을 받은 태스크 뒤의 태스크들을 옮기게 한다.
								current_prev_task = getTaskFromTask(info["TaskId"], "next")
								current_prev_task_id = [row["IDX"] for row in current_prev_task]
								
								# 이렇게 구해진 태스크들도 변경을 요청받은 태스크가 앞으로 이동한 만큼 이전시킨다.
								
								# 태스크의 시작일은 변경을 요청받은 날짜만큼 이전하게 한다.
								# 종료일은 계산해야 한다.
								if len(current_prev_task_id) > 0:
									sql2 = "update Task set EstStart=date_add(EstStart, interval '-%(day)s' day) , EstDue=date_add(date_add(EstStart, interval '-%(day)s' day), interval EstDay day) where idx in (%(idx)s)" % \
										{
										"day" : current_prev_task_move_days,
										"idx": ",".join(map(str, current_prev_task_id))
										}
									
									if (RunSql(sql2).rowcount <= 0):
										print "success"
						
					elif "task_date_modify_next" == sModifyDateIdentifier:
						# 기능 : Task 일정을 뒤로 미룬다.
						
						# 변경을 요청받은 Task를 기준으로 같은 샷 안의 변경 요청 받은 샷 뒤(시작일자)에 있는 태스크를 가져온다.
						change_old_tasks = getTaskFromTask(info["TaskId"], "next")
						
						if None != change_old_tasks:
							all_row = change_old_tasks.fetchall()
							
							# 변경을 요청받은 태스크의 새로운 시작일자에서 예전 시작일자의 차를 구한다.
							end_diff_start_days = (convertDate(info["StartDate"]) - task.StartDate).days
							
							affect_ids = [row["idx"] for row in all_row]
							
							if len(affect_ids) > 0:
								# 변경 요청받은 태스크 이후 태스크의 시작일자를 키로 해서 레코드를 담아둔다.
								strt_dt_dic = dict()
								[strt_dt_dic.update({row["StartDate"]:row}) for row in all_row]
								
								# 변경 요청을 받은 뒤로 가장 먼저 시작되는 Task
								min_strt_date = min(strt_dt_dic)
								
								# 변경을 통보받은 태스크 뒤로 가장 먼저 시작하는 태스크의 시작일자에서 변경 태스크의 종료일자를 뺀 값을 구한다.
								modifyTask_orderedTask_date = (strt_dt_dic[min_strt_date]["StartDate"] - task.EndDate).days
								
								# 간격 변수 : modifyTask_orderedTask_date
								
								# 추가로 움직여야 하는 기간 계산
								end_diff_start_days = end_diff_start_days - modifyTask_orderedTask_date
								
								if (end_diff_start_days <= 0):
									affect_ids[:] = []
							
							if len(affect_ids) > 0:
								# 뒷 일정의 시작일과 종료일을 미룬다.
								sql2 = "update Task set EstStart=date_add(EstStart, interval '%(day)d' day), EstDue=date_add(EstDue, interval '%(day)d' day) where idx in (%(idx)s)" % \
									{
									"day" : end_diff_start_days,
									"idx": ",".join(map(str, affect_ids))
									}
								
								if (RunSql(sql2).rowcount <= 0):
									print "success"
					
					# 본 태스크 수정
					
					#print u"sql = [%s]" % sql;
					#정상적으로 갱신된 경우, 갱신된 테스크 아이디를 저장한다.
					try:
						if (RunSql(sql).rowcount <= 0):
							continue
					except Exception as e :
						print e
						continue
						pass
					
					#print u"==> APPLIED";
					updated_task_ids.append(info["TaskId"]);
					
					msg = u"";
					
					#작업자가 변경된 경우
					if (user_id <> task.UserId):
					
						#변경된 작업자의 아이디가 있는 경우, 새로 할당된 작업에 대한 정보를 보낸다.
						if (user_id <> u""):
							if ((task.UserId <> u"") and (task.UserId <> None)):
								msg = u"%s 님에게 배정되었던 " % (task.UserName);
							
							msg += u"[%s]프로젝트의 [%s]샷에서 [%s (%s)]작업이 [%s]님에게 배정되었습니다." % (task.ProjectName, task.ShotName, info["TaskTypeName"], info["TaskElement"], info["UserName"]);
						
							#대상 사용자에게 보낼 메시지 추가한다.
							self.push_msg_to_user(messages, user_id, msg);
						
						#이전에 작업자가 배정되었던 작업일 경우, 이전 작업자에게 작업이 다른 사람에게 배정되었음을 알리는 메시지를 보낸다.
						if ((task.UserId <> None) and (task.UserId <> u"")):
							msg = u"%s 님에게 배정되었던 [%s]프로젝트의 [%s]샷에서 [%s (%s)]작업이 " % (task.UserName, task.ProjectName, task.ShotName, task.TaskTypeName, task.TaskElement);
							
							#작업자 배정이 해제된 경우,
							if (user_id == u""):
								msg += u"배정 취소되었습니다.";
							#새로운 작업자에게 배정된 경우
							else:
								msg += u"[%s]님에게 배정되었습니다." % (info["UserName"]);
						
							#대상 사용자에게 보낼 메시지 추가한다.
							self.push_msg_to_user(messages, task.UserId, msg);
							
					#작업자는 변경되지 않고, 작업에 대한 정보만 변경된 경우							
					else:
						#기존에 배정된 작업자가 있었을 경우
						if (user_id <> u""):
							msg = u"%s 님에게 배정되었던 [%s]프로젝트의 [%s]샷에서 [%s (%s)]작업이 (시작일 = %s, 기간 = %d일) 다음과 같이 변경되었습니다.<br>" % (task.UserName, task.ProjectName, task.ShotName, task.TaskTypeName, task.TaskElement, str(task.StartDate), task.Duration);
							msg += u"[작업명 = %s (%s), 시작일 = %s, 기간 = %d일]" % (info["TaskTypeName"], info["TaskElement"], info["StartDate"], info["Duration"]);

							#대상 사용자에게 보낼 메시지 추가한다.
							self.push_msg_to_user(messages, user_id, msg);
								
						
					
		#사용자들에게 보낼 메시지가 있다면, 보낸다.		
		#2011-08-26
		#(중요!) 작업 정보 변경시 잦은 메시지 전송으로 인해서 불편하기 때문에 프로젝트 계획을 세우는 기능이 마려될때까지 메시지 보내는 기능을 잠시 막아둔다.		
		#if (len(messages) > 0):			
		#	for user_id in messages:
		#		SendMessageForUser(user_id, session["UserID"], u"작업 배정의 변경 사항 알려드립니다.", "<br><br>".join(messages[user_id]));

		#추가/삭제/변경된 테스크들의 아이디를 리턴한다.
		return JSON([{"created_task_ids" : created_task_ids}, {"deleted_task_ids" : deleted_task_ids}, {"updated_task_ids" : updated_task_ids}]);


	#모든 사용자의 암호를 입력된 문자열로 변경하는 메서드
	def change_all_password_to(self, id):
		pwd = hashlib.md5(id).hexdigest();
		
		r = RunSql(u"""
			UPDATE
				User
			SET
				Password = '%s'
			""" % (pwd));
		
		return JSON({"changed count" : r.rowcount});
		
	#특정 프로젝트의 샷들에 대한 상태별 통계를 리턴하는 메서드
	def get_shot_state_statics_of_project(self, id):
		sql_params = dict();
		sql_params["project_id"] = id;
	
		#샷 상태 목록을 구한다.
		shot_states = RunSql(u"""
			SELECT
				Code,
				Name AS ShotState,
				BGColor AS Color
			FROM
				Shot_Stat
			ORDER BY
				Name
			""");
			
		shot_statics_sql = u"""
			SELECT
				Parent1 AS ProjectID,
				COUNT(*) AS Total
			""";
			
		colors = dict();
		state_names = dict();
			
		for state in shot_states:
			shot_statics_sql += u",SUM(IF(StatCode = '%s', 1, 0)) AS '%s'\n" % (state.Code, state.Code);
			colors[state.Code] = state.Color if state.Color <> None else "red";
			state_names[state.Code] = state.ShotState;

		shot_statics_sql += u""" 
			FROM
				Shot
			WHERE
				Shot.Parent1 = %(project_id)s
			""" % sql_params;
		
		statics = RunSql(shot_statics_sql).fetchone();
		
		r = [];

		#상태별 정보를 리스트 형태로 만든다.		
		for key in statics.keys():
			if (key in ["ProjectID", "Total"]):
				continue;
				
			if (statics.Total == 0):
				ratio = "0.0";
			else:
				ratio = "%.1f" % (float(statics[key] / statics.Total) * 100.0);
				
			r.append({"Name" : state_names[key], "Code" : key, "Value" : statics[key], "Ratio" :  ratio, "Color" : colors[key]});
		
		return {"Total" : statics.Total, "Data" : r};

	#특정 프로젝트의 샷들에 대한 상태별 통계를 JSON 형태로 리턴하는 메서드
	def get_shot_state_statics_of_project_as_json(self, id):
		return JSON(self.get_shot_state_statics_of_project(id));

	
	#프로젝트의 샷별 상태에 대한 통계를 파이 차트 형태의 이미지로 리턴하는 메서드
	def get_shot_state_statics_image_of_project(self, id):
		theme.use_color = True;
		theme.reinitialize();
	
		response.headers['Content-Type'] = 'image/png'
		can = canvas.init(response, format = "png")

		#프로젝트에 대한 샷 상태별 통계값을 얻는다.
		statics = self.get_shot_state_statics_of_project(id);
		
		#차트에 사용될 데이터를 저장할 배열
		data = list();
		
		#차트를 그릴때 사용될 칠하기 스타일을 저장할 배열
		data_fill_styles = list();
		
		for n in statics["Data"]:
			data.append((n["Name"], n["Value"]));
			c = ColorStringToRGB(n["Color"]);
			data_fill_styles.append(fill_style.Plain(bgcolor=color.T(r=c[0], g=c[1], b=c[2])));

		ar = area.T(size=(300, 300), legend=None,
				x_grid_style = None, y_grid_style = None)

		plot = pie_plot.T(data=data, arc_offsets=[0,10,0,10],
				  #shadow = (4, -4, fill_style.gray50),
				  fill_styles = data_fill_styles,
				  label_offset = 25,
				  arrow_style = arrow.a3)
		ar.add_plot(plot)
		ar.draw()

		can.close()
		
	def get_shots_for_request_confirm(self, id):
		#print "parameters ===>",request.params;
		
		#SQL 실행을 위해서 데이터베이스에 연결을한다.
		conn = Base.metadata.bind.connect();
	
		#SQL 파라메터를 저장하는 사전
		sql_params = dict();
		
		#샷 아이디
		sql_params["project_id"] = id;
		
		#샷을 페이징하기 위한 LIMIT 문
		sql_params["shot_page_filter"] = u"";
		
		#페이징 요청이 있는지 확인한다.
		if (request.params.has_key("start") and request.params.has_key("limit")):
			sql_params["shot_page_filter"] = u" LIMIT %s, %s " % (request.params["start"], request.params["limit"]); 

		#샷을 필터링하기 위한 조건들을 저장할 리스트 객체를 생성한다.
		shot_filters = list();
		
		#테이크를 필터링하기 위한 조건들을 저장할 리스트 객체를 생성한다.
		take_filters = list();
		
		#정렬 조건들을 저장할 리스트 객체를 생성한다.
		ordering = list();
		

		#특정 샷만을 원할 경우,
		if (request.params.has_key("shot_id_list")):
			shot_list = json.loads(request.params["shot_id_list"]);
			shot_filters.append(u"Shot.IDX IN(%s) " % (",".join(map(str, shot_list))));
		else:
			#컨펌 요청이 있는 테이크를 포함하는 샷을 원하는 경우,
			if (request.params.has_key("start_date") and request.params.has_key("end_date")):
			
				#특정 기간에 컨펌 요청이 있는 테이크만을 원할 경우
				if ((request.params["start_date"] <> "-") and (request.params["end_date"] <> "-")):
					take_filters.append(u"DATE(CreateDate) BETWEEN DATE('%(start_date)s') AND DATE('%(end_date)s')" % request.params);
					
				shot_filters.append(u"ConfirmRequestCount > 0");
				ordering.append(u"ConfirmRequestDate DESC");
				
			#특정 상태의 샷만을 원할 경우,
			elif (request.params.has_key("shot_state_list")):
				shot_state_list = json.loads(request.params["shot_state_list"]);
				if (len(shot_state_list) > 0):
					shot_filters.append(u"Shot.StatCode IN('%s')" % ("','".join(map(str, shot_state_list))));

			

		#샷 필터링 조건이 하나라도 있으면, 기존에 있는 WHERE 절의 조건과 합치기 위해서 중간에 AND 를 붙인다.
		sql_params["shot_filter"] = u" AND "+u" AND ".join(shot_filters) if (len(shot_filters) > 0) else u"";
		
		#테이크를 필터링하기 위한 SQL 조건문을 만든다.
		sql_params["take_filter"] = u" AND "+u" AND ".join(take_filters) if (len(take_filters) > 0) else u"";
		
		#정렬을 하기 위한 SQL 조건문을 만든다.
		sql_params["ordering"] = u" , "+u" , ".join(ordering) if (len(ordering) > 0) else u"";
		
		#컨펌 대상 샷 목록을 얻기 위한 SQL을 만든다.
		sql = u"""
			SELECT SQL_CALC_FOUND_ROWS
				Shot.IDX AS ShotId,
				Shot.Name AS ShotName,
				Shot.Thumb,
				Shot.Content,
				Shot.Roll,
				Shot.Scene,
				Shot.Duration,
				Shot.Note,
				Shot.StatCode AS StatusCode,
				Shot_Stat.Name AS Status,
				Shot.TypeCode,
				Shot_Type.Name AS ShotTypeName,
				TakeStatus.ConfirmRequestCount,
				TakeStatus.ConfirmRequestDate
			FROM
				Shot
			JOIN
				Shot_Stat
			ON
				(Shot.StatCode = Shot_Stat.Code)
			JOIN
				Shot_Type
			ON
				(Shot.TypeCode = Shot_Type.Code)
			LEFT OUTER JOIN
				(SELECT 
					Parent3 AS ShotId, 
					COUNT(*) AS ConfirmRequestCount, 
					MIN(CreateDate) AS ConfirmRequestDate 
				FROM 
					Take 
				WHERE 
					StatCode = 'RDY' AND
					Parent1 = %(project_id)s
					%(take_filter)s
				GROUP BY 
					Parent3) AS TakeStatus
			ON
				(Shot.IDX = TakeStatus.ShotId)
			WHERE
				Shot.Parent1 = %(project_id)s 
				%(shot_filter)s
			ORDER BY
				Shot.Name
				%(ordering)s
			%(shot_page_filter)s				
			""" % sql_params;
			
			
		#print "shot_sql = \n", sql;
		
		#컨펌 대상 샷 목록을 구한다.
		shot_list = RunSql(sql, conn);
		
		#LIMIT 를 하지 않았을때의 총 샷의 수를 얻는다.
		total_shot_count = RunSql(u"SELECT FOUND_ROWS() AS TotalRows", conn).fetchone().TotalRows;
		
		r = [];
		
		
		sql = u"""
			SELECT
				Task.Parent3 AS ShotId,
				Task_Type.Name AS TaskTypeName,
				Task.Element,
				TaskStatus.TakePreview,
				TaskStatus.TakeTypeName,
				TaskStatus.TakeStatus,
				TaskStatus.TakeStatusCode,
				TaskStatus.TakeContent,
				TaskStatus.TakeDate,
				TaskStatus.TakeId,
				Task.StatCode AS TaskStatusCode,
				Task_Stat.Name AS TaskStatus,
				User.UserName,
				User.Department,
				TaskStatus.TakeCount
			FROM
				(SELECT
					*
				FROM
					Task
				WHERE
					Task.Parent1 = %(project_id)s
				) AS Task					
			JOIN
				(SELECT
					*
				FROM
					Shot
				LEFT OUTER JOIN
					(SELECT 
						Parent3 AS ShotId, 
						COUNT(*) AS ConfirmRequestCount, 
						MIN(CreateDate) AS ConfirmRequestDate 
					FROM 
						Take 
					WHERE 
						StatCode = 'RDY' AND
						Parent1 = %(project_id)s
						%(take_filter)s
					GROUP BY 
						Parent3) AS TakeStatus
				ON
					(Shot.IDX = TakeStatus.ShotId)
				WHERE
					Shot.Parent1 = %(project_id)s 
					%(shot_filter)s
				ORDER BY
					Shot.Name
				%(shot_page_filter)s
				) AS Shot
			ON
				(Shot.IDX = Task.Parent3)
				
			JOIN
				Task_Type
			ON
				(Task.TypeCode = Task_Type.Code)
			JOIN
				Task_Stat
			ON
				(Task.StatCode = Task_Stat.Code)

			LEFT OUTER JOIN 
				(SELECT
					User.Name AS UserName,
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
					Take.TakeId, 
					Take.TakeCode, 
					Take_Type.Name AS TakeTypeName,
					Take_Stat.Name AS TakeStatus, 
					Take.StatCode AS TakeStatusCode,
					Take.TakeContent,
					Take.TakePreview,
					Take.TakeDate,
					Take.TaskId, 
					Take.TakeUserName,
					COUNT(Take.TakeId) as TakeCount,
					DATEDIFF(MAX(TakeDate), MIN(TakeDate))+1 AS Duration,
					DATE(MIN(TakeDate)) AS FirstTake
				FROM
					(SELECT 
						IDX as TakeId,
						Code as TakeCode,
						Parent4 as TaskId,
						TypeCode,
						StatCode,
						Preview AS TakePreview,
						Content AS TakeContent,
						CreateDate AS TakeDate,
						CreateBy2 AS TakeUserName
					FROM 
						Take 
					WHERE
						Parent1=%(project_id)s
					ORDER BY TakeId DESC) AS Take
				JOIN
					Take_Stat 
				ON 
					(Take.StatCode = Take_Stat.Code)
				JOIN
					Take_Type
				ON
					(Take.TypeCode = Take_Type.Code)
				GROUP BY 
					TaskId
				ORDER BY 
					TaskId) as TaskStatus				
	
			ON
				(Task.IDX = TaskStatus.TaskId)
				
			ORDER BY
				Shot.Name
			""" % sql_params;
			
		tasks = RunSql(sql);
		#print sql;
		need_fetch_task = True;
		task_index = 0;
		
		#print "take_sql = \n", sql;
		
		#컨펌 대상 샷을 결과 목록에 저장한다.
		for shot in shot_list:
			s = MakeDict(shot);
			r.append(s);
			
			shot_tasks = [];
			
			while True:
				if (task_index >= tasks.rowcount):
					break;
					
				#테스크 하나를 얻는다.
				if (need_fetch_task):
					task = tasks.fetchone();
					need_fetch_task = False;

				#태스크의 샷 아이디가 현재 샷의 아이디와 다를 경우, 현재 샷에 태스크를 추가하면
				#안되기 때문에 루프를 종료한다.
				if (task.ShotId <> shot.ShotId):
					break;

				t = MakeDict(task);
				
				#print t;
				shot_tasks.append(t);

				#다음 태스크를 얻기 위해서 인덱스를 1증가시킨다.
				task_index = task_index+1;
				need_fetch_task = True;
		
			#print str(dir(shot));
			
			s["Tasks"] = JSON(shot_tasks);
			
		conn.close();
		
		return JSON({"TotalRowCount" : total_shot_count, "Rows" : r});
		
		
	#테이크를 승인 또는 재작업 요청하는 메서드
	def confirm_or_reject_take(self):
		print "request.params = ", request.params;
		
		#컨펌인지 리테이크인지를 나타내는 파라메터가 빠져있다면 오류를 리턴한다.
		if ((request.params.has_key("is_confirm") == False) or (request.params.has_key("take_id") == False)):
			return Error(SPT_ERROR_PARAMETER_MISSING, "is_confirm or take_id is missing");
			
		take_id = request.params["take_id"];
		
		#컨펌 여부를 얻는다.
		is_confirm = (request.params["is_confirm"] == u"true");
		
		feedback = u"";
		#피드백 정보가 있을 경우 얻는다.
		if (request.params.has_key("feedback") == True):
			feedback = request.params["feedback"];
			
		#테이크를 승인 또는 재작업을 요청한다.
		error = ConfirmOrRejectTake(take_id, is_confirm, feedback, session["UserID"], True);
		
		if (error <> SPT_ERROR_NONE):
			return Error(error);
		
		return Success();
		
	def confirm_msg(self, id):
		#테이크에 대한 정보를 얻는다.
		take_info = GetTakeInfo(id);
		
		#테이크에 대한 피드백을 얻는다.
		take_feedbacks = GetFeedbackOfTake(id);
		
		msg = u"""
			%(ProjectName)s / %(ShotName)s / %(TaskTypeName)s(%(TaskElement)s) 작업이 승인되었습니다. <br><br><br>
		""" % (take_info);
		
		feedback_msg = u"<<피드백>>========================<br>";
		
		for f in take_feedbacks:
			feedback_msg += u"%(CreateDate)s - %(UserName)s(%(UserID)s) > %(Content)s <br>" % (f);
		
		
		
		return msg + feedback_msg;
	
	# Easy Task Add 추가
	def easy_task_id(self):
		# SQL 실행을 위해서 데이터베이스에 연결을한다.
		conn = Base.metadata.bind.connect();
		
		# TaskID Setting
		easy_task_id = request.params.get("easy_task_id")
		shot_id = request.params.get("shot_id")
		
		#샷 아이디가 빈 문자열로 넘어온 경우, 오류를 리턴한다.
		if (shot_id == u""):
			#print "shot id is empty";
			return Error(SPT_ERROR_PARAMETER_INVALID);
		
		#대상 샷을 찾는다.
		data = RunSql(u"""
			SELECT
				*
			FROM
				Shot
			WHERE
				IDX = %s
			""" % (shot_id));
			
		#대상 샷을 찾을 수 없는 경우, 오류를 리턴한다.
		if (data.rowcount <= 0):
			#print "no such shot (%s) exist" % (shot_id);
			return Error(SPT_ERROR_PARAMETER_INVALID);
		
		#샷 정보를 얻는다.
		shot = data.fetchone();
		
		# Task 목록을 불러와야 한다.
		TaskUnitProxy = RunSql("select * from Task_Unit where idx=%s" % easy_task_id, conn)
		TaskTypeProxy = RunSql("select Code, Name from Task_Type", conn)
		
		TaskTypeRef = {}
		for row in TaskTypeProxy:
			TaskTypeRef[row.Code] = row.Name
		
		easy_task_type_code = [row.unit_kind.split(",") for row in TaskUnitProxy]
		
		#print "%s_%s_00" % (shot.Name, TaskTypeRef[easy_task])
		
		# easy_task_type 만큼 데이터 반복
		for easy_task in easy_task_type_code[0]:
			# Task Table에 데이터를 추가하는 Insert Statement문을 위해 사전을 만들어놓는다.
			# 004_014_03_Removal_all_00
			InsData = {}
			InsData["Name"] = "%s_%s_00" % (shot.Name, TaskTypeRef[easy_task.strip()])
			InsData["TypeCode"] = easy_task.strip()
			InsData["Element"] = ""
			InsData["Parent3"] = shot_id
			InsData["Confirmer"] = ""
			
			result = Archive("Task").New(**InsData)
		
		return okMessage("추가 되었습니다")
