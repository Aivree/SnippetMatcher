'use strict';

angular.module('mainServices', [ 'ngResource', 'commonServices' ])
  .factory('Notice', [ 'AppInfo', '$resource', function(AppInfo, $resource) {
    return $resource(AppInfo.api.baseUrl+'/'+AppInfo.appname+'/notice');
  }])
  .factory('ShareFunc', function(){
    return {
      kakaoTalk: function(data){
        kakao.link('talk').send({
          msg : data.title+'\n\n'+data.content+'\n\n웹으로 보기:'+data.currentUrl+'\n\n앱 설치하기: ',
          url : data.marketUrl,
          appid : data.appId,
          appver : '1.0',
          appname : data.appName,
          type : 'link'
        });
      },

      kakaoStory: function(data){
        kakao.link('story').send({
          post : '['+data.appName+'] '+data.title+'\n\n'+data.content+'\n\n웹으로 보기:'+data.currentUrl+'\n\n앱 설치하기: '+data.marketUrl,
          appid : data.appId,
          appver : '1.0',
          appname : data.appName,
          urlinfo : JSON.stringify({
            title: data.title,
            desc: data.content.substring(0,80)+'...',
            imageurl:[data.currentImage],
            type:'app'
          })
        });
      },

      twitter: function(data){
        window.location.href = 'https://twitter.com/intent/tweet?'+
          'original_referer='+encodeURIComponent(data.currentUrl)+
          '&text='+encodeURIComponent('['+data.appName+'] '+data.title+'\n'+data.content.replace(/\n/gi, ' ').substring(0,60))+'\n\n'+
          '&url='+encodeURIComponent(data.currentUrl);
      },

      facebook: function(data){
        window.location.href = 'http://www.facebook.com/sharer.php?m2w&s=100'+
          '&p[url]='+encodeURIComponent(data.currentUrl)+
          '&p[images][0]='+encodeURIComponent(data.currentImage)+
          '&p[title]='+data.title+
          '&p[summary]='+data.content;
      },

      postText : function(data){
        return '['+data.appName+'] '+data.title+'\n\n'+data.content+'\n\n앱 설치하기 : '+data.marketUrl;
      }
    };
  });

angular.module('hanasyServices', [ 'ngResource' ])
  .factory('Hanasy', [ 'AppInfo', '$resource', function(AppInfo, $resource) {
    return $resource(AppInfo.api.baseUrl+'/'+AppInfo.appname+'/hanasies/:uid/:sid/:action',
      {'uid':'@uid', 'sid':'@sid', 'action':'@action'},
      {
        'update': {method:'POST'},
        'upload': {method:'POST', params: {'uid':'@uid', 'sid':'@sid'}, headers:{ 'Content-Type': undefined }, transformRequest:angular.identity}
      }
    );
  }])
  .factory('HanasiesLoader', [ 'Hanasy', '$q', function(Hanasy, $q) {
    return function(hanasies) {
      var param = {
        status: hanasies.status,
        order: hanasies.order,
        limit: hanasies.paging.limit,
        cursor: hanasies.paging.cursor,
        channel: hanasies.channel,
        country: hanasies.country
      };

      if(hanasies.tag) {
        param.tag= hanasies.tag;
      }

      var delay = $q.defer();
      Hanasy.get(param, function(res) {
        delay.resolve(res);
      }, function(res) {
        delay.reject(res);
      });
      return delay.promise;
    };
  }]);

angular.module('partServices', [ 'ngResource' ])
  .factory('Part', [ 'AppInfo', '$resource', function(AppInfo, $resource) {
    return $resource(AppInfo.api.baseUrl+'/'+AppInfo.appname+'/hanasies/:uid/:sid/parts/:tid/:action',
      {'uid':'@uid', 'sid':'@sid', 'tid':'@tid', 'action':'@action'},
      {
        'update': {method:'POST'},
        'upload': {method:'POST', headers:{ 'Content-Type': undefined }, transformRequest:angular.identity}
      }
    );
  }])
  .factory('PartsLoader', [ 'Part', '$q', function(Part, $q) {
    return function(part) {
      var param = {
        order: part.order,
        uid: part.uid,
        sid: part.sid,
        limit: part.paging.limit,
        offset: part.paging.offset,
        direction: part.paging.direction,
        access_token: part.access_token,
        token_type: part.token_type
      };

      var delay = $q.defer();
      Part.get(param, function(res) {
        delay.resolve(res);
      }, function(res) {
        delay.reject(res);
      });
      return delay.promise;
    };
  }]);