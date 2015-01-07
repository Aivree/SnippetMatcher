angular.module('sunaApp').controller('HomeCtrl', ['$scope', '$rootScope',

function($scope, $rootScope) {

  var init = function() {
    $rootScope.currentPage = 'Home';
    $scope.ok = 'ok';
  };

  init();
}]);
