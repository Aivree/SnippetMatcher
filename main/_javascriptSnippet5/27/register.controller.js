(function() {
    'use strict';

    angular
        .module('core.register')
        .controller('RegisterCtrl', function($scope, $http, $location) {

    // function to submit the form after all validation has occurred      
$scope.submitForm = function() {

    console.log($scope.user.username)

  $http({
         url: 'http://localhost:8090/register',
         method: "POST",
         transformRequest: function(obj) {
        var str = [];
        for(var p in obj)
        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        return str.join("&");
          },
          data: {username:$scope.user.username, email:$scope.user.email, password:$scope.user.password},
          headers: {'Content-Type': 'application/x-www-form-urlencoded'}
         })
          .success(function(data) {
            $location.path('/thanks');
          })
    }
});


})();