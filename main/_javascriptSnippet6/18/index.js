var Chat = angular.module('chat',['ngCookies'],function($httpProvider)
{
  // Используем x-www-form-urlencoded Content-Type
  $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
 
  // Переопределяем дефолтный transformRequest в $http-сервисе
  $httpProvider.defaults.transformRequest = [function(data)
  {
    /**
     * рабочая лошадка; преобразует объект в x-www-form-urlencoded строку.
     * @param {Object} obj
     * @return {String}
     */ 
    var param = function(obj)
    {
      var query = '';
      var name, value, fullSubName, subValue, innerObj, i;
      
      for(name in obj)
      {
        value = obj[name];
        
        if(value instanceof Array)
        {
          for(i=0; i<value.length; ++i)
          {
            subValue = value[i];
            fullSubName = name + '[' + i + ']';
            innerObj = {};
            innerObj[fullSubName] = subValue;
            query += param(innerObj) + '&';
          }
        }
        else if(value instanceof Object)
        {
          for(subName in value)
          {
            subValue = value[subName];
            fullSubName = name + '[' + subName + ']';
            innerObj = {};
            innerObj[fullSubName] = subValue;
            query += param(innerObj) + '&';
          }
        }
        else if(value !== undefined && value !== null)
        {
          query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
        }
      }
      
      return query.length ? query.substr(0, query.length - 1) : query;
    };
    
    return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
  }];
});


Chat.config(function ($httpProvider) {
    $httpProvider.defaults.transformRequest = function(data){
        if (data === undefined) {
            return data;
        }
        return $.param(data);
    }

    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';
});

Chat.controller('mainCtrl',function($scope, $http, $cookieStore){

    $scope.showChatWindow = false;

    var ids;

    $scope.toggleChatView = function(){
        $scope.showChatWindow = !$scope.showChatWindow;
    }



        // var idcli = $cookieStore.get("idcli");

        // if(!$cookieStore.get("ids")){

        //     var authJSON =  

        //         {"func":"authorize", "params":  {"ip":"1.1.1.1", "idcli":idcli} };

        //     $http({
        //         url: 'http://evercom.oktell.com.ua/API/web-chat.php',
        //         method: "POST",
        //         data: authJSON,
        //         headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        //     })



        //     .success(function (data, status, headers, config) {

        //         $cookieStore.put("ids",data.ids);
        //         $cookieStore.put("idcli",data.idcli);

        //     })

        //     .error(function (data, status, headers, config) {

        //     });
        // }


    // $scope.showHistory = function(){
    //     return $scope.history.join('\n');
    // }

    $scope.history = [];
    var checkMes;


    var getCurrentDateTime = function(type){
        var dateTime = new Date();

        var h = dateTime.getHours();
        if(h<10) h = "0" + h;

        var m = dateTime.getMinutes();
        if(m<10) m = "0" + m;

        var s = dateTime.getSeconds();
        if(s<10) s = "0" + s;

        if(type == "time"){
            return h + ":" + m + ":" + s;
        }

        var y = dateTime.getFullYear();
        
        var mo = dateTime.getMonth() + 1;
        if(mo<10) mo = "0" + mo;

        var d = dateTime.getDate();
        if(d<10) d = "0" + d;

        if(type == 'dateTime'){
            return y + "-" + mo + "-" + d + " " + h + ":" + m + ":" + s;
        }

    }

    var createMessageObject = function(){
        var message = {};

        message.author = "Я";
        message.time = getCurrentDateTime('time');
        message.dateTime = getCurrentDateTime('dateTime'); 


        if($scope.content){
            message.content = $scope.content;
        }
        else return;

        return message;


    }

    var addMessageToHistory = function(message){
        $scope.history.push(message);
    }

    var clearMessageField = function(){
        $scope.content = "";
    }

    var sendMessageToServer = function(message){

        ids = 'test';
        var requestParams = {"func": "cli_msg", "msg": message.content, "ids": ids};       

        $http({
            url: 'http://evercom.oktell.com.ua/API/web-chat.php',
            method: "POST",
            data: requestParams,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })

        .success(function () {
            $scope.caller(message.dateTime);
            addMessageToHistory(message);
            clearMessageField();

        })
    }

    $scope.addMessage = function(){
        var message = createMessageObject();    
        if(message){
            sendMessageToServer(message);
        }   
    }

    $scope.caller = function(date_time){
        clearInterval(checkMes);
        checkMes = setInterval(function(){
            $scope.checkNewMessage(date_time);

        }
        , 2000);
    }



    $scope.checkNewMessage = function(date_time){

        ids = $cookieStore.get("ids");


        var mesJSON = {
            "func": "msg_mt",
            "ids": ids,
            "mt" : 'm',
            "dt" : date_time,
        }       


        $http({
            url: 'http://evercom.oktell.com.ua/API/web-chat.php',
            method: "POST",
            data: mesJSON,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        })



        .success(function (data, status, headers, config) {
            if(!data[0]){
            }

            if(data[0]){
                for(var i=0;i<data.length;i++){
                    var cur_obj = {};
                    cur_obj.time = data[i].dt.split(" ")[1];
                    cur_obj.author = "Менеджер";
                    cur_obj.content = data[i].msg;

                    $scope.history.push(cur_obj);

                    document.getElementById('scrolling').scrollTop = 9999999;       // Быдлокод, поменять

         
                    $scope.caller(data[i].dt);


                }
            }

        })

    }


});



Chat.directive('enter', function($document){
    return function(scope, element, attrs){
        element.bind('keydown',function(event){
            if(event.which==13)  {
                event.preventDefault();
                scope.$apply(scope.addMessage());
                document.getElementById('scrolling').scrollTop = 9999999;       // Быдлокод, поменять
            }

        })

    }
})

Chat.directive('draggable', function($document) {
        return function(scope, element, attr) {
            var startX = 0,
                startY = 0,
                x = $document.width() - element.parent().width(),
                y = $document.height() - element.parent().height();

            element.parent().css({
                height: element.parent().height()         //задаем высоту по факту, иначе нельзя
            });
            element.bind('mousedown', function(event) {
                // Prevent default dragging of selected content
                event.preventDefault();
                startX = event.screenX - x;
                startY = event.screenY - y;
                $document.bind('mousemove', mousemove);
                $document.bind('mouseup', mouseup);
            });

            function mousemove(event) {
                y = event.screenY - startY;
                x = event.screenX - startX;
                element.parent().css({
                    top: y + 'px',
                    left:  x + 'px'
                });
            }

            function mouseup() {
                $document.unbind('mousemove', mousemove);
                $document.unbind('mouseup', mouseup);
            }
        }
    });


/*
 AngularJS v1.2.0rc1
 (c) 2010-2012 Google, Inc. http://angularjs.org
 License: MIT
*/
(function(p,f,n){'use strict';f.module("ngCookies",["ng"]).factory("$cookies",["$rootScope","$browser",function(d,b){var c={},g={},h,k=!1,l=f.copy,m=f.isUndefined;b.addPollFn(function(){var a=b.cookies();h!=a&&(h=a,l(a,g),l(a,c),k&&d.$apply())})();k=!0;d.$watch(function(){var a,e,d;for(a in g)m(c[a])&&b.cookies(a,n);for(a in c)(e=c[a],f.isString(e))?e!==g[a]&&(b.cookies(a,e),d=!0):f.isDefined(g[a])?c[a]=g[a]:delete c[a];if(d)for(a in e=b.cookies(),c)c[a]!==e[a]&&(m(e[a])?delete c[a]:c[a]=e[a])});
return c}]).factory("$cookieStore",["$cookies",function(d){return{get:function(b){return(b=d[b])?f.fromJson(b):b},put:function(b,c){d[b]=f.toJson(c)},remove:function(b){delete d[b]}}}])})(window,window.angular);
/*
//@ sourceMappingURL=angular-cookies.min.js.map
*/
