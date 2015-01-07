angular.module('app.controller', [])

.controller('MainCtrl', ['$scope', function($scope) {
}])

.controller('ChapterCtrl', ['$scope', function($scope) {

  var init = function() {
    d3.select("#ch5_1").append("p").text("New paragraph!");
  };

  init();
}]);