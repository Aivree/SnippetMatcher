angular.module('vaitkus.controllers')
.controller('aboutCtrl', function($scope, $location) {
  //skrollr.init();
  
  $scope.navigate = function(path){
    $location.path("/app/" + path);
  };
});