angular.module('starter.services', [])

.service('LoginService', function ($q, $http) {
  $http.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';

  return {
    login: function(email, password) {
      var defer = $q.defer(); 
      
      // replace timeout function with actual $http call
      // the $http call will return a promise equivelant to
      // defer.promise;

      var xsrf = { email: email, password: password };
      $http({
        method: 'POST',
        url: 'http://192.168.0.17:3001/api/user/signin',
        transformRequest: function(obj) {
          var str = [];
          for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
          return str.join("&");
        },
        data: xsrf
      }).success(function(data) {
        // presume data contains json {token: some token}
        defer.resolve(data);
      }).error(function(){
       defer.resolve();
      });      
      return defer.promise;
    }
  }  
})

.service('SignupService', function ($q, $http) {
  $http.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';

  return {
    login: function(name, email, password) {
      var defer = $q.defer(); 
    
      var xsrf = { name: name, email: email, password: password };
      $http({
        method: 'POST',
        url: 'http://192.168.0.17:3001/api/user/signup',
        transformRequest: function(obj) {
          var str = [];
          for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
          return str.join("&");
        },
        data: xsrf
      }).success(function(data) {
        defer.resolve(data);
      }).error(function(){
       defer.resolve();
      });      
      return defer.promise;
    }
  }  
})

.factory('CardsService', function ($q, $http) {
  return {
    getCards: function() {

      var defer = $q.defer(); 
      token = localStorage.getItem("token");
      $http({
        method: 'GET',
        url: 'http://192.168.0.17:3001/api/establishments/promotions/'+token+''
      }).success(function(data) {
        defer.resolve(data.promotions);

      }).error(function(){
       defer.resolve();
      });      
      return defer.promise;
    }
  };
})

.factory('CardDetailService', function ($q, $http) {
  $http.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
  token = localStorage.getItem("token");
  return {
    getDetails: function(promoId) {
      var defer = $q.defer(); 

      $http({
        method: 'GET',
        url: 'http://192.168.0.17:3001/api/promotions/'+promoId+''
      }).success(function(data) {
        defer.resolve(data);
      }).error(function(){
       defer.resolve();
      });      
      return defer.promise;
    },
    onHeart: function (promoId) {
      var defertwo = $q.defer(); 

      var xsrf = { token: token, promotion_id: promoId };
      $http({
        method: 'POST',
        url: 'http://192.168.0.17:3001/api/user/heart',
        transformRequest: function(obj) {
          var str = [];
          for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
          return str.join("&");
        },
        data: xsrf
      }).success(function(info) {
        defertwo.resolve(info);
      }).error(function(){
       defertwo.resolve();
      });      
      return defertwo.promise;
    },
    onPido: function (promoId) {
      var deferthree = $q.defer(); 

      var xsrf = { token: token, promotion_id: promoId };
      $http({
        method: 'POST',
        url: 'http://192.168.0.17:3001/api/establishment/client',
        transformRequest: function(obj) {
          var str = [];
          for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
          return str.join("&");
        },
        data: xsrf
      }).success(function(info) {
        deferthree.resolve(info);
      }).error(function(){
       deferthree.resolve();
      });      
      return deferthree.promise;
    },
    onFav: function (eId) {
      var deferfour = $q.defer(); 

      var xsrf = { token: token, establishment_id: eId };
      $http({
        method: 'POST',
        url: 'http://192.168.0.17:3001/api/user/favorite',
        transformRequest: function(obj) {
          var str = [];
          for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
          return str.join("&");
        },
        data: xsrf
      }).success(function(info) {
        deferfour.resolve(info);
      }).error(function(){
       deferfour.resolve();
      });      
      return deferfour.promise;
    }
  };
})

/**
 * A simple example service that returns some data.
 */
.factory('Friends', function() {
  // Might use a resource here that returns a JSON array

  // Some fake testing data
  var friends = [
    { id: 0, name: 'Scruff McGruff' },
    { id: 1, name: 'G.I. Joe' },
    { id: 2, name: 'Miss Frizzle' },
    { id: 3, name: 'Ash Ketchum' }
  ];

  return {
    all: function() {
      return friends;
    },
    get: function(friendId) {
      // Simple index lookup
      return friends[friendId];
    }
  }
});
