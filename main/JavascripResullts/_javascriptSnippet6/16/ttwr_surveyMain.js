/**
 * Created with JetBrains PhpStorm.
 * User: hoho
 * Date: 13. 8. 22.
 * Time: 오후 2:38
 * To change this template use File | Settings | File Templates.
 */
ttwr.controller('SurveyCtrl',function($rootScope, $location, surveyREST,responsesREST, $scope, $routeParams, $dialog){
    $scope.surveyId = $routeParams.id;
    $scope.survey = {};
    $scope.triggerTargets = [];
    (function() {
        var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
        po.src = 'https://apis.google.com/js/plusone.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
    })();
    var getMain = function(){
        surveyREST.get({id:$scope.surveyId},function(data){
            console.log(data);

            if(data.survey == null){
                $location.path('/');
            }
            //
            $scope.responses= [];
            for(var i =0; i < data.survey.Questions.length; i++){
                if(data.survey.Questions[i].type==="CheckBox"){
                    $scope.responses.push(
                        {
                            questionId: data.survey.Questions[i].questionId,
                            answer : []
                        }
                    );
                }else{
                    $scope.responses.push(
                        {
                            questionId: data.survey.Questions[i].questionId,
                            answer : ""
                        }
                    );
                }
                if(data.survey.Questions[i].options.targetId == ""){
                    data.survey.Questions[i].disabled = false;
                }else{

                    var options = data.survey.Questions[i].options;
                    data.survey.Questions[i].options.targetQnum = data.survey.Questions.filter(function(question){return question.questionId == options.targetId;})[0].qNum;
                    options.questionId = data.survey.Questions[i].questionId;
                    $scope.triggerTargets.push(options);
                    data.survey.Questions[i].disabled = true;
                }
            }
            $scope.survey = data.survey;
            angular.element('html').find('meta[name=description]').attr('content', $scope.survey.Memo);
            angular.element('html').find('meta[name=title]').attr('content', $scope.survey.Title);
            angular.element('html').find('meta[name=url]').attr('content', 'http://ttwr-blog.herokuapp.com/post/'+$scope.surveyId);
            angular.element('html').find('meta[name=image]').attr('content', "http://ttwr-blog.herokuapp.com/images/icon100.png");

            $scope.ready();
        });
    };
    getMain();
    $scope.$watch('responses', function (responses) {
        //var taegetQuestion = $scope.survey.Questions.filter(function(question){return question.questionId == targetResponse[0].questionId;});

        for(var i=0, len = $scope.triggerTargets.length; i < len; i++){
            var targetResponse = responses.filter(function(response){return response.questionId === $scope.triggerTargets[i].targetId;});   //타켓의 응답
            //console.log(targetResponse);
            if(targetResponse){

                var triggedQuestion = $scope.survey.Questions.filter(function(question){return question.questionId == $scope.triggerTargets[i].questionId;}); //enabled disabled 되어야 할 퀘스쳔
                var trggedResponse = responses.filter(function(response){return response.questionId === $scope.triggerTargets[i].questionId;});
                //console.log("TriggedQuestion:"+triggedQuestion[0].title);
                //if(targetResponse[0].answer == $scope.triggerTargets[i].targetChoice){
                var isSame = false;

                //응답된 타겟의 앤스워 가 같으냐 다르냐
                if(angular.isArray(targetResponse[0].answer)){
                    isSame = targetResponse[0].answer.filter(function(answer){return answer == $scope.triggerTargets[i].targetChoice}).length>0?true:false;
                }else{
                    isSame =  targetResponse[0].answer == $scope.triggerTargets[i].targetChoice ?true:false;
                }
                if(isSame){
                    //console.log("Trigger On");
                    triggedQuestion[0].disabled = false;
                }else{
                    //console.log("not Trigged");
                    triggedQuestion[0].disabled = true;
                    trggedResponse[0].answer ="";
                }
            }
        }
    }, true);
    $scope.ngObjFixHack = function(ngObj) {
        var output;

        output = angular.toJson(ngObj);
        output = angular.fromJson(output);

        return output;
    }
    $scope.showFacebookShare = function(){
        var title = "[설문조사]"+$scope.survey.Title;
        //var summary = $scope.survey.Memo;
        var img = "http://ttwr-blog.herokuapp.com/images/icon.png";
        //var img = stripTags($scope.post,null).Img;
        var url = 'http://ttwr-blog.herokuapp.com/survey/'+$scope.surveyId;
        window.open(
            //'https://www.facebook.com/sharer/sharer.php?u='+encodeURIComponent('http://ttwr-blog.herokuapp.com/post/'+$scope.postId), &p[images][0]=
            'https://www.facebook.com/sharer/sharer.php?s=100&p[title]=TTWR PlayGround&p[summary]='+title+'&p[url]='+url+'&p[images][0]='+encodeURIComponent(img),
            'facebook-share-dialog',
            'width=626,height=436');
        return false;
    };
    $scope.showGoogleShare = function(){
        var url = 'http://ttwr-blog.herokuapp.com/survey/'+$scope.surveyId;
        window.open(
            'https://plus.google.com/share?url='+url,'',
            'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');
        return false;
    };
    $scope.responseSurvey = function(){

        //question이 enable인데 answer가 ""인 사람은 혼나야함
        var unAnswerdQuestionNumber = 0;
        for(var i = 0 , len = $scope.survey.Questions.length; i < len ; i++){
            if($scope.survey.Questions[i].disabled == false ){  //즉 응답이 있어야 하는데 없는 경우
                //$scope.survey.Questions[i]
                var answer = $scope.responses.filter(function(res){return res.questionId == $scope.survey.Questions[i].questionId})[0].answer;
                if(angular.isArray(answer)){
                    //다중 택 checkbox
                    if(answer.filter(function(answer){return answer.length > 0}).length==0){
                        //응답 안됨
                        unAnswerdQuestionNumber = $scope.survey.Questions[i].qNum;
                        break;
                    }
                }else{
                    if(answer == "" || answer == null){
                        unAnswerdQuestionNumber = $scope.survey.Questions[i].qNum;
                        break;
                    }
                }
            }
        }

        if(unAnswerdQuestionNumber > 0){ //0보다 크거나로 하고 싶으나 이전에 생성된 설문지는 qNum이 없으니까..
            //alert(unAnswerdQuestionNumber+"번 응답이 누락 되었습니다. 활성화 된 질문에 모두 응답해 주셔야 합니다.");


            $dialog.messageBox("Message", unAnswerdQuestionNumber+"번 응답이 누락 되었습니다. 활성화 된 질문에 모두 응답해 주셔야 합니다.", [{result:'ok', label: 'Okay', cssClass: 'btn-primary'}])
                .open().then(function(result){
                    //$location.path("/surveyList");
                });
            return false;
        }else{
            responsesREST.create(
                {},
                {"surveyId":$scope.survey._id, "Responses":$scope.ngObjFixHack($scope.responses)},
                function(data){
                    console.log(data);
                    if(data.result==="OK"){
                        //alert("설문에 답변해 주셔서 감사합니다.");
                        //$location.path("/surveyList");
                        $dialog.messageBox("Message", "설문에 답변해 주셔서 감사합니다.", [{result:'ok', label: 'Okay', cssClass: 'btn-primary'}])
                            .open().then(function(result){

                                $location.path("/surveyList");
                            });
                    }
            });
        }
        /*$http({
            url:'/responses/create'
            ,method:"POST"
            ,data: $.param({"surveyId":$scope.survey._id, "Responses":$scope.ngObjFixHack($scope.responses)})
            ,headers:{'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
        }).success(function(data){
                console.log(data);
                if(data.result==="OK"){
                    $dialog.messageBox("Message", "설문에 답변해 주셔서 감사합니다.", [{result:'ok', label: 'Okay', cssClass: 'btn-primary'}])
                        .open().then(function(result){
                            $location.path("/surveyList");
                        });
                }
            });*/

    };
});