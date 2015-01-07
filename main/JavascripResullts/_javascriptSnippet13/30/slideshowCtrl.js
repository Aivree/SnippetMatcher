var app = angular.module('SinglePageApp');

app.controller('SlideshowController', function($scope) {

  function init() {
    $scope.images = [];
    
    for(var i = 2; i < 10; i++) {
      $scope.images.push({ src: '../assets/images/'+i+'.jpg' });
    }
  }

  init();
});