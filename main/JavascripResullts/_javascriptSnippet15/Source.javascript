angular.module('myModule').directive('user', function ($filter) {
  return {
    scope: {
      userId: '@'
    },
    link: function (scope, element, attrs) {
      $scope.connection = $resource('api.com/user/' + scope.userId);
    }
  };
});