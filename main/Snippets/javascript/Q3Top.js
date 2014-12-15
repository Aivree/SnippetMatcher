var app = angular.module('angularjs-starter', []);

app.controller('MainCtrl', function($scope) {

  $scope.init = function(name, id)
  {
    //This function is sort of private constructor for controller
    $scope.id = id;
    $scope.name = name;
    //Based on passed argument you can make a call to resource
    //and initialize more objects
    //$resource.getMeBond(007)
  };


});