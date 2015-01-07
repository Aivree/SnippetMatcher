'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
])
.config(['$routeProvider', function($routeProvider) {
  $routeProvider
    .when('/about', {
      controller: 'AppCtrl',
      templateUrl: 'views/about.html'
    })
    .when('/projects', {
      controller: 'AppCtrl',
      templateUrl: 'views/projects.html'
    })
    .when('/contact', {
      controller: 'AppCtrl',
      templateUrl: 'views/contact.html'
    })
    .otherwise({redirectTo: '/about'});
}])
.controller('AppCtrl', function($scope) {
})
.controller('ContactCtrl', function($scope, $http) {
  $scope.data = {};
  $scope.form_status = null;

  $scope.submit = function() {
    $scope.form_status = 'processing';
    $http({
      method: 'POST',
      url: 'email.php',
      data: $scope.data,
      transformRequest: function(obj) {
        var str = [];
        for(var p in obj)
        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        return str.join("&");
      },
      headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    })
    .success(function(data){
      $scope.form_status = 'success';
    })
    .error(function(data){
      $scope.form_status = 'error';
    });
  };
})
;
